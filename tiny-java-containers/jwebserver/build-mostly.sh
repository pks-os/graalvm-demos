#!/bin/sh

native-image -Ob -H:+StaticExecutableWithDynamicLibC -m jdk.httpserver -o jwebserver.mostly

# Distroless Base (provides glibc)
docker build . -f Dockerfile.distroless-base.mostly -t jwebserver:distroless-base.mostly
