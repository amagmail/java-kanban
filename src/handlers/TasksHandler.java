package handlers;

import com.google.gson.*;
import com.sun.net.httpserver.HttpExchange;
import enums.Endpoint;
import managers.Managers;
import managers.TaskManager;
import tasks.Task;

import java.io.*;
import java.util.*;

public class TasksHandler extends BaseHttpHandler {

    public TasksHandler(TaskManager taskManager) {
        super(taskManager);
    }

    private Task updateTaskPost(JsonObject jsonObject) {
        int id = jsonObject.get("id").getAsInt();
        Task task = taskManager.getTaskByID(id);
        if (jsonObject.has("title")) {
            task.setTitle(jsonObject.get("title").getAsString());
        }
        if (jsonObject.has("description")) {
            task.setDescription(jsonObject.get("description").getAsString());
        }
        taskManager.updateTask(task);
        return task;
    }

    private Task createTaskPost(JsonObject jsonObject) {
        String inputTitle = jsonObject.get("title").getAsString();
        String inputDescription = jsonObject.get("description").getAsString();
        int inputMinutes = jsonObject.get("minutes").getAsInt();
        String inputDate = jsonObject.get("date").getAsString();
        Task task = new Task(inputTitle, inputDescription, inputMinutes, Managers.stringToDate(inputDate));
        if (taskManager.isValid(task)) {
            taskManager.addTask(task);
        } else {
            task = null;
        }
        return task;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        int id;
        Task task;
        List<Task> tasks;
        Optional<Integer> optionVal;
        try {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();
            Endpoint endpoint = getEndpoint(path, method);
            Gson gson = getGson();
            switch (endpoint) {
                case GET_TASKS:
                    tasks = taskManager.getTasks();
                    writeResponse(exchange, gson.toJson(tasks), 200);
                    break;
                case GET_TASK:
                    optionVal = getPathParam(exchange);
                    if (optionVal.isEmpty()) {
                        writeResponse(exchange, "Некорректный идентификатор задачи", 400);
                        break;
                    }
                    id = optionVal.get();
                    task = taskManager.getTaskByID(id);
                    writeResponse(exchange, gson.toJson(task), 200);
                    break;
                case POST_TASKS:
                    JsonElement jsonElement = getRequestBody(exchange);
                    if (jsonElement == null || !jsonElement.isJsonObject()) {
                        writeResponse(exchange, "Не удалось извлечь тело запроса", 400);
                        break;
                    }
                    JsonObject jsonObject = jsonElement.getAsJsonObject();
                    if (jsonObject.has("id")) {
                        task = updateTaskPost(jsonObject);
                        writeResponse(exchange, gson.toJson(task), 200);
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
                        task = createTaskPost(jsonObject);
                        if (task != null) {
                            writeResponse(exchange, gson.toJson(task), 200);
                        } else {
                            writeResponse(exchange, "Добавляемая задача пересекается с существующей", 406);
                        }
                    }
                    break;
                case DELETE_TASK:
                    optionVal = getPathParam(exchange);
                    if (optionVal.isEmpty()) {
                        writeResponse(exchange, "Некорректный идентификатор задачи", 400);
                        break;
                    }
                    id = optionVal.get();
                    taskManager.getTaskByID(id);
                    taskManager.removeTaskByID(id);
                    writeResponse(exchange, null, 201);
                    break;
                default:
                    writeResponse(exchange, "Сервер не обслуживает эндпоинт " + method + path, 501);
            }
        } catch (NoSuchElementException e) {
            writeResponse(exchange, "Не найден элемент по указанному идентификатору", 404);
        }
    }

}
