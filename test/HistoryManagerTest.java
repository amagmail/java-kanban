import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class HistoryManagerTest {

    private static final HistoryManager historyManager = Managers.getDefaultHistory();
    private static final TaskManager taskManager = Managers.getDefault(historyManager);

    @BeforeAll
    static void beforeAll() {
        System.out.println("------------------------------------");
        System.out.println("Тестирование истории просмотра задач");
        System.out.println("------------------------------------");
    }

    @BeforeEach
    public void beforeEach() {

        // Создать новые задачи всех типов
        Task task1 = new Task("Базовая задача №1", "ОК-001");
        taskManager.addTask(task1);

        Task task2 = new Task("Базовая задача №2", "ОК-002");
        taskManager.addTask(task2);

        Epic epic3 = new Epic("Эпик №3", "ОК-003");
        taskManager.addEpic(epic3);

        Epic epic4 = new Epic("Эпик №4", "ОК-004");
        taskManager.addEpic(epic4);

        Subtask subtask5 = new Subtask("Подзадача №5", "ОК-005", epic3.id);
        taskManager.addSubtask(subtask5);

        Subtask subtask6 = new Subtask("Подзадача №6", "ОК-006", epic3.id);
        taskManager.addSubtask(subtask6);

        Subtask subtask7 = new Subtask("Подзадача №7", "ОК-007", epic4.id);
        taskManager.addSubtask(subtask7);

        // Сформировать историю просмотра задач
        taskManager.getTaskByID(2);
        taskManager.getEpicByID(3);
        taskManager.getSubtaskByID(6);
        taskManager.getSubtaskByID(6);
        taskManager.getSubtaskByID(6);
        taskManager.getSubtaskByID(6);
        taskManager.getSubtaskByID(6);
        taskManager.getSubtaskByID(6);
        taskManager.getSubtaskByID(6);
        taskManager.getSubtaskByID(6);
        taskManager.getSubtaskByID(6);
    }

    @Test
    public void checkFirstItemHistory() {
        System.out.println(">> Получить первый элемент истории");
        Assertions.assertEquals(historyManager.getHistory().getFirst(), taskManager.getEpicByID(3), "История просмотра задач отображается неверно");
    }

    @Test
    public void checkLastItemHistory() {
        System.out.println(">> Получить последний элемент истории");
        Assertions.assertEquals(historyManager.getHistory().getLast(), taskManager.getSubtaskByID(6), "История просмотра задач отображается неверно");
    }

    @Test
    public void checkTheNumberOfItems() {
        System.out.println(">> Получить количество элементов истории");
        Assertions.assertTrue(historyManager.getHistory().size() <= 10, "История просмотра задач отображается неверно");
    }

}
