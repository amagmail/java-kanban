import com.google.gson.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

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

}