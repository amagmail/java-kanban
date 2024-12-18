import com.google.gson.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

public class HttpTaskServer {

    private static final int PORT = 8080;

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

        HttpServer httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/tasks", new TasksHandler(taskManager));
        httpServer.createContext("/subtasks", new SubtasksHandler(taskManager));
        httpServer.createContext("/epics", new EpicsHandler(taskManager));
        httpServer.createContext("/history", new HistoryHandler(taskManager));
        httpServer.createContext("/prioritized", new PrioritizeHandler(taskManager));

        httpServer.start();
        System.out.println("HTTP-сервер запущен на " + PORT + " порту");

        //httpServer.stop(1);
        //System.out.println("HTTP-сервер остановлен на " + PORT + " порту");

        printTasks(taskManager);
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
}

class BaseHttpHandler implements HttpHandler {

    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    protected TaskManager taskManager;

    public BaseHttpHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        System.out.println("OK-BaseHttpHandler");
        writeResponse(exchange, "OK-BaseHttpHandler", 200);
    }

    protected void writeResponse(HttpExchange exchange, String responseString, int responseCode) throws IOException {
        try (OutputStream os = exchange.getResponseBody()) {
            exchange.sendResponseHeaders(responseCode, 0);
            if (responseString != null) {
                os.write(responseString.getBytes(DEFAULT_CHARSET));
            }
        }
        exchange.close();
    }

    protected JsonElement getRequestBody(HttpExchange exchange) throws IOException {
        InputStreamReader isr =  new InputStreamReader(exchange.getRequestBody(), DEFAULT_CHARSET);
        BufferedReader br = new BufferedReader(isr);
        String strVal = br.readLine();
        JsonElement jsonElement;
        try {
            jsonElement = JsonParser.parseString(strVal);
        } catch (Exception e) {
            jsonElement = null;
        }
        return jsonElement;
    }

    protected Optional<Integer> getPathParam(HttpExchange exchange) {
        String[] pathParts = exchange.getRequestURI().getPath().split("/");
        try {
            return Optional.of(Integer.parseInt(pathParts[2]));
        } catch (NumberFormatException exception) {
            return Optional.empty();
        }
    }
}

class TasksHandler extends BaseHttpHandler {

