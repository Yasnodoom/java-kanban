package server.handlers;

import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;
import server.Endpoint;
import task.Task;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class TaskHandler extends ManagerHandler {

    public TaskHandler(TaskManager manager) {
        super(manager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());

        switch (endpoint) {
            case GET_TASKS -> handleGetTasks(exchange);
            case GET_TASK_ID -> handleGetTaskID(exchange);
            case POST_TASKS -> handlePostTask(exchange);
            case DELETE_TASKS -> handleDeleteTasks(exchange);
            default -> writeResponse(exchange, "Такого эндпоинта не существует", 404);
        }
    }

    private void handleGetTasks(HttpExchange exchange) throws IOException {
        writeResponse(exchange, gson.toJson(manager.getTasks()), 200);
    }

    private void handleGetTaskID(HttpExchange exchange) throws IOException {
        Optional<Integer> id = getId(exchange);
        if (id.isEmpty()) {
            writeResponse(exchange, "Некорректный идентификатор", 400);
            return;
        }

        final int taskId = id.get();
        if (manager.getTasks().stream().noneMatch(task -> task.getId() == taskId)) {
            writeResponse(exchange, "Таск с идентификатором " + taskId + " не найден", 404);
            return;
        }
        writeResponse(exchange, gson.toJson(manager.getTaskByID(taskId)), 200);
    }

    private void handleDeleteTasks(HttpExchange exchange) throws IOException {
        Optional<Integer> id = getId(exchange);
        if (id.isEmpty()) {
            writeResponse(exchange, "Некорректный идентификатор", 400);
            return;
        }

        final int taskId = id.get();
        if (manager.getTasks().stream().noneMatch(task -> task.getId() == taskId)) {
            writeResponse(exchange, "Таск с идентификатором " + taskId + " не найден", 404);
            return;
        }

        manager.deleteTaskByID(taskId);
        exchange.sendResponseHeaders(201, 0);
        exchange.close();
    }

    private void handlePostTask(HttpExchange exchange) throws IOException {
        Optional<Integer> id = getId(exchange);
        if (id.isEmpty()) {
            InputStream inputStream = exchange.getRequestBody();
            String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            Task task = gson.fromJson(body, Task.class);
            try {
                manager.addTask(task);
            } catch (Exception e) {
                writeResponse(exchange, e.getMessage(), 500);
                return;
            }
            exchange.sendResponseHeaders(201, 0);
            exchange.close();
            return;
        }

        final int taskId = id.get();
        if (manager.getTasks().stream().noneMatch(task -> task.getId() == taskId)) {
            writeResponse(exchange, "Таск с идентификатором " + taskId + " не найден", 404);
            return;
        }
        manager.updateTask(manager.getTaskByID(taskId));
        exchange.sendResponseHeaders(201, 0);
        exchange.close();
    }

}