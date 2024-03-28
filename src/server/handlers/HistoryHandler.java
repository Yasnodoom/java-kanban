package server.handlers;

import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;
import server.Endpoint;

import java.io.IOException;
import java.util.Objects;

public class HistoryHandler extends ManagerHandler {

    public HistoryHandler(TaskManager manager) {
        super(manager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());

        if (Objects.requireNonNull(endpoint) == Endpoint.GET_HISTORY) {
            writeResponse(exchange, gson.toJson(manager.getHistory()), 200);
        } else {
            writeResponse(exchange, "Такого эндпоинта не существует", 404);
        }
    }
}