    public TasksHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        String[] pathParts = path.split("/");
        Gson gson = new Gson();
        switch (method) {
            case "GET":
                if (pathParts.length == 2) {
                    List<Task> tasks = taskManager.getTasks();
                    writeResponse(exchange, tasks.toString(), 200);
                } else if (pathParts.length == 3) {
                    Optional<Integer> optionVal = getPathParam(exchange);
                    if(optionVal.isEmpty()) {
                        writeResponse(exchange, "Некорректный идентификатор задачи", 400);
                        return;
                    }
                    int id = optionVal.get();
                    Task task = taskManager.getTaskByID(id);
                    if (task != null) {
                        //System.out.println(task.toString());
                        //Type taskType = new TypeToken<Task>(){}.getType();
                        //String strVal = gson.toJson(task, taskType);
                        //System.out.println(strVal);
                        writeResponse(exchange, task.toString(), 200);
                    } else {
                        writeResponse(exchange, "Не найдена задача с идентификатором " + id, 404);
                    }
                } else {
                    writeResponse(exchange, "Сервер не обслуживает эндпоинт " + method + path, 501);
                }
                break;
            case "POST":
                if (pathParts.length == 2) {
                    JsonElement jsonElement = getRequestBody(exchange);
                    if (jsonElement != null && jsonElement.isJsonObject()) {
                        String inputTitle;
                        String inputDescription;
                        int inputMinutes;
                        String inputDate;
                        Task task;
                        JsonObject jsonObject = jsonElement.getAsJsonObject();
                        if (jsonObject.has("id")) {
                            int id = jsonObject.get("id").getAsInt();
                            task = taskManager.getTaskByID(id);
                            if (task != null) {
                                if (jsonObject.has("title")) {
                                    task.title = jsonObject.get("title").getAsString();
                                }
                                if (jsonObject.has("description")) {
                                    task.description = jsonObject.get("description").getAsString();
                                }
                                taskManager.updateTask(task);
                                writeResponse(exchange, task.toString(), 200);
                            } else {
                                writeResponse(exchange, "Не найдена задача с идентификатором " + id, 404);
                            }
                        } else {
                            if (!jsonObject.has("title")) {
                                writeResponse(exchange, "Из тела запроса не удалось извлечь обязательный параметр title", 404);
                                break;
                            }
                            if (!jsonObject.has("description")) {
                                writeResponse(exchange, "Из тела запроса не удалось извлечь обязательный параметр description", 404);
                                break;
                            }
                            if (!jsonObject.has("minutes")) {
                                writeResponse(exchange, "Из тела запроса не удалось извлечь обязательный параметр minutes", 404);
                                break;
                            }
                            if (!jsonObject.has("date")) {
                                writeResponse(exchange, "Из тела запроса не удалось извлечь обязательный параметр date", 404);
                                break;
                            }
                            inputTitle = jsonObject.get("title").getAsString();
                            inputDescription = jsonObject.get("description").getAsString();
                            inputMinutes = jsonObject.get("minutes").getAsInt();
                            inputDate = jsonObject.get("date").getAsString();
                            task = new Task(inputTitle, inputDescription, inputMinutes, Managers.stringToDate(inputDate));
                            if (taskManager.isValid(task)) {
                                taskManager.addTask(task);
                                writeResponse(exchange, task.toString(), 200);
                            } else {
                                writeResponse(exchange, "Добавляемая задача пересекается с существующей", 406);
                            }
                        }
                    } else {
                        writeResponse(exchange, "Не удалось извлечь тело запроса", 400);
                    }
                } else {
                    writeResponse(exchange, "Сервер не обслуживает эндпоинт " + method + path, 501);
                }
                break;
            case "DELETE":
                if (pathParts.length == 3) {
                    Optional<Integer> optionVal = getPathParam(exchange);
                    if(optionVal.isEmpty()) {
                        writeResponse(exchange, "Некорректный идентификатор задачи", 400);
                    } else {
                        int id = optionVal.get();
                        Task task = taskManager.getTaskByID(id);
                        if (task != null) {
                            taskManager.removeTaskByID(id);
                            writeResponse(exchange, null, 201);
                        } else {
                            writeResponse(exchange, "Не найдена задача с идентификатором " + id, 404);
                        }
                    }
                } else {
                    writeResponse(exchange, "Сервер не обслуживает эндпоинт " + method + path, 501);
                }
                break;
            default:
                writeResponse(exchange, "Сервер не обслуживает метод " + method, 501);
        }
    }
}

class SubtasksHandler extends BaseHttpHandler {

