package manager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.HttpTaskServer;
import server.adapters.DurationAdapter;
import server.adapters.EpicAdapter;
import server.adapters.LocalDateTimeAdapter;
import task.Epic;
import task.SubTask;
import task.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HttpTaskServerTest {
    private final TaskManager manager = new InMemoryTaskManager();
    private final HttpTaskServer taskServer = new HttpTaskServer(manager);
    protected Map<Integer, Task> tasks = Map.of(
            1, new Task("task1", "desc", Duration.ofSeconds(10), LocalDateTime.now()),
            2, new Task("task2", "desc", Duration.ofSeconds(10), LocalDateTime.now().plusMinutes(1)),
            3, new Task("task3", "desc", Duration.ofSeconds(10), LocalDateTime.now().plusMinutes(2))
    );
    protected Map<Integer, Epic> epics = Map.of(
            1, new Epic("epic1", "desc"),
            2, new Epic("epic2", "desc")
    );

    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Epic.class, new EpicAdapter())
            .create();
    HttpClient client = HttpClient.newHttpClient();


    @BeforeEach
    public void setUp() throws IOException {
        manager.deleteTasks();
        manager.deleteSubTasks();
        manager.deleteEpics();
        taskServer.start();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
        client.close();
    }

    @Test
    public void getTasksShouldReturnTwoTask() throws IOException, InterruptedException {
        manager.addTask(tasks.get(1));
        manager.addTask(tasks.get(2));

        URI uri = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder(uri).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains(tasks.get(1).getName()));
        assertTrue(response.body().contains(tasks.get(2).getName()));
    }

    @Test
    public void getTaskIdsShouldReturnOneTask() throws IOException, InterruptedException {
        manager.addTask(tasks.get(1));
        int id = tasks.get(1).getId();

        URI uri = URI.create("http://localhost:8080/tasks/" + id);
        HttpRequest request = HttpRequest.newBuilder(uri).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains(tasks.get(1).getName()));
    }

    @Test
    public void postTaskWithoutIdsShouldAddOneTask() throws IOException, InterruptedException {
        String taskJson = gson.toJson(tasks.get(1));

        URI uri = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest
                .newBuilder(uri)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());
        assertEquals(1, manager.getTasks().size());
    }

    @Test
    public void postTaskWithIdsShouldUpdateTask() throws IOException, InterruptedException {
        manager.addTask(tasks.get(1));
        int id = tasks.get(1).getId();

        URI uri = URI.create("http://localhost:8080/tasks/" + id);
        HttpRequest request = HttpRequest
                .newBuilder(uri)
                .POST(HttpRequest.BodyPublishers.ofString(""))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());
    }

    @Test
    public void deleteTaskWithIdsShouldDeleteTask() throws IOException, InterruptedException {
        manager.addTask(tasks.get(1));
        int id = tasks.get(1).getId();

        URI uri = URI.create("http://localhost:8080/tasks/" + id);
        HttpRequest request = HttpRequest
                .newBuilder(uri)
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());
        assertEquals(0, manager.getTasks().size());
    }

    @Test
    public void getSubTasksShouldReturnTwoTask() throws IOException, InterruptedException {
        manager.addEpic(epics.get(1));
        manager.addSubTask(new SubTask("subTask1", "desc", epics.get(1).getId(),
                Duration.ofSeconds(10), LocalDateTime.now().plusMinutes(3)));
        manager.addSubTask(new SubTask("subTask2", "desc", epics.get(1).getId(),
                Duration.ofSeconds(10), LocalDateTime.now().plusMinutes(4)));

        URI uri = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder(uri).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("subTask1"));
        assertTrue(response.body().contains("subTask2"));
    }

    @Test
    public void getSubTaskIdsShouldReturnOneTask() throws IOException, InterruptedException {
        manager.addEpic(epics.get(1));
        SubTask task = new SubTask("subTask1", "desc", epics.get(1).getId(),
                Duration.ofSeconds(10), LocalDateTime.now().plusMinutes(3));
        manager.addSubTask(task);
        int id = task.getId();

        URI uri = URI.create("http://localhost:8080/subtasks/" + id);
        HttpRequest request = HttpRequest.newBuilder(uri).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains(task.getName()));
    }

    @Test
    public void postSubTaskWithoutIdsShouldAddOneTask() throws IOException, InterruptedException {
        manager.addEpic(epics.get(1));
        SubTask task = new SubTask("subTask1", "desc", epics.get(1).getId(),
                Duration.ofSeconds(10), LocalDateTime.now().plusMinutes(3));
        String taskJson = gson.toJson(task);

        URI uri = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest
                .newBuilder(uri)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());
        assertEquals(1, manager.getSubTasks().size());
    }

    @Test
    public void postSubTaskWithIdsShouldUpdateTask() throws IOException, InterruptedException {
        manager.addEpic(epics.get(1));
        SubTask task = new SubTask("subTask1", "desc", epics.get(1).getId(),
                Duration.ofSeconds(10), LocalDateTime.now().plusMinutes(3));
        manager.addSubTask(task);
        int id = task.getId();

        URI uri = URI.create("http://localhost:8080/subtasks/" + id);
        HttpRequest request = HttpRequest
                .newBuilder(uri)
                .POST(HttpRequest.BodyPublishers.ofString(""))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());
    }

    @Test
    public void deleteSubtaskWithIdsShouldDeleteTask() throws IOException, InterruptedException {
        manager.addEpic(epics.get(1));
        SubTask task = new SubTask("subTask1", "desc", epics.get(1).getId(),
                Duration.ofSeconds(10), LocalDateTime.now().plusMinutes(3));
        manager.addSubTask(task);
        int id = task.getId();

        URI uri = URI.create("http://localhost:8080/subtasks/" + id);
        HttpRequest request = HttpRequest
                .newBuilder(uri)
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());
        assertEquals(0, manager.getSubTasks().size());
    }

    @Test
    public void getEpicsShouldReturnEpics() throws IOException, InterruptedException {
        manager.addEpic(epics.get(1));
        manager.addEpic(epics.get(2));

        URI uri = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder(uri).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains(epics.get(1).getName()));
        assertTrue(response.body().contains(epics.get(2).getName()));
    }

    @Test
    public void getEpicByIdShouldReturnOneEpic() throws IOException, InterruptedException {
        manager.addEpic(epics.get(1));
        int id = epics.get(1).getId();

        URI uri = URI.create("http://localhost:8080/epics/" + id);
        HttpRequest request = HttpRequest.newBuilder(uri).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains(epics.get(1).getName()));
    }

    @Test
    public void getEpicSubtasksShouldReturnEpicSubtasks() throws IOException, InterruptedException {
        manager.addEpic(epics.get(1));
        int id = epics.get(1).getId();
        SubTask task1 = new SubTask("subTask1", "desc", epics.get(1).getId(),
                Duration.ofSeconds(10), LocalDateTime.now().plusMinutes(3));
        SubTask task2 = new SubTask("subTask2", "desc", epics.get(1).getId(),
                Duration.ofSeconds(10), LocalDateTime.now().plusMinutes(3));
        manager.addSubTask(task1);
        manager.addSubTask(task2);

        URI uri = URI.create("http://localhost:8080/epics/" + id + "/subtasks");
        HttpRequest request = HttpRequest.newBuilder(uri).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains(epics.get(1).getSubTaskIDs().toString()));
    }

    @Test
    public void postEpicShouldCreateEpic() throws IOException, InterruptedException {
        String taskJson = gson.toJson(epics.get(1));
        URI uri = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest
                .newBuilder(uri)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());
        assertEquals(1, manager.getEpics().size());
    }

    @Test
    public void deleteEpicsWithIdShouldDeleteEpic() throws IOException, InterruptedException {
        manager.addEpic(epics.get(1));
        int id = epics.get(1).getId();
        assertEquals(1, manager.getEpics().size());

        URI uri = URI.create("http://localhost:8080/epics/" + id);
        HttpRequest request = HttpRequest
                .newBuilder(uri)
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());
        assertEquals(0, manager.getEpics().size());
    }

    @Test
    public void getHistoryShouldReturnHistory() throws IOException, InterruptedException {
        manager.addTask(tasks.get(1));
        manager.addTask(tasks.get(2));
        manager.addEpic(epics.get(1));
        manager.addEpic(epics.get(2));
        manager.getTaskByID(tasks.get(1).getId());
        manager.getTaskByID(tasks.get(2).getId());
        manager.getEpicByID(epics.get(1).getId());
        manager.getEpicByID(epics.get(2).getId());

        URI uri = URI.create("http://localhost:8080/history");
        HttpRequest request = HttpRequest
                .newBuilder(uri)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("\"id\": " + tasks.get(1).getId().toString()));
        assertTrue(response.body().contains("\"id\": " + tasks.get(2).getId().toString()));
        assertTrue(response.body().contains("\"id\": " + epics.get(1).getId().toString()));
        assertTrue(response.body().contains("\"id\": " + epics.get(2).getId().toString()));
    }

    @Test
    public void getPrioritized() throws IOException, InterruptedException {
        manager.addEpic(epics.get(1));
        Task task1 =  new Task("task1", "4", Duration.ofSeconds(10), LocalDateTime.now().plusMinutes(40));
        Task task2 =  new Task("task2", "1", Duration.ofSeconds(10), LocalDateTime.now());
        SubTask subtask1 =  new SubTask("subtask1", "3", epics.get(1).getId(),
                Duration.ofSeconds(10), LocalDateTime.now().plusMinutes(30));
        SubTask subtask2 =  new SubTask("subtask2", "2", epics.get(1).getId(),
                Duration.ofSeconds(10), LocalDateTime.now().plusMinutes(10));
        manager.addTask(task1);
        manager.addTask(task2);
        manager.addSubTask(subtask1);
        manager.addSubTask(subtask2);

        URI uri = URI.create("http://localhost:8080/prioritized");
        HttpRequest request = HttpRequest
                .newBuilder(uri)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        int pos1 = response.body().indexOf("\"id\": " + task1.getId().toString());
        int pos2 = response.body().indexOf("\"id\": " + task2.getId().toString());
        int pos3 = response.body().indexOf("\"id\": " + subtask1.getId().toString());
        int pos4 = response.body().indexOf("\"id\": " + subtask2.getId().toString());
        assertTrue(pos2 < pos4 && pos4 < pos3 && pos3 < pos1);
    }

    @Test
    public void error404ifEndpointNotExist() throws IOException, InterruptedException {
        URI uri = URI.create("http://localhost:8080/notexist");
        HttpRequest request = HttpRequest.newBuilder(uri).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
    }

    @Test
    public void error404ifTaskNotExist() throws IOException, InterruptedException {
        URI uri = URI.create("http://localhost:8080/tasks/999");
        HttpRequest request = HttpRequest.newBuilder(uri).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
    }

    @Test
    public void error500ifTaskIntersection() throws IOException, InterruptedException {
        Task task1 =  new Task("task1", "4", Duration.ofSeconds(50), LocalDateTime.now());
        Task task2 =  new Task("task2", "1", Duration.ofSeconds(100), LocalDateTime.now());

        String taskJson1 = gson.toJson(task1);
        String taskJson2 = gson.toJson(task2);

        URI uri = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest
                .newBuilder(uri)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson1))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        request = HttpRequest
                .newBuilder(uri)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson2))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(500, response.statusCode());
    }

}
