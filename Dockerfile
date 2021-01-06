
# Extend vert.x image
FROM swde/vertx:4.0.0

ENV VERTICLE_NAME io.vertx.falcon.TestHelloVerticle
ENV VERTICLE_FILE build/libs/vertx4-poc-1.0.0-SNAPSHOT.jar

# Set the location of the verticles
ENV VERTICLE_HOME /usr/verticles

EXPOSE 8082

# Copy your verticle to the container                 
COPY $VERTICLE_FILE $VERTICLE_HOME/
COPY src/main/resources/test.json $VERTICLE_HOME/

ADD build/distributions/vertx4-poc-1.0.0-SNAPSHOT.tar $VERTICLE_HOME
RUN cp $VERTICLE_HOME/vertx4-poc-1.0.0-SNAPSHOT/lib/* $VERTX_HOME/lib && \
rm -rf $VERTICLE_HOME/vertx4-poc-1.0.0-SNAPSHOT

# Launch the verticle
WORKDIR $VERTICLE_HOME
ENTRYPOINT ["sh", "-c"]
CMD ["exec vertx run $VERTICLE_NAME -cp $VERTICLE_HOME/*"]
