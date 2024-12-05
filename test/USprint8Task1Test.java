import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;

public class USprint8Task1Test {

    private static TaskManager taskManager;

    @BeforeAll
    static void beforeAll() {
        System.out.println("-------------------------------------");
        System.out.println("Тестирование №1. Расчет эпик статусов");
        System.out.println("-------------------------------------");
    }

    @BeforeEach
    public void beforeEach() {

        HistoryManager historyManager = Managers.getDefaultHistory();
        taskManager = Managers.getDefault(historyManager);

        Epic epic1= new Epic("Эпик №1", "ОК-001");
        taskManager.addEpic(epic1);

        Subtask subtask2 = new Subtask("Подзадача №2", "ОК-002", epic1.id);
        taskManager.addSubtask(subtask2);

        Subtask subtask3 = new Subtask("Подзадача №3", "ОК-003", epic1.id);
        taskManager.addSubtask(subtask3);

        Subtask subtask4 = new Subtask("Подзадача №4", "ОК-004", epic1.id);
        taskManager.addSubtask(subtask4);
    }

    @AfterEach
    public void afterEach(){
        taskManager.removeTasks();
        taskManager.removeSubtasks();
        taskManager.removeEpics();
    }

    @Test
    public void checkSubtasksNew() {
        System.out.println(">> Все подзадачи эпика со статусом NEW");
        Epic epic1 = taskManager.getEpicByID(1);
        Assertions.assertEquals(epic1.status, StatusTask.NEW, "Ошибка статуса");
    }

    @Test
    public void checkSubtasksDone() {
        System.out.println(">> Все подзадачи эпика со статусом DONE");

        Subtask subtask2 = taskManager.getSubtaskByID(2);
        subtask2.status = StatusTask.DONE;
        taskManager.updateSubtask(subtask2);

        Subtask subtask3 = taskManager.getSubtaskByID(3);
        subtask3.status = StatusTask.DONE;
        taskManager.updateSubtask(subtask3);

        Subtask subtask4 = taskManager.getSubtaskByID(4);
        subtask4.status = StatusTask.DONE;
        taskManager.updateSubtask(subtask4);

        Epic epic1 = taskManager.getEpicByID(1);
        Assertions.assertEquals(epic1.status, StatusTask.DONE, "Ошибка статуса");
    }

    @Test
    public void checkSubtasksNewAndDone() {
        System.out.println(">> Подзадачи эпика со статусом NEW и DONE");

        Subtask subtask2 = taskManager.getSubtaskByID(2);
        subtask2.status = StatusTask.DONE;
        taskManager.updateSubtask(subtask2);

        Epic epic1 = taskManager.getEpicByID(1);
        Assertions.assertEquals(epic1.status, StatusTask.IN_PROGRESS, "Ошибка статуса");
    }

    @Test
    public void checkSubtasksInProgress() {
        System.out.println(">> Подзадачи эпика со статусом IN_PROGRESS");

        Subtask subtask2 = taskManager.getSubtaskByID(2);
        subtask2.status = StatusTask.IN_PROGRESS;
        taskManager.updateSubtask(subtask2);

        Epic epic1 = taskManager.getEpicByID(1);
        Assertions.assertEquals(epic1.status, StatusTask.IN_PROGRESS, "Ошибка статуса");
    }

}
