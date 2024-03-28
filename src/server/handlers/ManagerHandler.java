package server.handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.TaskManager;
import server.Endpoint;
import server.adapters.DurationAdapter;
import server.adapters.EpicAdapter;
import server.adapters.LocalDateTimeAdapter;
import task.Epic;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

public abstract class ManagerHandler implements HttpHandler {
    protected Gson gson;
    protected TaskManager manager;

    protected ManagerHandler(TaskManager manager) {
        this.manager = manager;
        gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Epic.class, new EpicAdapter())
                .create();
    }

    protected void writeResponse(HttpExchange exchange,
                                 String responseString,
                                 int responseCode) throws IOException {
        try (OutputStream os = exchange.getResponseBody()) {
            exchange.sendResponseHeaders(responseCode, 0);
            os.write(responseString.getBytes(StandardCharsets.UTF_8));
        }
    }

    protected Optional<Integer> getId(HttpExchange exchange) {
        String[] pathParts = exchange.getRequestURI().getPath().split("/");

        try {
            return Optional.of(Integer.parseInt(pathParts[2]));
        } catch (NumberFormatException | IndexOutOfBoundsException exception) {
            return Optional.empty();
        }
    }

    protected Endpoint getEndpoint(String requestPath, String requestMethod) {
        String[] pathParts = requestPath.split("/");

        switch (pathParts[1]) {
            case "tasks":
                if (requestMethod.equals("GET")) {
                    if (pathParts.length == 3)
                        return Endpoint.GET_TASK_ID;
                    if (pathParts.length == 2)
                        return Endpoint.GET_TASKS;
                }
                if (requestMethod.equals("POST"))
                    return Endpoint.POST_TASKS;
                if (requestMethod.equals("DELETE"))
                    return Endpoint.DELETE_TASKS;
                break;
            case "subtasks":
                if (requestMethod.equals("GET")) {
                    if (pathParts.length == 3)
                        return Endpoint.GET_SUBTASKS_ID;
                    if (pathParts.length == 2)
                        return Endpoint.GET_SUBTASKS;
                }
                if (requestMethod.equals("POST"))
                    return Endpoint.POST_SUBTASKS;
                if (requestMethod.equals("DELETE"))
                    return Endpoint.DELETE_SUBTASKS;
                break;
            case "epics":
                if (requestMethod.equals("GET")) {
                    if (pathParts.length == 3)
                        return Endpoint.GET_EPIC_ID;
                    if (pathParts.length == 2)
                        return Endpoint.GET_EPICS;
                    if (pathParts.length == 4 && pathParts[3].equals("subtasks"))
                        return Endpoint.GET_EPIC_SUBTASKS;
                }
                if (requestMethod.equals("POST"))
                    return Endpoint.POST_EPICS;
                if (requestMethod.equals("DELETE"))
                    return Endpoint.DELETE_EPICS;
                break;
            case "history":
                if (requestMethod.equals("GET")) {
                    return Endpoint.GET_HISTORY;
                }
                break;
            case "prioritized":
                if (requestMethod.equals("GET")) {
                    return Endpoint.GET_PRIORITIZED;
                }
                break;
            default:
                break;
        }
        return Endpoint.UNKNOWN;
    }
}