    public SubtasksHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        String[] pathParts = path.split("/");
        Gson gson = new Gson();
        switch (method) {
            case "GET":
                if (pathParts.length == 2) {
                    List<Subtask> subtasks = taskManager.getSubtasks();
                    writeResponse(exchange, subtasks.toString(), 200);
                } else if (pathParts.length == 3) {
                    Optional<Integer> optionVal = getPathParam(exchange);
                    if(optionVal.isEmpty()) {
                        writeResponse(exchange, "Некорректный идентификатор подзадачи", 400);
                        return;
                    }
                    int id = optionVal.get();
                    Subtask subtask = taskManager.getSubtaskByID(id);
                    if (subtask != null) {
                        writeResponse(exchange, subtask.toString(), 200);
                    } else {
                        writeResponse(exchange, "Не найдена подзадача с идентификатором " + id, 404);
                    }
                } else {
                    writeResponse(exchange, "Сервер не обслуживает эндпоинт " + method + path, 501);
                }
                break;
            case "POST":
                if (pathParts.length == 2) {
                    JsonElement jsonElement = getRequestBody(exchange);
                    if (jsonElement != null && jsonElement.isJsonObject()) {
                        String inputTitle;
                        String inputDescription;
                        int inputEpicID;
                        int inputMinutes;
                        String inputDate;
                        Subtask subtask;
                        JsonObject jsonObject = jsonElement.getAsJsonObject();
                        if (jsonObject.has("id")) {
                            int id = jsonObject.get("id").getAsInt();
                            subtask = taskManager.getSubtaskByID(id);
                            if (subtask != null) {
                                if (jsonObject.has("title")) {
                                    String q2 = jsonObject.get("title").getAsString();
                                    subtask.title = jsonObject.get("title").getAsString();
                                }
                                if (jsonObject.has("description")) {
                                    subtask.description = jsonObject.get("description").getAsString();
                                }
                                taskManager.updateSubtask(subtask);
                                writeResponse(exchange, subtask.toString(), 200);
                            } else {
                                writeResponse(exchange, "Не найдена подзадача с идентификатором " + id, 404);
                            }
                        } else {
                            if (!jsonObject.has("title")) {
                                writeResponse(exchange, "Из тела запроса не удалось извлечь обязательный параметр title", 404);
                                break;
                            }
                            if (!jsonObject.has("description")) {
                                writeResponse(exchange, "Из тела запроса не удалось извлечь обязательный параметр description", 404);
                                break;
                            }
                            if (!jsonObject.has("minutes")) {
                                writeResponse(exchange, "Из тела запроса не удалось извлечь обязательный параметр minutes", 404);
                                break;
                            }
                            if (!jsonObject.has("date")) {
                                writeResponse(exchange, "Из тела запроса не удалось извлечь обязательный параметр date", 404);
                                break;
                            }
                            inputTitle = jsonObject.get("title").getAsString();
                            inputDescription = jsonObject.get("description").getAsString();
                            inputEpicID = jsonObject.get("epicId").getAsInt();
                            inputMinutes = jsonObject.get("minutes").getAsInt();
                            inputDate = jsonObject.get("date").getAsString();
                            subtask = new Subtask(inputTitle, inputDescription, inputEpicID, inputMinutes, Managers.stringToDate(inputDate));
                            if (taskManager.isValid(subtask)) {
                                taskManager.addSubtask(subtask);
                                writeResponse(exchange, subtask.toString(), 200);
                            } else {
                                writeResponse(exchange, "Добавляемая подзадача пересекается с существующей", 406);
                            }
                        }
                    } else {
                        writeResponse(exchange, "Не удалось извлечь тело запроса", 400);
                    }
                } else {
                    writeResponse(exchange, "Сервер не обслуживает эндпоинт " + method + path, 501);
                }
                break;
            case "DELETE":
                if (pathParts.length == 3) {
                    Optional<Integer> optionVal = getPathParam(exchange);
                    if(optionVal.isEmpty()) {
                        writeResponse(exchange, "Некорректный идентификатор задачи", 400);
                    } else {
                        int id = optionVal.get();
                        Subtask subtask = taskManager.getSubtaskByID(id);
                        if (subtask != null) {
                            taskManager.removeSubtaskByID(id);
                            writeResponse(exchange, null, 201);
                        } else {
                            writeResponse(exchange, "Не найдена подзадача с идентификатором " + id, 404);
                        }
                    }
                } else {
                    writeResponse(exchange, "Сервер не обслуживает эндпоинт " + method + path, 501);
                }
                break;
            default:
                writeResponse(exchange, "Сервер не обслуживает метод " + method, 501);
        }
    }
}

class EpicsHandler extends BaseHttpHandler {

