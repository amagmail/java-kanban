import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class TaskManagerTest {

    private static final HistoryManager historyManager = Managers.getDefaultHistory();
    private static final TaskManager taskManager = Managers.getDefault(historyManager);

    @BeforeAll
    static void beforeAll() {
        System.out.println("----------------------------");
        System.out.println("Тестирование менеджера задач");
        System.out.println("----------------------------");
    }

    @BeforeEach
    public void beforeEach() {

        // Создать новые задачи всех типов
        Task task1 = new Task("Базовая задача №1", "ОК-001");
        taskManager.addTask(task1);

        Task task2 = new Task("Базовая задача №2", "ОК-002");
        taskManager.addTask(task2);

        Epic epic3= new Epic("Эпик №3", "ОК-003");
        taskManager.addEpic(epic3);

        Epic epic4 = new Epic("Эпик №4", "ОК-004");
        taskManager.addEpic(epic4);

        Subtask subtask5 = new Subtask("Подзадача №5", "ОК-005", epic3.id);
        taskManager.addSubtask(subtask5);

        Subtask subtask6 = new Subtask("Подзадача №6", "ОК-006", epic3.id);
        taskManager.addSubtask(subtask6);

        Subtask subtask7 = new Subtask("Подзадача №7", "ОК-007", epic4.id);
        taskManager.addSubtask(subtask7);
    }

    @Test
    public void checkUpdateDescription() {
        System.out.println(">> Изменить свойство задачи");
        Task task = taskManager.getTasks().getFirst();
        task.description = "MODIFIED-FIRST-ITEM";
        taskManager.updateTask(task);
        Assertions.assertEquals(task.description, "MODIFIED-FIRST-ITEM", "Ошибка редактирования");
    }

    @Test
    public void checkUpdateStatuses() {
        System.out.println(">> Изменить статус задачи");
        Subtask subtask = taskManager.getSubtasks().getFirst();
        subtask.status = StatusTask.DONE;
        taskManager.updateSubtask(subtask);
        Assertions.assertEquals(taskManager.getEpicByID(subtask.getEpicId()).getStatus(), StatusTask.IN_PROGRESS, "Ошибка редактирования");
    }

    @Test
    public void checkEpicSubtasks() {
        System.out.println(">> Получить подзадачи эпика");
        List<Integer> subtaskIds = taskManager.getEpicByID(4).getSubtaskIds();
        List<Integer> checkIds = new ArrayList<>();
        checkIds.add(7);
        Assertions.assertEquals(subtaskIds, checkIds, "Ошибка редактирования");
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
    }

    @Test
    public void checkRemoveSubtask() {
        System.out.println(">> Удалить подзадачу и скорректировать свойство эпика");
        Assertions.assertNotNull(taskManager.getSubtaskByID(7), "Ошибка удаления");
        Assertions.assertFalse(taskManager.getEpicByID(4).getSubtaskIds().isEmpty(), "Ошибка удаления");
        taskManager.removeSubtaskByID(7);
        Assertions.assertNull(taskManager.getSubtaskByID(7), "Ошибка удаления");
        Assertions.assertTrue(taskManager.getEpicByID(4).getSubtaskIds().isEmpty(), "Ошибка удаления");
    }

}
