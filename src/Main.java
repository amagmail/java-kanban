import java.util.List;

public class Main {

    public static void main(String[] args) {

        HistoryManager historyManager = Managers.getDefaultHistory();
        TaskManager taskManager = Managers.getDefault(historyManager);

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

        // Тестирование редактирования свойств
        task2.description = "MODIFIED-002";
        taskManager.updateTask(task2);

        epic3.description = "MODIFIED-004";
        taskManager.updateEpic(epic3);

        subtask6.description = "MODIFIED-006";
        taskManager.updateSubtask(subtask6);

        // Тестирование редактирования статусов
        task1.status = StatusTask.IN_PROGRESS;
        taskManager.updateTask(task1);

        task2.status = StatusTask.DONE;
        taskManager.updateTask(task2);

        subtask5.status = StatusTask.DONE;
        taskManager.updateSubtask(subtask5);

        // Вывести на печать все задачи
        System.out.println();
        System.out.println(">> Вывести на печать все задачи");
        taskManager.printTasks();
        taskManager.printEpics();
        taskManager.printSubtasks();

        // Работа с задачами
        System.out.println();
        int searchId = 3;
        System.out.println(">> Результат поиска элемента типа Epic по идентификатору " + searchId);
        System.out.println(taskManager.getEpicByID(searchId));
        System.out.println(">> Получить список всех подзадач элемента типа Epic по идентификатору " + searchId);
        taskManager.printSubtasks();

        System.out.println();
        searchId = 4;
        System.out.println(">> Результат поиска элемента типа Epic по идентификатору " + searchId);
        System.out.println(taskManager.getEpicByID(searchId));
        System.out.println(">> Удалить элемент типа Epic по идентификатору " + searchId);
        taskManager.removeEpicByID(searchId);

        System.out.println();
        searchId = 6;
        System.out.println(">> Результат поиска элемента типа Subtask по идентификатору " + searchId);
        System.out.println(taskManager.getSubtaskByID(searchId));
        System.out.println(">> Удалить элемент типа Subtask по идентификатору " + searchId);
        taskManager.removeSubtaskByID(searchId);

        // Вывести на печать все оставшиеся задачи
        System.out.println();
        System.out.println(">> Вывести на печать все оставшиеся задачи");
        taskManager.printTasks();
        taskManager.printEpics();
        taskManager.printSubtasks();

        // Получить историю просмотра задач
        System.out.println();
        taskManager.getTaskByID(1);
        taskManager.getTaskByID(2);
        taskManager.getEpicByID(3);
        taskManager.getSubtaskByID(5);
        taskManager.getSubtaskByID(5);
        taskManager.getSubtaskByID(5);
        taskManager.getSubtaskByID(5);
        taskManager.getSubtaskByID(5);
        taskManager.getSubtaskByID(5);
        taskManager.getSubtaskByID(5);
        taskManager.getSubtaskByID(5);
        historyManager.printHistory();

        List<Task> history = taskManager.getHistory();
        System.out.println();
        System.out.println(history);
        history.removeFirst();
        history.removeFirst();
        history.removeFirst();
        history.removeFirst();
        history.removeFirst();
        history.removeFirst();
        history.removeFirst();
        history.removeFirst();
        history.removeFirst();
        System.out.println(history);
        history = taskManager.getHistory();
        System.out.println(history);

        // Тестовая утилита печати всех задач
        printAllTasks(taskManager);
    }

    private static void printAllTasks(TaskManager manager) {

        System.out.println();
        System.out.println("Задачи:");
        for (Task task : manager.getTasks()) {
            System.out.println(task);
        }

        System.out.println();
        System.out.println("Эпики:");
        for (Task epic : manager.getEpics()) {
            System.out.println(epic);
            for (Task task : manager.getEpicSubtasksByID(epic.id)) {
                System.out.println("--> " + task);
            }
        }

        System.out.println();
        System.out.println("Подзадачи:");
        for (Task subtask : manager.getSubtasks()) {
            System.out.println(subtask);
        }

        System.out.println();
        System.out.println("История:");
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }
    }
}
