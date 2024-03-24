package server.handlers;

import com.sun.net.httpserver.HttpExchange;
import server.Endpoint;
import task.Task;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

public class TaskHandler extends ManagerHandler {
    public TaskHandler() throws IOException {
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());

        switch (endpoint) {
            case GET_TASKS: {
                handleGetTasks(exchange);
                break;
            }
            case GET_TASK_ID: {
                handleGetTaskID(exchange);
                break;
            }
            case POST_TASKS: {
                handlePostTask(exchange);
                break;
            }
            case DELETE_TASKS:
                handleDeleteTasks(exchange);
                break;
            default:
                writeResponse(exchange, "Такого эндпоинта не существует", 404);
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
            writeResponse(exchange, "Таск с идентификатором " + taskId + " не найден", 400);
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
            writeResponse(exchange, "Таск с идентификатором " + taskId + " не найден", 400);
            return;
        }

        manager.deleteTaskByID(taskId);
        exchange.sendResponseHeaders(201, 0);
        exchange.close();
    }

    private void handlePostTask(HttpExchange exchange) throws IOException {
        Optional<Integer> id = getId(exchange);
        if (id.isEmpty()) {
            Task task = new Task("name", "cerate by API", Duration.ZERO, LocalDateTime.now());
            manager.addTask(task);
            String response = "Создан новый таск! \n" + gson.toJson(task);
            writeResponse(exchange, response, 201);
            return;
        }

        final int taskId = id.get();
        if (manager.getTasks().stream().noneMatch(task -> task.getId() == taskId)) {
            writeResponse(exchange, "Таск с идентификатором " + taskId + " не найден", 400);
            return;
        }
        manager.updateTask(manager.getTaskByID(taskId));
        exchange.sendResponseHeaders(201, 0);
        exchange.close();
    }

}
