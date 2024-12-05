import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;

public class USprint8Task4Test {

    private static TaskManager taskManager;

    @BeforeAll
    static void beforeAll() {
        System.out.println("------------------------------------");
        System.out.println("Тестирование №4. Перехват исключений");
        System.out.println("------------------------------------");
    }

    @BeforeEach
    public void beforeEach() {

        HistoryManager historyManager = Managers.getDefaultHistory();
        taskManager = Managers.getFileBackedTaskManager(historyManager, "D:/Projects/TmpFiles/file.csv");

        Task task1 = new Task("Базовая задача №1", "ОК-001", 30, "2024-12-01 20:00");
        taskManager.addTask(task1);

        Task task2 = new Task("Базовая задача №2", "ОК-002", 30, "2024-12-01 21:00");
        taskManager.addTask(task2);
    }

    @AfterEach
    public void afterEach(){
        //taskManager.removeTasks();
        //taskManager.removeSubtasks();
        //taskManager.removeEpics();
    }

    @Test
    public void checkExceptionCatching() {
        assertThrows(ManagerSaveException.class, () -> {
            Task task3 = new Task("Базовая задача №3", "ОК-003", 30, "2024-12-01 22:00");
            taskManager.addTask(task3);
            throw new ManagerSaveException("Произошла ошибка во время чтения файла");
        }, "Не удалось перехватить исключение");
    }
}
