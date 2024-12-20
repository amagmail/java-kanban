import com.sun.net.httpserver.HttpServer;
import java.io.*;
import java.net.InetSocketAddress;

public class HttpTaskServer {

    private static final int PORT = 8080;

    private final TaskManager taskManager;

    private HttpServer httpServer;

    public HttpTaskServer(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    public void start() throws IOException {
        httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/tasks", new TasksHandler(taskManager));
        httpServer.createContext("/subtasks", new SubtasksHandler(taskManager));
        httpServer.createContext("/epics", new EpicsHandler(taskManager));
        httpServer.createContext("/history", new HistoryHandler(taskManager));
        httpServer.createContext("/prioritized", new PrioritizedHandler(taskManager));
        httpServer.start();
        System.out.println("HTTP-сервер запущен на " + PORT + " порту");
    }

    public void stop() {
        httpServer.stop(1);
        System.out.println("HTTP-сервер остановлен на " + PORT + " порту");
    }

    public static void main(String[] args) throws IOException {

        HistoryManager historyManager = Managers.getDefaultHistory();
        TaskManager taskManager = Managers.getDefault(historyManager);

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

        printTasks(taskManager);

        HttpTaskServer taskServer = new HttpTaskServer(taskManager);
        taskServer.start();
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
        System.out.println();
    }
}

