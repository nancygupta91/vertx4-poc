package io.vertx.falcon.handler;

import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

public class WelcomeHandler implements Handler<RoutingContext> {
    
   
    @Override
    public void handle(RoutingContext routingContext) {
        System.out.println(routingContext.vertx().getOrCreateContext());
        JsonObject json = routingContext.vertx().getOrCreateContext().get("TestKey");
        System.out.println(json.encodePrettily());
            routingContext.response().end("Welcome");
    

}

}
