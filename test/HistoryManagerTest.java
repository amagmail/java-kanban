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

        Epic epic1 = new Epic("Эпик №1", "ОК-003");
        taskManager.addEpic(epic1);

        Epic epic2 = new Epic("Эпик №2", "ОК-004");
        taskManager.addEpic(epic2);

        Subtask subtask1 = new Subtask("Подзадача №1", "ОК-005", epic1.id);
        taskManager.addSubtask(subtask1);

        Subtask subtask2 = new Subtask("Подзадача №2", "ОК-006", epic1.id);
        taskManager.addSubtask(subtask2);

        Subtask subtask3 = new Subtask("Подзадача №3", "ОК-007", epic2.id);
        taskManager.addSubtask(subtask3);

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
    public void CheckFirstItemHistory() {
        System.out.println(">> Получить первый элемент истории");
        Assertions.assertEquals(historyManager.getHistory().getFirst(), taskManager.getEpicByID(3), "История просмотра задач отображается неверно");
    }

    @Test
    public void CheckLastItemHistory() {
        System.out.println(">> Получить последний элемент истории");
        Assertions.assertEquals(historyManager.getHistory().getLast(), taskManager.getSubtaskByID(6), "История просмотра задач отображается неверно");
    }

    @Test
    public void CheckTheNumberOfItems() {
        System.out.println(">> Получить количество элементов истории");
        Assertions.assertTrue(historyManager.getHistory().size() <= 10, "История просмотра задач отображается неверно");
    }

}
