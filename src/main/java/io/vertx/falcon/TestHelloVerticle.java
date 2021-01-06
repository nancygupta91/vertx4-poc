package io.vertx.falcon;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.openapi.RouterBuilder;
import io.vertx.ext.web.openapi.RouterBuilderOptions;

public class TestHelloVerticle extends AbstractVerticle {
    
    private HttpServer server;

    @Override
    public void start() {
        
      // Load the api spec. This operation is asynchronous
      RouterBuilder.create(this.vertx, "test.json")
        .onFailure(Throwable::printStackTrace) // In case the contract loading failed print the stacktrace
        .onSuccess(routerBuilder -> {
          // Before router creation you can enable/disable various router factory behaviours
          RouterBuilderOptions factoryOptions = new RouterBuilderOptions()
                  .setRequireSecurityHandlers(false)
                  .setMountResponseContentTypeHandler(true)
                  .setMountNotImplementedHandler(true); // Mount ResponseContentTypeHandler automatically
          routerBuilder.setOptions(factoryOptions);

          // Setup an handler for listPets
          routerBuilder.operation("welcome")
            .handler(routingContext -> {
              routingContext.response().setStatusCode(200).end("Welcome");
            });

          // Create the router
          Router router = routerBuilder.createRouter();

          // Now you can use your Router instance
          server = vertx
            .createHttpServer()
            .requestHandler(router);
          server.listen(8082)
            .onSuccess(server -> System.out.println("Server started on port " + server.actualPort()))
            .onFailure(Throwable::printStackTrace);
        });
    }
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new TestHelloVerticle());
    }
}
