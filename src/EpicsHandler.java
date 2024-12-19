import com.google.gson.*;
import com.sun.net.httpserver.HttpExchange;
import java.io.*;
import java.util.*;

public class EpicsHandler extends BaseHttpHandler {

    public EpicsHandler(TaskManager taskManager) {
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
                        List<Epic> epics = taskManager.getEpics();
                        writeResponse(exchange, gson.toJson(epics), 200);
                    } else if (pathParts.length == 3 || (pathParts.length == 4 && pathParts[3].equals("subtasks"))) {
                        Optional<Integer> optionVal = getPathParam(exchange);
                        if (optionVal.isEmpty()) {
                            writeResponse(exchange, "Некорректный идентификатор эпика'", 400);
                            return;
                        }
                        int id = optionVal.get();
                        Epic epic = taskManager.getEpicByID(id);
                        if (pathParts.length == 3) {
                            writeResponse(exchange, gson.toJson(epic), 200);
                        } else {
                            List<Subtask> subtasks = taskManager.getEpicSubtasksByID(id);
                            writeResponse(exchange, gson.toJson(subtasks), 200);
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
                                if (jsonObject.has("title")) {
                                    epic.title = jsonObject.get("title").getAsString();
                                }
                                if (jsonObject.has("description")) {
                                    epic.description = jsonObject.get("description").getAsString();
                                }
                                taskManager.updateEpic(epic);
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
                                inputTitle = jsonObject.get("title").getAsString();
                                inputDescription = jsonObject.get("description").getAsString();
                                epic = new Epic(inputTitle, inputDescription);
                                taskManager.addEpic(epic);
                                writeResponse(exchange, gson.toJson(epic), 200);
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
                            writeResponse(exchange, "Некорректный идентификатор эпика", 400);
                        } else {
                            int id = optionVal.get();
                            Epic epic = taskManager.getEpicByID(id);
                            taskManager.removeEpicByID(id);
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
