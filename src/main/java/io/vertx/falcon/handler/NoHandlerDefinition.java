package io.vertx.falcon.handler;

import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

public class NoHandlerDefinition implements Handler<RoutingContext> {

    @Override
    public void handle(RoutingContext rc) {
        rc.response().setStatusCode(500).setStatusMessage("No Handler")
                .end(new JsonObject().put("message", "Handler not found").encodePrettily());
    }
}
