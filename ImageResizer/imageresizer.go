//
// Copyright 2015 Google Inc. All Rights Reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//

package main

import (
	"bytes"
	"flag"
	"fmt"
	"io/ioutil"
	"log"
	"net/http"
	"time"

	"golang.org/x/net/context"
	"golang.org/x/oauth2"

	"runtime"
	"strings"

	"github.com/gographics/imagick/imagick"
	auth "github.com/google/google-api-go-client/oauth2/v2"
	"golang.org/x/oauth2/google"
	"google.golang.org/cloud"
	"google.golang.org/cloud/storage"
)

const (
	projectID     = "abelana-v2"
	outputBucket  = "abelanav2"
	pushURL       = "https://"+ projectID +".appspot.com/photopush/"
  listenAddress = "0.0.0.0:8080"
)

var (
	// map with the suffixes and sizes to generate
	// 0 indicates to conpute the value based on the other value provided,
	// or to keep the original size if both values are equal to 0
	sizes = map[string]struct{ x, y uint }{
		"thumbnail": {0, 300},
		"o": {0, 0},
	}

	ctx    context.Context
	client *http.Client
)

func main() {
	flag.Parse()
	runtime.GOMAXPROCS(runtime.NumCPU())

	client = &http.Client{Transport: &oauth2.Transport{
		Source: google.ComputeTokenSource(""),
	}}

	ctx = cloud.NewContext(projectID, client)

	http.HandleFunc("/healthcheck", func(http.ResponseWriter, *http.Request) {})
	http.HandleFunc("/", notificationHandler)
	log.Println("server listening on", listenAddress)

	if err := http.ListenAndServe(listenAddress, nil); err != nil {
		log.Fatal(err)
	}
}

func notificationHandler(w http.ResponseWriter, r *http.Request) {
	bucket, name := r.PostFormValue("bucket"), r.PostFormValue("name")
	if bucket == "" || name == "" {
		http.Error(w, "missing bucket or name", http.StatusBadRequest)
		return
	}

	if ok, err := authorized(r.Header.Get("Authorization")); !ok {
		if err != nil {
			log.Printf("authorize: %v", err)
		}
		http.Error(w, "you're not authorized", http.StatusForbidden)
		return
	}

	start := time.Now()
	defer func() { log.Printf("%v: processed in %v", name, time.Since(start)) }()

	if err := processImage(bucket, name); err != nil {
		log.Println(err.Error())
		http.Error(w, err.Error(), http.StatusInternalServerError)
		return
	}

	s := strings.Split(name, "_")

	if err := notifyDone(s[0]+ "_" + r.PostFormValue("task")); err != nil {
		log.Println(err.Error())
		http.Error(w, err.Error(), http.StatusInternalServerError)
	}
}

func authorized(token string) (ok bool, err error) {
	if fs := strings.Fields(token); len(fs) == 2 && fs[0] == "Bearer" {
		token = fs[1]
	} else {
		return false, nil
	}

	svc, err := auth.New(http.DefaultClient)
	if err != nil {
		return false, err
	}
	tok, err := svc.Tokeninfo().AccessToken(token).Do()
	s := strings.Split(tok.Email, "@")
	return err == nil && s[0] == projectID, err
}

func processImage(bucket, name string) error {
	r, err := storage.NewReader(ctx, bucket, name)
	if err != nil {
		return fmt.Errorf("storage reader: %v", err)
	}
	img, err := ioutil.ReadAll(r)
	r.Close()
	if err != nil {
		return fmt.Errorf("read image: %v", err)
	}

	wand := imagick.NewMagickWand()
	defer wand.Destroy()

	wand.ReadImageBlob(img)
	if err := wand.SetImageFormat("WEBP"); err != nil {
		return fmt.Errorf("set WEBP format: %v", err)
	}

	errc := make(chan error, len(sizes))
	for suffix, size := range sizes {
		go func(wand *imagick.MagickWand, suffix string, x, y uint) {
			errc <- func() error {
				defer wand.Destroy()

				width := x
				height := y
				if x == 0 && y > 0 {
					width = uint(float64(wand.GetImageWidth()) / float64(wand.GetImageHeight()) * float64(y))
				} else if x > 0 && y == 0 {
					height = uint(float64(wand.GetImageHeight()) / float64(wand.GetImageWidth()) * float64(x))
				} else if x== 0 && y == 0 {
					width = wand.GetImageWidth()
					height = wand.GetImageHeight()
				}

				if err := wand.AdaptiveResizeImage(width, height); err != nil {
					return fmt.Errorf("resize: %v", err)
				}

				target := name
				if sep := strings.LastIndex(target, "."); sep >= 0 {
					target = target[:sep]
				}
				s := suffix
				if suffix == "thumbnail" {
					s = ""
				} else {
					s = "_" + suffix
				}
				target = fmt.Sprintf("%s%s.webp", target, s)

				w := storage.NewWriter(ctx, outputBucket, target)
				w.ContentType = "image/webp"
				if _, err := w.Write(wand.GetImageBlob()); err != nil {
					return fmt.Errorf("new writer: %v", err)
				}
				if err := w.Close(); err != nil {
					return fmt.Errorf("close object writer: %v", err)
				}
				return nil
			}()
		}(wand.Clone(), suffix, size.x, size.y)
	}

	for _ = range sizes {
		if err := <-errc; err != nil {
			return err
		}
	}
	return nil
}

func notifyDone(name string) (err error) {
	req, err := http.NewRequest("POST", pushURL+name, &bytes.Buffer{})
	if err != nil {
		return err
	}
	res, err := client.Do(req)
	if err != nil {
		return fmt.Errorf("photo push: %v", err)
	}
	if res.StatusCode != http.StatusOK {
		return fmt.Errorf("photo push status: %v", res.Status)
	}
	return nil
}
