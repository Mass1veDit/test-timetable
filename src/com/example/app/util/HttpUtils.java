package util;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class HttpUtils {

    public static void sendJsonResponse(HttpExchange exchange, int statusCode, String json) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(statusCode, bytes.length);

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    /**
     * Отправляет JSON с ошибкой
     */
    public static void sendError(HttpExchange exchange, int statusCode, String message) throws IOException {
        String json = String.format("{\"error\":\"%s\"}", escapeJson(message));
        sendJsonResponse(exchange, statusCode, json);
    }

    /**
     * Экранирование специальных символов в JSON
     *
     * @param input строка
     * @return строка с экранированием
     */
    public static String escapeJson(String input) {
        if (input == null) return "";
        return input.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
