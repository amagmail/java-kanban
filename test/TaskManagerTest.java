import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

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
    }

    @Test
    public void CheckUpdateDescription() {
        System.out.println(">> Изменить свойство задачи");
        Task task = taskManager.getTasks().getFirst();
        task.description = "MODIFIED-FIRST-ITEM";
        taskManager.updateTask(task);
        Assertions.assertEquals(task.description, "MODIFIED-FIRST-ITEM", "Ошибка редактирования");
    }

    @Test
    public void CheckUpdateStatuses() {
        System.out.println(">> Изменить статус задачи");
        Subtask subtask = taskManager.getSubtasks().getFirst();
        subtask.status = StatusTask.DONE;
        taskManager.updateSubtask(subtask);
        Assertions.assertEquals(taskManager.getEpicByID(subtask.getEpicId()).getStatus(), StatusTask.IN_PROGRESS, "Ошибка редактирования");
    }

    @Test
    public void CheckEpicSubtasks() {
        System.out.println(">> Получить подзадачи эпика");
        ArrayList<Integer> subtaskIds = taskManager.getEpicByID(3).getSubtaskIds();
        ArrayList<Integer> checkIds = new ArrayList<>();
        checkIds.add(5);
        checkIds.add(6);
        Assertions.assertEquals(subtaskIds, checkIds, "Ошибка редактирования");
    }

    @Test
    public void CheckRemoveEpic() {
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
    public void CheckRemoveSubtask() {
        System.out.println(">> Удалить подзадачу и скорректировать свойство эпика");
        Assertions.assertNotNull(taskManager.getSubtaskByID(7), "Ошибка удаления");
        Assertions.assertFalse(taskManager.getEpicByID(4).getSubtaskIds().isEmpty(), "Ошибка удаления");
        taskManager.removeSubtaskByID(7);
        Assertions.assertNull(taskManager.getSubtaskByID(7), "Ошибка удаления");
        Assertions.assertTrue(taskManager.getEpicByID(4).getSubtaskIds().isEmpty(), "Ошибка удаления");
    }

}
