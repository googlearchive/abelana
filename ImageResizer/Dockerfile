FROM golang:1.4.2
MAINTAINER Francesc Campoy <campoy@google.com>

# Ignore APT warnings about not having a TTY
ENV DEBIAN_FRONTEND noninteractive

# install build essentials
RUN apt-get update && apt-get install -y wget build-essential pkg-config --no-install-recommends

# Install webp
RUN apt-get -q -y install libjpeg-dev libpng-dev libtiff-dev libgif-dev --no-install-recommends
RUN wget http://downloads.webmproject.org/releases/webp/libwebp-0.4.2.tar.gz && \
	tar xvzf libwebp-0.4.2.tar.gz && \
	cd libwebp-0.4.2 && \
	./configure && \
	make && make install

# install imagemagick 6.9.1-7
RUN cd && \
	wget http://www.imagemagick.org/download/ImageMagick-6.9.1-7.tar.gz && \
	tar xvzf ImageMagick-6.9.1-7.tar.gz && \
	cd ImageMagick-* && \
	./configure && \
	make && make install && \
	ldconfig /usr/local/lib

ADD Godeps/_workspace/ /go/
ADD imageresizer.go /go/src/github.com/GoogleCloudPlatform/abelana/imageresizer/imageresizer.go
RUN go install github.com/GoogleCloudPlatform/abelana/imageresizer && touch ~/logs
CMD /go/bin/imageresizer

EXPOSE 8080
