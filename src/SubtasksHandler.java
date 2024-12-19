import com.google.gson.*;
import com.sun.net.httpserver.HttpExchange;
import java.io.*;
import java.util.*;

public class SubtasksHandler extends BaseHttpHandler {

    public SubtasksHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();
            String[] pathParts = path.split("/");
            Gson gson = getGson();
            switch (method) {
                case "GET":
                    if (pathParts.length == 2) {
                        List<Subtask> subtasks = taskManager.getSubtasks();
                        writeResponse(exchange, gson.toJson(subtasks), 200);
                    } else if (pathParts.length == 3) {
                        Optional<Integer> optionVal = getPathParam(exchange);
                        if (optionVal.isEmpty()) {
                            writeResponse(exchange, "Некорректный идентификатор подзадачи", 400);
                            return;
                        }
                        int id = optionVal.get();
                        Subtask subtask = taskManager.getSubtaskByID(id);
                        writeResponse(exchange, gson.toJson(subtask), 200);
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
                                if (jsonObject.has("title")) {
                                    subtask.title = jsonObject.get("title").getAsString();
                                }
                                if (jsonObject.has("description")) {
                                    subtask.description = jsonObject.get("description").getAsString();
                                }
                                taskManager.updateSubtask(subtask);
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
                                inputTitle = jsonObject.get("title").getAsString();
                                inputDescription = jsonObject.get("description").getAsString();
                                inputEpicID = jsonObject.get("epicId").getAsInt();
                                inputMinutes = jsonObject.get("minutes").getAsInt();
                                inputDate = jsonObject.get("date").getAsString();
                                subtask = new Subtask(inputTitle, inputDescription, inputEpicID, inputMinutes, Managers.stringToDate(inputDate));
                                if (taskManager.isValid(subtask)) {
                                    taskManager.addSubtask(subtask);
                                    writeResponse(exchange, gson.toJson(subtask), 200);
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
                        if (optionVal.isEmpty()) {
                            writeResponse(exchange, "Некорректный идентификатор задачи", 400);
                        } else {
                            int id = optionVal.get();
                            Subtask subtask = taskManager.getSubtaskByID(id);
                            taskManager.removeSubtaskByID(id);
                            writeResponse(exchange, null, 201);
                        }
                    } else {
                        writeResponse(exchange, "Сервер не обслуживает эндпоинт " + method + path, 501);
                    }
                    break;
                default:
                    writeResponse(exchange, "Сервер не обслуживает метод " + method, 501);
            }
        } catch (NoSuchElementException e) {
            writeResponse(exchange, "Не найден элемент по указанному идентификатору", 404);
        }
    }

}
