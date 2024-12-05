import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;

public class USprint8Task2Test {

    private static TaskManager taskManager;
    public final boolean isFileBacked = false;

    @BeforeAll
    static void beforeAll() {
        System.out.println("--------------------------------");
        System.out.println("Тестирование №2. Менеджеры задач");
        System.out.println("--------------------------------");
    }

    @BeforeEach
    public void beforeEach() {

        HistoryManager historyManager = Managers.getDefaultHistory();
        if (isFileBacked) {
            taskManager = Managers.getFileBackedTaskManager(historyManager, "D:/Projects/TmpFiles/file.csv");
        } else {
            taskManager = Managers.getDefault(historyManager);
        }

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
    }

    @AfterEach
    public void afterEach(){
        taskManager.removeTasks();
        taskManager.removeSubtasks();
        taskManager.removeEpics();
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
        Subtask subtask8 = new Subtask("Подзадача №8", "ОК-008", 4, 10, "2024-12-01 23:05");
        taskManager.addSubtask(subtask8);
        Assertions.assertNull(taskManager.getSubtaskByID(8), "Ошибка проверки пересечения интервалов");
        System.out.println("Валидатор отсек попытку создания подзадачи с идентификатором 8");
        System.out.println();
    }

    @Test
    public void checkUpdateDescription() {
        System.out.println(">> Изменить свойства задачи");
        Task task = taskManager.getTasks().getFirst();
        task.description = "MODIFIED-FIRST-ITEM";
        taskManager.updateTask(task);
        Assertions.assertEquals(task.description, "MODIFIED-FIRST-ITEM", "Ошибка редактирования");
        System.out.println("Свойства задачи успешно изменены");
        System.out.println();
    }

    @Test
    public void checkRemoveEpic() {
        System.out.println(">> Удалить эпик и все его подзадачи");
        Assertions.assertNotNull(taskManager.getSubtaskByID(5), "Ошибка удаления");
        Assertions.assertNotNull(taskManager.getSubtaskByID(6), "Ошибка удаления");
        Assertions.assertNotNull(taskManager.getEpicByID(3), "Ошибка удаления");
        taskManager.removeEpicByID(3);
        Assertions.assertNull(taskManager.getSubtaskByID(5), "Ошибка удаления");
        Assertions.assertNull(taskManager.getSubtaskByID(6), "Ошибка удаления");
        Assertions.assertNull(taskManager.getEpicByID(3), "Ошибка удаления");
        System.out.println();
    }

    @Test
    public void checkRemoveSubtask() {
        System.out.println(">> Удалить подзадачу и скорректировать свойство эпика");
        Assertions.assertNotNull(taskManager.getSubtaskByID(7), "Ошибка удаления");
        Assertions.assertFalse(taskManager.getEpicByID(4).getSubtaskIds().isEmpty(), "Ошибка удаления");
        taskManager.removeSubtaskByID(7);
        Assertions.assertNull(taskManager.getSubtaskByID(7), "Ошибка удаления");
        Assertions.assertTrue(taskManager.getEpicByID(4).getSubtaskIds().isEmpty(), "Ошибка удаления");
        System.out.println();
    }

}
