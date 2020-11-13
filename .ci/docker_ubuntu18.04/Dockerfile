# https://hub.docker.com/_/ubuntu/
FROM ubuntu:18.04

ENV DEBIAN_FRONTEND=noninteractive
RUN apt-get update && apt-get upgrade --yes && \
    apt-get install --yes curl jq gpg rsync ssh git p7zip-full openjdk-8-jdk \
                      libgdbm-dev libncurses5-dev automake libtool bison libffi-dev \
                      sudo nano bash tzdata unzip zip && \
    apt-get clean
RUN cd opt && \
    curl https://mirror.checkdomain.de/apache/maven/maven-3/3.6.3/binaries/apache-maven-3.6.3-bin.tar.gz | tar xz && \
    ln -sf /opt/apache-maven-3.6.3/bin/mvn /usr/local/bin/mvn && \
    cd ..
RUN groupadd --gid 1000 pmd-ci && useradd --gid 1000 --uid 1000 --groups sudo \
             --shell /bin/bash --create-home --password "" \
             pmd-ci

RUN mkdir -p /workspaces/pmd/pmd && chown -R pmd-ci:pmd-ci /workspaces && ln -sf /workspaces /home/pmd-ci/workspaces

USER pmd-ci:pmd-ci
WORKDIR /home/pmd-ci
ENV GITHUB_EVENT_PATH=/workspaces/event.json
ENV GITHUB_REPOSITORY=pmd/pmd
ENV GITHUB_ACTION=run1
ENV GITHUB_EVENT_NAME=pull_request
ENV PMD_CI_BRANCH=master
ENV PMD_CI_PULL_REQUEST_NUMBER=2913
COPY create-gh-pull-request-event.sh .
COPY install-ruby.sh .
RUN ./install-ruby.sh
RUN ./create-gh-pull-request-event.sh

CMD ["/bin/bash", "--login"]

#
# build with: docker build -t pmd-ci .
# run with: docker run -it pmd-ci
#