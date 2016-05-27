![status: inactive](https://img.shields.io/badge/status-inactive-red.svg)

This project is no longer actively developed or maintained.

For new work on this check out [this sample](https://cloud.google.com/solutions/mobile/mobile-compute-engine-grpc).

## Abelana v2

Abelana is a simple and beautiful cross-platform application allowing users to publish and view
photos.

Each user can see on the homepage of the application a photo stream where photos are ranked. Users
can vote on pictures (which affects ranking and hides photos they have down-voted), and set a
picture as their phone's wallpaper. They can upload their own photos for others to see, edit them
and delete them. Photos reported by at least two users will be hidden in everyone's feed. Sign-in is
possible using Google+/Facebook accounts, by leveraging the Google Identity Toolkit (GitKit).

The backend is a Java component exposing the APIs using the [gRPC framework](http://www.grpc.io/) and
running on Google Compute Engine in a Docker container, and a Go component also running on GCE in a
Docker container to resize the images.  Android and iOS clients are available.

See our other [Google Cloud Platform github repos](https://github.com/GoogleCloudPlatform) for
sample applications and scaffolding for other frameworks and use cases.

## WARNING - Abelana's image resizer uses [ImageMagick](http://www.imagemagick.org/) which has a recently discovered [exploit](https://imagetragick.com/).  Please add  [these lines](https://bugzilla.redhat.com/show_bug.cgi?id=1332492#c3) to your configuration file.

## Composition of the project

* Android is the Android client application, connecting to the gRPCserver.
* AppEngine is an App Engine component that will receive Google Cloud Storage notifications,
schedule image resizing tasks and update the database when photos are available.
* gRPCserver is the gRPC server handling all the requests from the clients to retrieve the data.
* ImageResizer is a Go component that takes an uploaded image in GCS and resizes it to various
sizes, in WebP.
* iOS is the iOS client application, connecting to the gRPCserver.

## Configure, deploy and run the sample

A [full tutorial on Abelana](https://cloud.google.com/solutions/mobile/image-management-mobile-apps-grpc)
 explaining how it works, how to set it up and deploy it on Google Cloud Platform is available on the
Google Cloud website.

**Note**: The mobile client implemented in the sample uses HTTP/2 without TLS/SSL security.

## Contributing changes

* See [CONTRIBUTING.md](CONTRIBUTING.md)

## Licensing

* See [LICENSE](LICENSE)
