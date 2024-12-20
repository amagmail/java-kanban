package handlers;

import com.google.gson.*;
import com.sun.net.httpserver.HttpExchange;
import enums.Endpoint;
import managers.TaskManager;
import tasks.Epic;
import tasks.Subtask;

import java.io.*;
import java.util.*;

public class EpicsHandler extends BaseHttpHandler {

    public EpicsHandler(TaskManager taskManager) {
        super(taskManager);
    }

    private Epic updateEpicPost(JsonObject jsonObject) {
        int id = jsonObject.get("id").getAsInt();
        Epic epic = taskManager.getEpicByID(id);
        if (jsonObject.has("title")) {
            epic.setTitle(jsonObject.get("title").getAsString());
        }
        if (jsonObject.has("description")) {
            epic.setDescription(jsonObject.get("description").getAsString());
        }
        taskManager.updateEpic(epic);
        return epic;
    }

    private Epic createEpicPost(JsonObject jsonObject) {
        String inputTitle = jsonObject.get("title").getAsString();
        String inputDescription = jsonObject.get("description").getAsString();
        Epic epic = new Epic(inputTitle, inputDescription);
        taskManager.addEpic(epic);
        return epic;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        int id;
        Epic epic;
        List<Epic> epics;
        Optional<Integer> optionVal;
        try {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();
            Endpoint endpoint = getEndpoint(path, method);
            Gson gson = getGson();
            switch (endpoint) {
                case GET_EPICS:
                    epics = taskManager.getEpics();
                    writeResponse(exchange, gson.toJson(epics), 200);
                    break;
                case GET_EPIC:
                    optionVal = getPathParam(exchange);
                    if (optionVal.isEmpty()) {
                        writeResponse(exchange, "Некорректный идентификатор эпика'", 400);
                        break;
                    }
                    id = optionVal.get();
                    epic = taskManager.getEpicByID(id);
                    writeResponse(exchange, gson.toJson(epic), 200);
                    break;
                case GET_EPIC_SUBTASKS:
                    optionVal = getPathParam(exchange);
                    if (optionVal.isEmpty()) {
                        writeResponse(exchange, "Некорректный идентификатор эпика'", 400);
                        break;
                    }
                    id = optionVal.get();
                    taskManager.getEpicByID(id);
                    List<Subtask> subtasks = taskManager.getEpicSubtasksByID(id);
                    writeResponse(exchange, gson.toJson(subtasks), 200);
                    break;
                case POST_EPICS:
                    JsonElement jsonElement = getRequestBody(exchange);
                    if (jsonElement == null || !jsonElement.isJsonObject()) {
                        writeResponse(exchange, "Не удалось извлечь тело запроса", 400);
                        break;
                    }
                    JsonObject jsonObject = jsonElement.getAsJsonObject();
                    if (jsonObject.has("id")) {
                        epic = updateEpicPost(jsonObject);
                        writeResponse(exchange, gson.toJson(epic), 200);
                    } else {
                        if (!jsonObject.has("title")) {
                            writeResponse(exchange, "Из тела запроса не удалось извлечь обязательный параметр title", 404);
                            break;
                        }
                        if (!jsonObject.has("description")) {
                            writeResponse(exchange, "Из тела запроса не удалось извлечь обязательный параметр description", 404);
                            break;
                        }
                        epic = createEpicPost(jsonObject);
                        writeResponse(exchange, gson.toJson(epic), 200);
                    }
                    break;
                case DELETE_EPIC:
                    optionVal = getPathParam(exchange);
                    if (optionVal.isEmpty()) {
                        writeResponse(exchange, "Некорректный идентификатор эпика", 400);
                    } else {
                        id = optionVal.get();
                        taskManager.getEpicByID(id);
                        taskManager.removeEpicByID(id);
                        writeResponse(exchange, null, 201);
                    }
                    break;
                default:
                    writeResponse(exchange, "Сервер не обслуживает эндпоинт " + method + path, 501);
            }
        } catch (NoSuchElementException e) {
            writeResponse(exchange, "Не найден элемент по указанному идентификатору", 404);
        }
    }

}
