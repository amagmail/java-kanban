import managers.HistoryManager;
import managers.Managers;
import managers.TaskManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import tasks.Task;

public class USprint8Task4Test {

    private static TaskManager taskManager;
    public final boolean isFileBacked = false;

    @BeforeAll
    static void beforeAll() {
        System.out.println("------------------------------------");
        System.out.println("Тестирование №4. Перехват исключений");
        System.out.println("------------------------------------");
    }

    @BeforeEach
    public void beforeEach() {

        HistoryManager historyManager = Managers.getDefaultHistory();
        if (isFileBacked) {
            taskManager = Managers.getFileBackedTaskManager(historyManager, "D:/Projects/TmpFiles/file.csv");
        } else {
            taskManager = Managers.getDefault(historyManager);
        }

        Task task1 = new Task("Базовая задача №1", "ОК-001", 30, Managers.stringToDate("2024-12-01 20:00"));
        taskManager.addTask(task1);

        Task task2 = new Task("Базовая задача №2", "ОК-002", 30, Managers.stringToDate("2024-12-01 21:00"));
        taskManager.addTask(task2);
    }

    @AfterEach
    public void afterEach(){
        taskManager.removeTasks();
        taskManager.removeSubtasks();
        taskManager.removeEpics();
    }

    @Test
    public void checkExceptionCatching() {
        Assertions.assertDoesNotThrow(() -> {
            Task task3 = new Task("Базовая задача №3", "ОК-003", 30, Managers.stringToDate("2024-12-01 22:00"));
            taskManager.addTask(task3);
        }, "Обнаружено исключение");
    }
}
