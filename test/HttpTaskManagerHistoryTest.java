import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;

import managers.HistoryManager;
import managers.Managers;
import managers.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

public class HttpTaskManagerHistoryTest {

    private static TaskManager taskManager;
    private static HttpTaskServer taskServer;

    @BeforeAll
    static void beforeAll() {
        System.out.println("-------------------------------------");
        System.out.println("Тестирование №4. HTTP-Server: History");
        System.out.println("-------------------------------------");
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

        Epic epic3 = new Epic("Эпик №3", "ОК-003");
        taskManager.addEpic(epic3);

        Epic epic4 = new Epic("Эпик №4", "ОК-004");
        taskManager.addEpic(epic4);

        Subtask subtask5 = new Subtask("Подзадача №5", "ОК-005", epic3.getId(), 10, Managers.stringToDate("2024-12-01 21:00"));
        taskManager.addSubtask(subtask5);

        Subtask subtask6 = new Subtask("Подзадача №6", "ОК-006", epic3.getId(), 10, Managers.stringToDate("2024-12-01 22:00"));
        taskManager.addSubtask(subtask6);

        Subtask subtask7 = new Subtask("Подзадача №7", "ОК-007", epic4.getId(), 10, Managers.stringToDate("2024-12-01 23:00"));
        taskManager.addSubtask(subtask7);

        taskManager.getTaskByID(1);
        taskManager.getTaskByID(2);
        taskManager.getEpicByID(3);
        taskManager.getSubtaskByID(5);
        taskManager.getSubtaskByID(5);
        taskManager.getSubtaskByID(5);
        taskManager.getEpicByID(3);
        taskManager.getSubtaskByID(5);
        taskManager.getSubtaskByID(5);
        taskManager.getSubtaskByID(5);
        taskManager.getTaskByID(2);

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
        URI url = URI.create("http://localhost:8080/history");

        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println(">> Получить историю задач");
        System.out.println("* Код состояния ответа: " + response.statusCode());
        System.out.println("* Тело ответа: " + response.body());
        assertEquals(200, response.statusCode());
    }

}