    public EpicsHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        String[] pathParts = path.split("/");
        Gson gson = new Gson();
        switch (method) {
            case "GET":
                if (pathParts.length == 2) {
                    List<Epic> epics = taskManager.getEpics();
                    writeResponse(exchange, epics.toString(), 200);
                } else if (pathParts.length == 3 || (pathParts.length == 4 && pathParts[3].equals("subtasks"))) {
                    Optional<Integer> optionVal = getPathParam(exchange);
                    if(optionVal.isEmpty()) {
                        writeResponse(exchange, "Некорректный идентификатор эпика'", 400);
                        return;
                    }
                    int id = optionVal.get();
                    Epic epic = taskManager.getEpicByID(id);
                    if (epic != null) {
                        if (pathParts.length == 3) {
                            writeResponse(exchange, epic.toString(), 200);
                        } else {
                            List<Subtask> subtasks = taskManager.getEpicSubtasksByID(id);
                            writeResponse(exchange, subtasks.toString(), 200);
                        }
                    } else {
                        writeResponse(exchange, "Не найден эпик с идентификатором " + id, 404);
                    }
                } else {
                    writeResponse(exchange, "Сервер не обслуживает эндпоинт " + method + path, 501);
                }
                break;
            case "POST":
                if (pathParts.length == 2) {
                    JsonElement jsonElement = getRequestBody(exchange);
                    if (jsonElement != null && jsonElement.isJsonObject()) {
                        String inputTitle;
                        String inputDescription;
                        Epic epic;
                        JsonObject jsonObject = jsonElement.getAsJsonObject();
                        if (jsonObject.has("id")) {
                            int id = jsonObject.get("id").getAsInt();
                            epic = taskManager.getEpicByID(id);
                            if (epic != null) {
                                if (jsonObject.has("title")) {
                                    String q2 = jsonObject.get("title").getAsString();
                                    epic.title = jsonObject.get("title").getAsString();
                                }
                                if (jsonObject.has("description")) {
                                    epic.description = jsonObject.get("description").getAsString();
                                }
                                taskManager.updateEpic(epic);
                                writeResponse(exchange, epic.toString(), 200);
                            } else {
                                writeResponse(exchange, "Не найден эпик с идентификатором " + id, 404);
                            }
                        } else {
                            if (!jsonObject.has("title")) {
                                writeResponse(exchange, "Из тела запроса не удалось извлечь обязательный параметр title", 404);
                                break;
                            }
                            if (!jsonObject.has("description")) {
                                writeResponse(exchange, "Из тела запроса не удалось извлечь обязательный параметр description", 404);
                                break;
                            }
                            inputTitle = jsonObject.get("title").getAsString();
                            inputDescription = jsonObject.get("description").getAsString();
                            epic = new Epic(inputTitle, inputDescription);
                            taskManager.addEpic(epic);
                            writeResponse(exchange, epic.toString(), 200);
                        }
                    } else {
                        writeResponse(exchange, "Не удалось извлечь тело запроса", 400);
                    }
                } else {
                    writeResponse(exchange, "Сервер не обслуживает эндпоинт " + method + path, 501);
                }
                break;
            case "DELETE":
                if (pathParts.length == 3) {
                    Optional<Integer> optionVal = getPathParam(exchange);
                    if(optionVal.isEmpty()) {
                        writeResponse(exchange, "Некорректный идентификатор эпика", 400);
                    } else {
                        int id = optionVal.get();
                        Epic epic = taskManager.getEpicByID(id);
                        if (epic != null) {
                            taskManager.removeEpicByID(id);
                            writeResponse(exchange, null, 201);
                        } else {
                            writeResponse(exchange, "Не найден эпик с идентификатором " + id, 404);
                        }
                    }
                } else {
                    writeResponse(exchange, "Сервер не обслуживает эндпоинт " + method + path, 501);
                }
                break;
            default:
                writeResponse(exchange, "Сервер не обслуживает метод " + method, 501);
        }
    }
}

class HistoryHandler extends BaseHttpHandler {

    public HistoryHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        System.out.println("OK-HistoryHandler");
        writeResponse(exchange, "OK-HistoryHandler", 200);
    }
}

class PrioritizeHandler extends BaseHttpHandler {

    public PrioritizeHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        System.out.println("OK-PrioritizeHandler");
        writeResponse(exchange, "OK-PrioritizeHandler", 200);
    }
}
