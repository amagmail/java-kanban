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

public class HttpTaskManagerEpicsTest {

    private static TaskManager taskManager;
    private static HttpTaskServer taskServer;

    @BeforeAll
    static void beforeAll() {
        System.out.println("-----------------------------------");
        System.out.println("Тестирование №3. HTTP-Server: Epics");
        System.out.println("-----------------------------------");
    }

    @BeforeEach
    public void beforeEach() throws IOException {

        HistoryManager historyManager = Managers.getDefaultHistory();
        taskManager = Managers.getDefault(historyManager);
        taskServer = new HttpTaskServer(taskManager);

        Epic epic1= new Epic("Эпик №1", "ОК-001");
        taskManager.addEpic(epic1);
        Subtask subtask2 = new Subtask("Подзадача №2", "ОК-002", epic1.id, 10, Managers.stringToDate("2024-12-01 21:00"));
        taskManager.addSubtask(subtask2);
        Subtask subtask3 = new Subtask("Подзадача №3", "ОК-003", epic1.id, 10, Managers.stringToDate("2024-12-01 22:00"));
        taskManager.addSubtask(subtask3);
        Subtask subtask4 = new Subtask("Подзадача №4", "ОК-004", epic1.id, 10, Managers.stringToDate("2024-12-01 23:00"));
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
    public void checkGetEpics() throws IOException, InterruptedException {

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");

        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println(">> Получить все эпики");
        System.out.println("* Код состояния ответа: " + response.statusCode());
        System.out.println("* Тело ответа: " + response.body());
        assertEquals(200, response.statusCode());
    }

    @Test
    public void checkGetEpicByID() throws IOException, InterruptedException {

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/1");

        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println(">> Получить эпик по идентификатору");
        System.out.println("* Код состояния ответа: " + response.statusCode());
        System.out.println("* Тело ответа: " + response.body());
        assertEquals(200, response.statusCode());
    }

    @Test
    public void checkGetEpicSubtasks() throws IOException, InterruptedException {

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/1/subtasks");

        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println(">> Получить подзадачи по идентификатору эпика");
        System.out.println("* Код состояния ответа: " + response.statusCode());
        System.out.println("* Тело ответа: " + response.body());
        assertEquals(200, response.statusCode());
    }

    @Test
    public void checkDeleteEpic() throws IOException, InterruptedException {

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/1");

        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println(">> Удалить эпик по идентификатору");
        System.out.println("* Код состояния ответа: " + response.statusCode());
        System.out.println("* Тело ответа: " + response.body());
        assertEquals(201, response.statusCode());

        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println(">> Попытка повторного удаления эпика");
        System.out.println("* Код состояния ответа: " + response.statusCode());
        System.out.println("* Тело ответа: " + response.body());
        assertEquals(404, response.statusCode());
    }

    @Test
    public void checkUpdateEpic() throws IOException, InterruptedException {

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");

        String taskJson = taskJson = "{\"title\":\"14\",\"description\":\"14\",\"id\":1}";
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println(">> Изменить эпик по существующему идентификатору");
        System.out.println("* Код состояния ответа: " + response.statusCode());
        System.out.println("* Тело ответа: " + response.body());
        assertEquals(200, response.statusCode());
    }

    @Test
    public void checkCreateEpic() throws IOException, InterruptedException {

        int histSize = taskManager.getEpics().size();

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");

        String taskJson = taskJson = "{\"title\":\"14\",\"description\":\"14\"}";
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println(">> Создать новый эпик");
        System.out.println("* Код состояния ответа: " + response.statusCode());
        System.out.println("* Тело ответа: " + response.body());
        assertEquals(200, response.statusCode());
        assertEquals(histSize + 1, taskManager.getEpics().size());
    }
}
