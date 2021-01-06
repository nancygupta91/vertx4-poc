FROM alpine:3.12

RUN apk -U upgrade && apk add curl openjdk8-jre bash 


RUN wget -qO - https://repo1.maven.org/maven2/io/vertx/vertx-stack-manager/4.0.0/vertx-stack-manager-4.0.0-full.tar.gz | tar xvz -f - -C /usr/local

RUN apk add --update bash && rm -rf /var/cache/apk/*
RUN chmod +x /usr/local/vertx/bin/vertx
RUN cat /etc/os-release

RUN  apk update
RUN  apk upgrade

# Set path
ENV VERTX_HOME /usr/local/vertx
ENV PATH $VERTX_HOME/bin:$PATH
