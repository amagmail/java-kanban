public class Main {

    public static void main(String[] args) {

        //Спринт 9

        //1. InMemoryTaskManager
        HistoryManager historyManager = Managers.getDefaultHistory();
        TaskManager taskManager = Managers.getDefault(historyManager);

        //2. FileBackedTaskManager
        //HistoryManager historyManager = Managers.getDefaultHistory();
        //TaskManager taskManager = Managers.getFileBackedTaskManager(historyManager, "D:/Projects/TmpFiles/file.csv");

        // Создать новые задачи всех типов
        Task task1 = new Task("Базовая задача №1", "ОК-001", 30, Managers.stringToDate("2024-12-01 20:00"));
        taskManager.addTask(task1);

        Task task2 = new Task("Базовая задача №2", "ОК-002", 30, Managers.stringToDate("2024-12-01 20:30"));
        taskManager.addTask(task2);

        Epic epic3 = new Epic("Эпик №3", "ОК-003");
        taskManager.addEpic(epic3);

        Epic epic4 = new Epic("Эпик №4", "ОК-004");
        taskManager.addEpic(epic4);

        Subtask subtask5 = new Subtask("Подзадача №5", "ОК-005", epic3.id, 10, Managers.stringToDate("2024-12-01 21:00"));
        taskManager.addSubtask(subtask5);

        Subtask subtask6 = new Subtask("Подзадача №6", "ОК-006", epic3.id, 10, Managers.stringToDate("2024-12-01 22:00"));
        taskManager.addSubtask(subtask6);

        Subtask subtask7 = new Subtask("Подзадача №7", "ОК-007", epic4.id, 10, Managers.stringToDate("2024-12-01 23:00"));
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

        // Вывести на печать задачи
        printTasks(taskManager);

        // Работа с задачами
        System.out.println();
        int searchId = 3;
        System.out.println(">> Результат поиска элемента типа Epic по идентификатору " + searchId);
        System.out.println(taskManager.getEpicByID(searchId));
        System.out.println(">> Получить список всех подзадач элемента типа Epic по идентификатору " + searchId);
        for (Task subtask : taskManager.getEpicSubtasksByID(searchId)) {
            System.out.println(subtask);
        }

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

        // Вывести на печать оставшиеся задачи
        printTasks(taskManager);

        // Изменить историю просмотра задач
        taskManager.getTaskByID(1);
        taskManager.getTaskByID(2);
        taskManager.getEpicByID(3);
        taskManager.getSubtaskByID(5);
        taskManager.getSubtaskByID(5);
        taskManager.getSubtaskByID(5);
        taskManager.getEpicByID(3);
        taskManager.getSubtaskByID(5);
        taskManager.getSubtaskByID(5);
        taskManager.getSubtaskByID(5);
        taskManager.getTaskByID(2);

        // Вывести на печать историю просмотра
        System.out.println();
        printHistory(taskManager);

        // Удалить эпик из истории просмотра
        System.out.println();
        taskManager.removeEpicByID(3);

        // История просмотра после удаления эпика
        System.out.println();
        printHistory(taskManager);
    }

    private static void printTasks(TaskManager taskManager) {
        if (taskManager == null) {
            return;
        }
        System.out.println();
        System.out.println(">> Вывести на печать задачи");
        for (Task task : taskManager.getTasks()) {
            System.out.println(task);
        }
        System.out.println();
        System.out.println(">> Вывести на печать эпики с подзадачами");
        for (Task epic : taskManager.getEpics()) {
            System.out.println(epic);
            for (Task subtask : taskManager.getEpicSubtasksByID(epic.id)) {
                System.out.println("* " + subtask);
            }
        }
    }

    private static void printHistory(TaskManager taskManager) {
        if (taskManager == null) {
            return;
        }
        System.out.println(">> История");
        for (Task task : taskManager.getHistory()) {
            System.out.println(task);
        }
    }

}
