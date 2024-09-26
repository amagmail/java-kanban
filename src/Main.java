public class Main {
    public static void main(String[] args) {

        TaskManager taskManager = new TaskManager();

        // Создать новые задачи всех типов
        ElementTask task1 = new ElementTask("Базовая задача №1", "ОК-001");
        taskManager.addTask(task1);

        ElementTask task2 = new ElementTask("Базовая задача №2", "ОК-002");
        taskManager.addTask(task2);

        ElementEpic epic1 = new ElementEpic("Эпик №1", "ОК-003");
        taskManager.addEpic(epic1);

        ElementEpic epic2 = new ElementEpic("Эпик №2", "ОК-004");
        taskManager.addEpic(epic2);

        ElementSubtask subtask1 = new ElementSubtask("Подзадача №1", "ОК-005", epic1.id);
        taskManager.addSubtask(subtask1);

        ElementSubtask subtask2 = new ElementSubtask("Подзадача №2", "ОК-006", epic1.id);
        taskManager.addSubtask(subtask2);

        ElementSubtask subtask3 = new ElementSubtask("Подзадача №3", "ОК-007", epic2.id);
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
        taskManager.getTasks();
        taskManager.getEpics();
        taskManager.getSubtasks();

        // Работа с задачами
        System.out.println();
        int searchId = 3;
        System.out.println(">> Результат поиска ElementEpic по идентификатору " + searchId);
        taskManager.getEpicByID(searchId);
        System.out.println(">> Получить список всех подзадач ElementEpic по идентификатору " + searchId);
        taskManager.getEpicSubtaskByID(searchId);

        System.out.println();
        searchId = 4;
        System.out.println(">> Результат поиска ElementEpic по идентификатору " + searchId);
        taskManager.getEpicByID(searchId);
        System.out.println(">> Удалить ElementEpic по идентификатору " + searchId);
        taskManager.removeEpicByID(searchId);

        System.out.println();
        searchId = 6;
        System.out.println(">> Результат поиска ElementSubtask по идентификатору " + searchId);
        taskManager.getSubtaskByID(searchId);
        System.out.println(">> Удалить ElementSubtask по идентификатору " + searchId);
        taskManager.removeSubtaskByID(searchId);

        // Вывести на печать все оставшиеся задачи
        System.out.println();
        System.out.println(">> Вывести на печать все оставшиеся задачи");
        taskManager.getTasks();
        taskManager.getEpics();
        taskManager.getSubtasks();
    }
}
