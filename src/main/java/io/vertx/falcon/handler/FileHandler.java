package io.vertx.falcon.handler;

import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.FileUpload;
import io.vertx.ext.web.RoutingContext;

public class FileHandler implements Handler<RoutingContext> {

    @Override
    public void handle(RoutingContext routingContext) {
        if (routingContext.fileUploads() != null && !routingContext.fileUploads().isEmpty()) {

            System.out.println(Vertx.currentContext());
            JsonObject json1 = Vertx.currentContext().get("TestKey");
            System.out.println(json1);
            System.out.println(routingContext.vertx().getOrCreateContext());
            JsonObject json2 = routingContext.vertx().getOrCreateContext().get("TestKey");
            System.out.println(json2);
            FileUpload fileUpload = routingContext.fileUploads().iterator().next();
            routingContext.response().end("Testing File Upload");
        }

    }
}
