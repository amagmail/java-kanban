import com.google.gson.*;
import com.sun.net.httpserver.HttpExchange;
import enums.Endpoint;
import tasks.Task;

import java.io.*;
import java.util.*;

public class HistoryHandler extends BaseHttpHandler {

    public HistoryHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        Endpoint endpoint = getEndpoint(path, method);
        Gson gson = getGson();
        if (endpoint == Endpoint.GET_HISTORY) {
            List<Task> history = taskManager.getHistory();
            writeResponse(exchange, gson.toJson(history), 200);
        } else {
            writeResponse(exchange, "Сервер не обслуживает эндпоинт " + method + path, 501);
        }
    }

}
