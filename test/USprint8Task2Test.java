import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class USprint8Task2Test {

    private static HistoryManager historyManager = Managers.getDefaultHistory();
    private static TaskManager taskManager = Managers.getDefault(historyManager);

    @BeforeAll
    static void beforeAll() {
        System.out.println("-------------------------------");
        System.out.println("Тестирование №2. Менеджер задач");
        System.out.println("-------------------------------");
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

        Subtask subtask8 = new Subtask("Подзадача №8", "ОК-008", epic4.id, 10, "2024-12-01 23:05");
        taskManager.addSubtask(subtask8);
    }

    @Test
    public void checkSubtaskEpicExists() {
        System.out.println(">> Проверка наличия эпика у подзадач");
        Subtask subtask5 = taskManager.getSubtaskByID(5);
        Assertions.assertEquals(subtask5.getEpicId(), 3, "Ошибка проверки наличия эпика");
        System.out.println("Подзадача с идентификатором 5 привязана к эпику с идентификатором 3");

        Subtask subtask6 = taskManager.getSubtaskByID(6);
        Assertions.assertEquals(subtask6.getEpicId(), 3, "Ошибка проверки наличия эпика");
        System.out.println("Подзадача с идентификатором 6 привязана к эпику с идентификатором 3");
        System.out.println();
    }

    @Test
    public void checkEpicStatusActualization() {
        System.out.println(">> Проверка актуализации статуса эпика");
        Epic epic3 = taskManager.getEpicByID(3);
        Assertions.assertEquals(epic3.status, StatusTask.NEW, "Ошибка статуса");
        System.out.println("Создан эпик с двумя подзадачами, эпик в статусе NEW");

        Subtask subtask5 = taskManager.getSubtaskByID(5);
        subtask5.status = StatusTask.DONE;
        taskManager.updateSubtask(subtask5);
        Assertions.assertEquals(epic3.status, StatusTask.IN_PROGRESS, "Ошибка статуса");
        System.out.println("Выполнена первая подзадача, эпик в статусе IN_PROGRESS");

        Subtask subtask6 = taskManager.getSubtaskByID(6);
        subtask6.status = StatusTask.DONE;
        taskManager.updateSubtask(subtask6);
        Assertions.assertEquals(epic3.status, StatusTask.DONE, "Ошибка статуса");
        System.out.println("Выполнена вторая подзадача, эпик в статусе DONE");
        System.out.println();
    }

    @Test
    public void checkValidationProcess() {
        System.out.println(">> Проверка пересечение интервалов");
        Subtask subtask8 = taskManager.getSubtaskByID(8);
        Assertions.assertNull(subtask8, "Ошибка проверки пересечения интервалов");
        System.out.println("Валидатор отсек попытку создания подзадачи с идентификатором 8");
        System.out.println();
    }

}
