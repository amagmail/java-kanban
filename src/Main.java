public class Main {
    public static void main(String[] args) {

        TaskManager taskManager = new TaskManager();

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

        // Тестирование редактирования свойств
        task2.description = "MODIFIED-002";
        taskManager.updateTask(task2);

        epic2.description = "MODIFIED-004";
        taskManager.updateEpic(epic2);

        subtask2.description = "MODIFIED-006";
        taskManager.updateSubtask(subtask2);

        // Тестирование редактирования статусов
        task1.status = StatusTask.IN_PROGRESS;
        taskManager.updateTask(task1);

        task2.status = StatusTask.DONE;
        taskManager.updateTask(task2);

        subtask1.status = StatusTask.DONE;
        taskManager.updateSubtask(subtask1);

        // Вывести на печать все задачи
        System.out.println();
        System.out.println(">> Вывести на печать все задачи");
        taskManager.printTasks(taskManager.getTasks());
        taskManager.printEpics(taskManager.getEpics());
        taskManager.printSubtasks(taskManager.getSubtasks());

        // Работа с задачами
        System.out.println();
        int searchId = 3;
        System.out.println(">> Результат поиска элемента типа Epic по идентификатору " + searchId);
        System.out.println(taskManager.getEpicByID(searchId));
        System.out.println(">> Получить список всех подзадач элемента типа Epic по идентификатору " + searchId);
        taskManager.printSubtasks(taskManager.getEpicSubtasksByID(searchId));

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

        taskManager.getTaskByID(1);

        // Вывести на печать все оставшиеся задачи
        System.out.println();
        System.out.println(">> Вывести на печать все оставшиеся задачи");
        taskManager.printTasks(taskManager.getTasks());
        taskManager.printEpics(taskManager.getEpics());
        taskManager.printSubtasks(taskManager.getSubtasks());
    }
}
