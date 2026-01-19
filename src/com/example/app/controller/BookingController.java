package controller;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;
import java.util.List;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Map;

import dto.BookingResponse;
import dto.CreateBookingRequest;

import service.BookingService;

import util.BookingValidator;
import util.JsonUtil;
import util.ValidationUtil;

import static util.HttpUtils.sendJsonResponse;
import static util.HttpUtils.sendError;

public class BookingController implements HttpHandler {
    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        switch (path) {
            case "/api/v0/pool/timetable/all":
                handleBusy(exchange);
                break;
            case "/api/v0/pool/timetable/reserve":
                handleCreateBooking(exchange);
                break;
            case "/api/v0/pool/timetable/available":
                handleGetAvailable(exchange);
                break;
            case "/api/v0/pool/timetable/cancel":
                handleCancel(exchange);
                break;
            default:
                sendError(exchange, 404, "Not Found");
        }
    }

    public void handleBusy(HttpExchange exchange) throws IOException {
        if (!"GET".equals(exchange.getRequestMethod())) {
            sendError(exchange, 405, "Method Not Allowed");
            return;
        }

        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        LocalDate date = parseDate(body);

        if (date == null) {
            sendError(exchange, 400, "Invalid date");
            return;
        }
        
        // Validate the date
        try {
            BookingValidator.validateDate(date);
        } catch (IllegalArgumentException e) {
            sendError(exchange, 400, e.getMessage());
            return;
        }

        Map<LocalTime, Integer> stats = bookingService.getBusyStats(date);
        sendJsonResponse(exchange, 200, toJson(stats));
    }

    private void handleCreateBooking(HttpExchange exchange) throws IOException {
        String body = new String(
                exchange.getRequestBody().readAllBytes(),
                StandardCharsets.UTF_8
        );

        CreateBookingRequest req = parseBookingRequest(body);

        if (req == null) {
            sendError(exchange, 400, "Invalid request body");
            return;
        }

        try {
            // Validate the request
            BookingValidator.validateCreateBookingRequest(req);

            int bookingId = bookingService.createBooking(
                    req.getClientId(),
                    req.getVisitDate(),
                    req.getVisitTime()
            );

            // 4. Формируем ответ
            BookingResponse response = BookingResponse.success(bookingId);

            sendJsonResponse(exchange, 201, toJson(response));

        } catch (IllegalArgumentException | IllegalStateException e) {
            sendError(exchange, 400, e.getMessage());
        }
    }
    
    public void handleCancel(HttpExchange exchange) throws IOException {
        if (!"GET".equals(exchange.getRequestMethod())) {
            sendError(exchange, 405, "Method Not Allowed");
            return;
        }

        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);

        try {
            int clientId = Integer.parseInt(extractJsonValue(body, "clientId"));
            String orderIdStr = extractJsonValue(body, "orderId");
            
            // Validate client ID, but we can't validate order ID format as easily with integers
            if (!ValidationUtil.isValidClientId(clientId)) {
                throw new IllegalArgumentException("Invalid client ID: must be a positive integer");
            }
            
            int orderId = Integer.parseInt(orderIdStr);

            bookingService.cancelBooking(clientId, orderId);

            // Успешный ответ
            sendJsonResponse(exchange, 200, "{\"status\":\"success\"}");
        } catch (IllegalArgumentException e) {
            sendError(exchange, 400, e.getMessage());
        } catch (SQLException e) {
            e.printStackTrace();
            sendError(exchange, 500, "Internal server error");
        } catch (Exception e) {
            e.printStackTrace();
            sendError(exchange, 400, "Invalid request");
        }
    }

    // Простейший парсер JSON (using JsonUtil)
    private String extractJsonValue(String body, String key) {
        return JsonUtil.extractValue(body, key);
    }

    private CreateBookingRequest parseBookingRequest(String jsonBody) {
        try {
            String clientIdStr = JsonUtil.extractValue(jsonBody, "clientId");
            String dateTimeStr = JsonUtil.extractValue(jsonBody, "dateTime");

            if (clientIdStr != null && dateTimeStr != null) {
                int clientId = Integer.parseInt(clientIdStr);
                LocalDateTime dateTime = LocalDateTime.parse(dateTimeStr);
                return new CreateBookingRequest(clientId, dateTime);
            }

        } catch (Exception e) {
            System.err.println("Parse error: " + e.getMessage());
        }
        return null;
    }

    private String toJson(BookingResponse r) {
        return String.format(
                "{\"status\":\"%s\",\"bookingId\":%s,\"message\":\"%s\"}",
                r.getStatus(),
                r.getBookingId(),
                escapeJson(r.getMessage())
        );
    }

    private LocalDate parseDate(String body) {
        String dateStr = JsonUtil.extractValue(body, "date");
        if (dateStr == null) {
            throw new IllegalArgumentException("date not found in body");
        }
        return LocalDate.parse(dateStr);
    }

    private String toJson(Map<LocalTime, Integer> stats) {
        StringBuilder sb = new StringBuilder("[");
        int i = 0;
        for (Map.Entry<LocalTime, Integer> entry : stats.entrySet()) {
            sb.append("{\"time\":\"").append(entry.getKey())
                    .append("\",\"count\":").append(entry.getValue()).append("}");
            if (i < stats.size() - 1) sb.append(",");
            i++;
        }
        sb.append("]");
        return sb.toString();
    }

    private String escapeJson(String input) {
        if (input == null) return "";
        return input.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    public void handleGetAvailable(HttpExchange exchange) throws IOException {
        if (!"GET".equals(exchange.getRequestMethod())) {
            sendError(exchange, 405, "Method Not Allowed");
            return;
        }

        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        LocalDate date = parseDate(body);

        if (date == null) {
            sendError(exchange, 400, "Invalid date");
            return;
        }
        
        // Validate the date
        try {
            BookingValidator.validateDate(date);
        } catch (IllegalArgumentException e) {
            sendError(exchange, 400, e.getMessage());
            return;
        }

        Map<LocalTime, Integer> available = bookingService.getAvailableStats(date);

        sendJsonResponse(exchange, 200, toJson(available));
    }


}