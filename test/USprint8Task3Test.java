import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class USprint8Task3Test {

    private static HistoryManager historyManager = Managers.getDefaultHistory();
    private static TaskManager taskManager = Managers.getDefault(historyManager);

    @BeforeAll
    static void beforeAll() {
        System.out.println("----------------------------------------");
        System.out.println("Тестирование №3. История просмотра задач");
        System.out.println("----------------------------------------");
    }

    @BeforeEach
    public void beforeEach() {

        historyManager = Managers.getDefaultHistory();
        taskManager = Managers.getDefault(historyManager);

        Task task1 = new Task("Базовая задача №1", "ОК-001", 30, "2024-12-01 20:00");
        taskManager.addTask(task1);

        Task task2 = new Task("Базовая задача №2", "ОК-002", 30, "2024-12-01 20:30");
        taskManager.addTask(task2);

        Epic epic3 = new Epic("Эпик №3", "ОК-003");
        taskManager.addEpic(epic3);

        Epic epic4 = new Epic("Эпик №4", "ОК-004");
        taskManager.addEpic(epic4);

        Subtask subtask5 = new Subtask("Подзадача №5", "ОК-005", epic3.id, 10, "2024-12-01 21:00");
        taskManager.addSubtask(subtask5);

        Subtask subtask6 = new Subtask("Подзадача №6", "ОК-006", epic3.id, 10, "2024-12-01 22:00");
        taskManager.addSubtask(subtask6);

        Subtask subtask7 = new Subtask("Подзадача №7", "ОК-007", epic4.id, 10, "2024-12-01 23:00");
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
        System.out.println(">> Получить первый элемент истории: id = " + historyManager.getHistory().getFirst().id);
        printHistory();
        Assertions.assertEquals(historyManager.getHistory().getFirst(), taskManager.getTaskByID(2), "История просмотра задач отображается неверно");
        System.out.println();
    }

    @Test
    public void checkLastItemHistory() {
        System.out.println(">> Получить последний элемент истории: id = " + historyManager.getHistory().getLast().id);
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
