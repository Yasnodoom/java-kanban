package server.handlers;

import com.sun.net.httpserver.HttpExchange;
import server.Endpoint;
import task.Epic;
import task.Task;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

public class EpicHandler extends ManagerHandler {
    public EpicHandler() throws IOException {
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());

        switch (endpoint) {
            case GET_EPICS -> handleGetEpics(exchange);
            case GET_EPIC_ID -> handleGetEpicID(exchange);
            case POST_EPICS -> handlePostEpic(exchange);
            case DELETE_EPICS -> handleDeleteEpics(exchange);
            default -> writeResponse(exchange, "Такого эндпоинта не существует", 404);
        }
    }

    private void handleGetEpics(HttpExchange exchange) throws IOException {
        writeResponse(exchange, gson.toJson(manager.getEpics()), 200);
    }

    private void handleGetEpicID(HttpExchange exchange) throws IOException {
        Optional<Integer> id = getId(exchange);
        if (id.isEmpty()) {
            writeResponse(exchange, "Некорректный идентификатор", 400);
            return;
        }

        final int taskId = id.get();
        if (manager.getEpics().stream().noneMatch(task -> task.getId() == taskId)) {
            writeResponse(exchange, "Таск с идентификатором " + taskId + " не найден", 404);
            return;
        }
        writeResponse(exchange, gson.toJson(manager.getEpicByID(taskId)), 200);
    }

    private void handleDeleteEpics(HttpExchange exchange) throws IOException {
        Optional<Integer> id = getId(exchange);
        if (id.isEmpty()) {
            writeResponse(exchange, "Некорректный идентификатор", 400);
            return;
        }

        final int taskId = id.get();
        if (manager.getEpics().stream().noneMatch(task -> task.getId() == taskId)) {
            writeResponse(exchange, "Таск с идентификатором " + taskId + " не найден", 404);
            return;
        }

        manager.deleteEpicByID(taskId);
        exchange.sendResponseHeaders(201, 0);
        exchange.close();
    }

    private void handlePostEpic(HttpExchange exchange) throws IOException {
        Epic task = new Epic("Epic", "create by API");
        try {
            manager.addEpic(task);
        } catch (Exception e) {
            writeResponse(exchange, e.getMessage(), 500);
            return;
        }
        String response = "Создан новый 'эпик! \n" + gson.toJson(task);
        writeResponse(exchange, response, 201);
    }
}
