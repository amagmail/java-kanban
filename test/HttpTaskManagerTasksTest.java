import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;

public class HttpTaskManagerTasksTest {

    private static TaskManager taskManager;
    private static HttpTaskServer taskServer;

    @BeforeAll
    static void beforeAll() {
        System.out.println("-----------------------------------");
        System.out.println("Тестирование №1. HTTP-Server: Tasks");
        System.out.println("-----------------------------------");
    }

    @BeforeEach
    public void beforeEach() throws IOException {

        HistoryManager historyManager = Managers.getDefaultHistory();
        taskManager = Managers.getDefault(historyManager);
        taskServer = new HttpTaskServer(taskManager);

        Task task1 = new Task("Базовая задача №1", "ОК-001", 30, Managers.stringToDate("2024-12-01 20:00"));
        taskManager.addTask(task1);
        Task task2 = new Task("Базовая задача №2", "ОК-002", 30, Managers.stringToDate("2024-12-01 20:30"));
        taskManager.addTask(task2);
        Task task3 = new Task("Базовая задача №3", "ОК-003", 30, Managers.stringToDate("2024-12-01 21:00"));
        taskManager.addTask(task3);

        taskServer.start();
    }

    @AfterEach
    public void afterEach(){

        taskManager.removeTasks();
        taskManager.removeSubtasks();
        taskManager.removeEpics();

        taskServer.stop();
    }

    @Test
    public void checkGetTasks() throws IOException, InterruptedException {

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");

        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println(">> Получить все задачи");
        System.out.println("* Код состояния ответа: " + response.statusCode());
        System.out.println("* Тело ответа: " + response.body());
        assertEquals(200, response.statusCode());
    }

    @Test
    public void checkGetTaskByID() throws IOException, InterruptedException {

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/1");

        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println(">> Получить задачу по идентификатору");
        System.out.println("* Код состояния ответа: " + response.statusCode());
        System.out.println("* Тело ответа: " + response.body());
        assertEquals(200, response.statusCode());
    }

    @Test
    public void checkDeleteTask() throws IOException, InterruptedException {

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/1");

        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println(">> Удалить задачу по идентификатору");
        System.out.println("* Код состояния ответа: " + response.statusCode());
        System.out.println("* Тело ответа: " + response.body());
        assertEquals(201, response.statusCode());

        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println(">> Попытка повторного удаления задачи");
        System.out.println("* Код состояния ответа: " + response.statusCode());
        System.out.println("* Тело ответа: " + response.body());
        assertEquals(404, response.statusCode());
    }

    @Test
    public void checkUpdateTask() throws IOException, InterruptedException {

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");

        String taskJson = "{\"title\":\"14\",\"description\":\"14\",\"minutes\":30,\"date\":\"2024-12-11 20:40\",\"id\":1}";
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println(">> Изменить задачу по существующему идентификатору");
        System.out.println("* Код состояния ответа: " + response.statusCode());
        System.out.println("* Тело ответа: " + response.body());
        assertEquals(200, response.statusCode());

        taskJson = "{\"title\":\"14\",\"description\":\"14\",\"minutes\":30,\"date\":\"2024-12-11 20:40\",\"id\":100}";
        request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println(">> Изменить задачу по несуществующему идентификатору");
        System.out.println("* Код состояния ответа: " + response.statusCode());
        System.out.println("* Тело ответа: " + response.body());
        assertEquals(404, response.statusCode());
    }

    @Test
    public void checkCreateTask() throws IOException, InterruptedException {

        int histSize = taskManager.getTasks().size();

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");

        String taskJson = "{\"title\":\"14\",\"description\":\"14\",\"minutes\":30,\"date\":\"2024-12-11 20:40\"}";
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println(">> Создать новую задачу");
        System.out.println("* Код состояния ответа: " + response.statusCode());
        System.out.println("* Тело ответа: " + response.body());
        assertEquals(200, response.statusCode());
        assertEquals(histSize + 1, taskManager.getTasks().size());

        taskJson = "{\"title\":\"14\",\"description\":\"14\",\"minutes\":30,\"date\":\"2024-12-01 20:40\"}";
        request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println(">> Создать задачу с пересекающейся датой");
        System.out.println("* Код состояния ответа: " + response.statusCode());
        System.out.println("* Тело ответа: " + response.body());
        assertEquals(406, response.statusCode());
        assertEquals(histSize + 1, taskManager.getTasks().size());
    }

}