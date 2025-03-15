# syntax=docker/dockerfile:1
# https://docs.docker.com/reference/dockerfile/

#
# BSD-style license; for more info see http://pmd.sourceforge.net/license.html
#

# This Dockerfile doesn't need a build context.
# Build this locally via: "docker build --load -t pmd:latest - < Dockerfile"
# Run it with "docker run --rm --tty pmd:latest --version"

ARG JAVA_VERSION=21
ARG PMD_VERSION=7.11.0

FROM eclipse-temurin:${JAVA_VERSION}

# bring global args into scope
ARG JAVA_VERSION
ARG PMD_VERSION

RUN apt-get update && apt-get -y install unzip

RUN wget -O /pmd-dist-${PMD_VERSION}-bin.zip \
    https://github.com/pmd/pmd/releases/download/pmd_releases%2F${PMD_VERSION}/pmd-dist-${PMD_VERSION}-bin.zip

RUN unzip -d / /pmd-dist-${PMD_VERSION}-bin.zip \
    && rm /pmd-dist-${PMD_VERSION}-bin.zip \
    && mv /pmd-bin-${PMD_VERSION} /pmd-dist

# Create a custom Java runtime
RUN $JAVA_HOME/bin/jlink \
         --add-modules java.base,java.xml,java.desktop \
         --strip-debug \
         --no-man-pages \
         --no-header-files \
         --compress=2 \
         --output /javaruntime

RUN wget -O /openjfx.zip https://download2.gluonhq.com/openjfx/21.0.6/openjfx-21.0.6_linux-x64_bin-sdk.zip \
    && unzip -d / /openjfx.zip \
    && rm /openjfx.zip \
    && mv /javafx-sdk-21.0.6 /javafx-sdk

#FROM eclipse-temurin:${JAVA_VERSION}-alpine
#FROM alpine:3.21
#RUN apk add bash
FROM debian:buster-slim
ENV JAVA_HOME="/opt/java/openjdk"
ENV PMD_HOME="/opt/pmd"
ENV JAVAFX_HOME="/opt/java/openjfx"
ENV PATH="${JAVA_HOME}/bin:${PMD_HOME}/bin:${PATH}"
COPY --from=0 /javaruntime $JAVA_HOME
COPY --from=0 /javafx-sdk $JAVAFX_HOME
COPY --from=0 /pmd-dist $PMD_HOME

ENTRYPOINT ["pmd"]
