import managers.HistoryManager;
import managers.Managers;
import managers.TaskManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

public class HistoryManagerTest {

    private static HistoryManager historyManager = Managers.getDefaultHistory();
    private static TaskManager taskManager = Managers.getDefault(historyManager);

    @BeforeAll
    static void beforeAll() {
        System.out.println("------------------------------------");
        System.out.println("Тестирование истории просмотра задач");
        System.out.println("------------------------------------");
    }

    @BeforeEach
    public void beforeEach() {

        historyManager = Managers.getDefaultHistory();
        taskManager = Managers.getDefault(historyManager);

        // Создать новые задачи всех типов
        Task task1 = new Task("Базовая задача №1", "ОК-001");
        taskManager.addTask(task1);

        Task task2 = new Task("Базовая задача №2", "ОК-002");
        taskManager.addTask(task2);

        Epic epic3 = new Epic("Эпик №3", "ОК-003");
        taskManager.addEpic(epic3);

        Epic epic4 = new Epic("Эпик №4", "ОК-004");
        taskManager.addEpic(epic4);

        Subtask subtask5 = new Subtask("Подзадача №5", "ОК-005", epic3.getId());
        taskManager.addSubtask(subtask5);

        Subtask subtask6 = new Subtask("Подзадача №6", "ОК-006", epic3.getId());
        taskManager.addSubtask(subtask6);

        Subtask subtask7 = new Subtask("Подзадача №7", "ОК-007", epic4.getId());
        taskManager.addSubtask(subtask7);

        // Сформировать историю просмотра задач
        taskManager.getTaskByID(2);
        taskManager.getEpicByID(3);
        taskManager.getSubtaskByID(6);
        taskManager.getSubtaskByID(6);
        taskManager.getSubtaskByID(6);
        taskManager.getEpicByID(3);
    }

    @Test
    public void checkFirstItemHistory() {
        System.out.println(">> Получить первый элемент истории: id = " + historyManager.getHistory().getFirst().getId());
        printHistory();
        Assertions.assertEquals(historyManager.getHistory().getFirst(), taskManager.getTaskByID(2), "История просмотра задач отображается неверно");
        System.out.println();
    }

    @Test
    public void checkLastItemHistory() {
        System.out.println(">> Получить последний элемент истории: id = " + historyManager.getHistory().getLast().getId());
        printHistory();
        Assertions.assertEquals(historyManager.getHistory().getLast(), taskManager.getEpicByID(3), "История просмотра задач отображается неверно");
        System.out.println();
    }

    @Test
    public void checkTheNumberOfItems() {
        System.out.println(">> Получить количество элементов истории: size = " + historyManager.getHistory().size());
        printHistory();
        Assertions.assertEquals(historyManager.getHistory().size(), 3, "История просмотра задач отображается неверно");
        System.out.println();
    }

    @Test
    public void checkTheNumberOfItemsAfterDelete() {
        System.out.println(">> Получить количество элементов истории до и после удаления: sizeBebore = " + historyManager.getHistory().size());
        taskManager.removeEpicByID(3);
        System.out.println("sizeAfter = " + historyManager.getHistory().size());
        Assertions.assertEquals(historyManager.getHistory().size(), 1, "История просмотра задач отображается неверно");
        System.out.println();
    }

    private static void printHistory() {
        for (Task task : taskManager.getHistory()) {
            System.out.println(task);
        }
    }
}
