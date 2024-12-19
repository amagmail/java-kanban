import com.google.gson.*;
import com.sun.net.httpserver.HttpExchange;
import java.io.*;
import java.util.*;

public class PrioritizedHandler extends BaseHttpHandler {

    public PrioritizedHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        String[] pathParts = path.split("/");
        Gson gson = getGson();
        if (method.equals("GET")) {
            if (pathParts.length == 2) {
                Set<Task> prioritizedTasks = taskManager.getPrioritizedTasks();
                writeResponse(exchange, gson.toJson(prioritizedTasks), 200);
            } else {
                writeResponse(exchange, "Сервер не обслуживает эндпоинт " + method + path, 501);
            }
        } else {
            writeResponse(exchange, "Сервер не обслуживает метод " + method, 501);
        }
    }

}
