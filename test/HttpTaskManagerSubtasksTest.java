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
import tasks.Epic;
import tasks.Subtask;

public class HttpTaskManagerSubtasksTest {

    private static TaskManager taskManager;
    private static HttpTaskServer taskServer;

    @BeforeAll
    static void beforeAll() {
        System.out.println("--------------------------------------");
        System.out.println("Тестирование №2. HTTP-Server: Subtasks");
        System.out.println("--------------------------------------");
    }

    @BeforeEach
    public void beforeEach() throws IOException {

        HistoryManager historyManager = Managers.getDefaultHistory();
        taskManager = Managers.getDefault(historyManager);
        taskServer = new HttpTaskServer(taskManager);

        Epic epic1= new Epic("Эпик №1", "ОК-001");
        taskManager.addEpic(epic1);
        Subtask subtask2 = new Subtask("Подзадача №2", "ОК-002", epic1.getId(), 10, Managers.stringToDate("2024-12-01 21:00"));
        taskManager.addSubtask(subtask2);
        Subtask subtask3 = new Subtask("Подзадача №3", "ОК-003", epic1.getId(), 10, Managers.stringToDate("2024-12-01 22:00"));
        taskManager.addSubtask(subtask3);
        Subtask subtask4 = new Subtask("Подзадача №4", "ОК-004", epic1.getId(), 10, Managers.stringToDate("2024-12-01 23:00"));
        taskManager.addSubtask(subtask4);

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
    public void checkGetSubtasks() throws IOException, InterruptedException {

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");

        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println(">> Получить все подзадачи");
        System.out.println("* Код состояния ответа: " + response.statusCode());
        System.out.println("* Тело ответа: " + response.body());
        assertEquals(200, response.statusCode());
    }

    @Test
    public void checkGetSubtaskByID() throws IOException, InterruptedException {

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/2");

        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println(">> Получить подзадачу по идентификатору");
        System.out.println("* Код состояния ответа: " + response.statusCode());
        System.out.println("* Тело ответа: " + response.body());
        assertEquals(200, response.statusCode());
    }

    @Test
    public void checkDeleteSubtask() throws IOException, InterruptedException {

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/2");

        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println(">> Удалить подзадачу по идентификатору");
        System.out.println("* Код состояния ответа: " + response.statusCode());
        System.out.println("* Тело ответа: " + response.body());
        assertEquals(201, response.statusCode());

        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println(">> Попытка повторного удаления подзадачи");
        System.out.println("* Код состояния ответа: " + response.statusCode());
        System.out.println("* Тело ответа: " + response.body());
        assertEquals(404, response.statusCode());
    }

    @Test
    public void checkUpdateSubtask() throws IOException, InterruptedException {

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");

        String taskJson = "{\"title\":\"14\",\"description\":\"14\",\"minutes\":30,\"date\":\"2024-12-11 20:40\",\"epicId\":1,\"id\":2}";
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println(">> Изменить подзадачу по существующему идентификатору");
        System.out.println("* Код состояния ответа: " + response.statusCode());
        System.out.println("* Тело ответа: " + response.body());
        assertEquals(200, response.statusCode());

        taskJson = "{\"title\":\"14\",\"description\":\"14\",\"minutes\":30,\"date\":\"2024-12-11 20:40\",\"epicId\":1,\"id\":20}";
        request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println(">> Изменить задачу по несуществующему идентификатору");
        System.out.println("* Код состояния ответа: " + response.statusCode());
        System.out.println("* Тело ответа: " + response.body());
        assertEquals(404, response.statusCode());
    }

    @Test
    public void checkCreateSubtask() throws IOException, InterruptedException {

        int histSize = taskManager.getSubtasks().size();

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");

        String taskJson = taskJson = "{\"title\":\"14\",\"description\":\"14\",\"minutes\":30,\"date\":\"2024-12-11 20:40\",\"epicId\":1}";
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println(">> Создать новую подзадачу");
        System.out.println("* Код состояния ответа: " + response.statusCode());
        System.out.println("* Тело ответа: " + response.body());
        assertEquals(200, response.statusCode());
        assertEquals(histSize + 1, taskManager.getSubtasks().size());

        taskJson = "{\"title\":\"14\",\"description\":\"14\",\"minutes\":30,\"date\":\"2024-12-01 21:05\",\"epicId\":1}";
        request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println(">> Создать подзадачу с пересекающейся датой");
        System.out.println("* Код состояния ответа: " + response.statusCode());
        System.out.println("* Тело ответа: " + response.body());
        assertEquals(406, response.statusCode());
        assertEquals(histSize + 1, taskManager.getSubtasks().size());
    }

}
