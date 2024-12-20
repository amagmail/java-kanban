import com.google.gson.*;
import com.sun.net.httpserver.HttpExchange;
import java.io.*;
import java.util.*;

public class SubtasksHandler extends BaseHttpHandler {

    public SubtasksHandler(TaskManager taskManager) {
        super(taskManager);
    }

    private Subtask updateSubtaskPost(JsonObject jsonObject) {
        int id = jsonObject.get("id").getAsInt();
        Subtask subtask = taskManager.getSubtaskByID(id);
        if (jsonObject.has("title")) {
            subtask.title = jsonObject.get("title").getAsString();
        }
        if (jsonObject.has("description")) {
            subtask.description = jsonObject.get("description").getAsString();
        }
        taskManager.updateSubtask(subtask);
        return subtask;
    }

    private Subtask createSubtaskPost(JsonObject jsonObject) {
        String inputTitle = jsonObject.get("title").getAsString();
        String inputDescription = jsonObject.get("description").getAsString();
        int inputEpicID = jsonObject.get("epicId").getAsInt();
        int inputMinutes = jsonObject.get("minutes").getAsInt();
        String inputDate = jsonObject.get("date").getAsString();
        Subtask subtask = new Subtask(inputTitle, inputDescription, inputEpicID, inputMinutes, Managers.stringToDate(inputDate));
        if (taskManager.isValid(subtask)) {
            taskManager.addSubtask(subtask);
        } else {
            subtask = null;
        }
        return subtask;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        int id;
        Subtask subtask;
        List<Subtask> subtasks;
        Optional<Integer> optionVal;
        try {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();
            Endpoint endpoint = getEndpoint(path, method);
            Gson gson = getGson();
            switch (endpoint) {
                case GET_SUBTASKS:
                    subtasks = taskManager.getSubtasks();
                    writeResponse(exchange, gson.toJson(subtasks), 200);
                    break;
                case GET_SUBTASK:
                    optionVal = getPathParam(exchange);
                    if (optionVal.isEmpty()) {
                        writeResponse(exchange, "Некорректный идентификатор подзадачи", 400);
                        break;
                    }
                    id = optionVal.get();
                    subtask = taskManager.getSubtaskByID(id);
                    writeResponse(exchange, gson.toJson(subtask), 200);
                    break;
                case POST_SUBTASKS:
                    JsonElement jsonElement = getRequestBody(exchange);
                    if (jsonElement == null || !jsonElement.isJsonObject()) {
                        writeResponse(exchange, "Не удалось извлечь тело запроса", 400);
                        break;
                    }
                    JsonObject jsonObject = jsonElement.getAsJsonObject();
                    if (jsonObject.has("id")) {
                        subtask = updateSubtaskPost(jsonObject);
                        writeResponse(exchange, gson.toJson(subtask), 200);
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
                        subtask = createSubtaskPost(jsonObject);
                        if (subtask != null) {
                            writeResponse(exchange, gson.toJson(subtask), 200);
                        } else {
                            writeResponse(exchange, "Добавляемая подзадача пересекается с существующей", 406);
                        }
                    }
                    break;
                case DELETE_SUBTASK:
                    optionVal = getPathParam(exchange);
                    if (optionVal.isEmpty()) {
                        writeResponse(exchange, "Некорректный идентификатор задачи", 400);
                    } else {
                        id = optionVal.get();
                        taskManager.getSubtaskByID(id);
                        taskManager.removeSubtaskByID(id);
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
