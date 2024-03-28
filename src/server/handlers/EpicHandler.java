package server.handlers;

import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;
import server.Endpoint;
import task.Epic;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class EpicHandler extends ManagerHandler {

    public EpicHandler(TaskManager manager) {
        super(manager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());

        switch (endpoint) {
            case GET_EPICS -> handleGetEpics(exchange);
            case GET_EPIC_ID -> handleGetEpicID(exchange);
            case GET_EPIC_SUBTASKS -> handleGetEpicSubtasks(exchange);
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
        InputStream inputStream = exchange.getRequestBody();
        String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        Epic task = gson.fromJson(body, Epic.class);
        try {
            manager.addEpic(task);
        } catch (Exception e) {
            writeResponse(exchange, e.getMessage(), 500);
            return;
        }
        exchange.sendResponseHeaders(201, 0);
        exchange.close();
    }

    private void handleGetEpicSubtasks(HttpExchange exchange) throws IOException {
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
        writeResponse(exchange, manager.getEpicByID(taskId).getSubTaskIDs().toString(), 200);
    }

}
