package handlers;

import com.google.gson.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import enums.Endpoint;
import managers.TaskManager;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class BaseHttpHandler implements HttpHandler {

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
            exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
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

    protected Gson getGson() {
        return new GsonBuilder()
                .registerTypeAdapter(Duration.class, new TypeAdapterDuration())
                .registerTypeAdapter(LocalDateTime.class, new TypeAdapterLocalDateTime())
                .create();
    }

    protected Endpoint getEndpoint(String requestPath, String requestMethod) {
        String[] pathParts = requestPath.split("/");
        if (requestMethod.equals("GET")) {
            if (pathParts[1].equals("tasks")) {
                if (pathParts.length == 2) {
                    return Endpoint.GET_TASKS;
                } else if (pathParts.length == 3) {
                    return Endpoint.GET_TASK;
                }
            }
            if (pathParts[1].equals("subtasks")) {
                if (pathParts.length == 2) {
                    return Endpoint.GET_SUBTASKS;
                } else if (pathParts.length == 3) {
                    return Endpoint.GET_SUBTASK;
                }
            }
            if (pathParts[1].equals("epics")) {
                if (pathParts.length == 2) {
                    return Endpoint.GET_EPICS;
                } else if (pathParts.length == 3) {
                    return Endpoint.GET_EPIC;
                } else if (pathParts.length == 4) {
                    return Endpoint.GET_EPIC_SUBTASKS;
                }
            }
            if (pathParts[1].equals("history")) {
                if (pathParts.length == 2) {
                    return Endpoint.GET_HISTORY;
                }
            }
            if (pathParts[1].equals("prioritized")) {
                if (pathParts.length == 2) {
                    return Endpoint.GET_PRIORITIZED;
                }
            }
        }
        if (requestMethod.equals("DELETE")) {
            if (pathParts[1].equals("tasks")) {
                if (pathParts.length == 3) {
                    return Endpoint.DELETE_TASK;
                }
            }
            if (pathParts[1].equals("subtasks")) {
                if (pathParts.length == 3) {
                    return Endpoint.DELETE_SUBTASK;
                }
            }
            if (pathParts[1].equals("epics")) {
                if (pathParts.length == 3) {
                    return Endpoint.DELETE_EPIC;
                }
            }
        }
        if (requestMethod.equals("POST")) {
            if (pathParts[1].equals("tasks")) {
                if (pathParts.length == 2) {
                    return Endpoint.POST_TASKS;
                }
            }
            if (pathParts[1].equals("subtasks")) {
                if (pathParts.length == 2) {
                    return Endpoint.POST_SUBTASKS;
                }
            }
            if (pathParts[1].equals("epics")) {
                if (pathParts.length == 2) {
                    return Endpoint.POST_EPICS;
                }
            }
        }
        return Endpoint.UNKNOWN;
    }


}