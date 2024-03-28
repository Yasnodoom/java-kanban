package server.handlers;

import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;
import server.Endpoint;

import java.io.IOException;
import java.util.Objects;

public class PrioritizedHandler extends ManagerHandler {

    public PrioritizedHandler(TaskManager manager) {
        super(manager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());

        if (Objects.requireNonNull(endpoint) == Endpoint.GET_PRIORITIZED) {
            writeResponse(exchange, gson.toJson(manager.getPrioritizedTasks()), 200);
        } else {
            writeResponse(exchange, "Такого эндпоинта не существует", 404);
        }
    }
}
