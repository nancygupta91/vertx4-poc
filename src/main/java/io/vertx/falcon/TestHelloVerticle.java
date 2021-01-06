package io.vertx.falcon;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.openapi.RouterBuilder;
import io.vertx.ext.web.openapi.RouterBuilderOptions;
import io.vertx.falcon.handler.NoHandlerDefinition;

public class TestHelloVerticle extends AbstractVerticle {

    private HttpServer server;

    @Override
    public void start() {
        vertx.getOrCreateContext().put("TestKey", new JsonObject().put("key1", "value1"));
        addRoutes("test.json", "io.vertx.falcon.handler").onComplete(handler -> {
            if (handler.succeeded()) {
                Router router = handler.result();
                server = vertx
                        .createHttpServer()
                        .requestHandler(router);
                server.listen(8082)
                        .onSuccess(server -> System.out.println("Server started on port " + server.actualPort()))
                        .onFailure(Throwable::printStackTrace);

            }
            else {
                Throwable exception = handler.cause();
                System.out.println(exception);
            }
        });
    }

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new TestHelloVerticle());
    }

    private Future<Router> addRoutes(String fileName, String handlerPackageName) {
        Promise<Router> routePromise = Promise.promise();
        RouterBuilder.create(this.vertx,fileName)
                .onFailure(asyncResult -> routePromise.fail(asyncResult.getCause()))
                .onSuccess(routerBuilder -> {
                    RouterBuilderOptions factoryOptions = new RouterBuilderOptions()
                            .setRequireSecurityHandlers(false)
                            .setMountResponseContentTypeHandler(true)
                            .setMountNotImplementedHandler(true);
                    routerBuilder.setOptions(factoryOptions);

                    vertx.fileSystem().readFile(fileName, fileData -> {
                        if (fileData.succeeded()) {
                            JsonObject schema = new JsonObject(fileData.result());
                            String opId;
                            String handlerClassName;
                            Handler<RoutingContext> handler;

                            JsonObject pathsObject = schema.getJsonObject("paths");
                            for (String path : pathsObject.fieldNames()) {
                                JsonObject pathObject = pathsObject.getJsonObject(path);
                                for (String meth : pathObject.fieldNames()) {
                                    opId = pathObject.getJsonObject(meth).getString("operationId");
                                    handlerClassName = opId.substring(0, 1).toUpperCase() + opId.substring(1);
                                    try {
                                        handler = (Handler<RoutingContext>)Class
                                                .forName(handlerPackageName + "." + handlerClassName + "Handler")
                                                .newInstance();
                                        routerBuilder.operation(opId).handler(handler);

                                    }
                                    catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
                                        routerBuilder.operation(opId).handler(new NoHandlerDefinition());

                                    }
                                }
                            }
                            // Generate the router.
                            Router router = routerBuilder.createRouter();

                            // health check end point handling
                            router.get("/health").handler(routingContext -> {
                                routingContext.response().putHeader("content-type", "text/html")
                                        .setStatusCode(200).end("OK. Healthy");
                            });
                            routePromise.complete(router);
                        }
                        else {
                            routePromise.fail(fileData.cause());

                        }
                    });
                });
        return routePromise.future();
    }

}
