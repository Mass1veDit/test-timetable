package controller;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import util.ClientValidator;
import util.JsonUtil;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.List;

import dto.ClientResponse;
import dto.CreateClientRequest;
import dto.UpdateClientRequest;

import model.Client;
import service.ClientService;

import static util.HttpUtils.sendJsonResponse;
import static util.HttpUtils.sendError;

public class ClientController implements HttpHandler {

    private final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        switch (path) {
            case "/api/v0/pool/client/add":
                handleAddClient(exchange);
                break;
            case "/api/v0/pool/client/update":
                handleUpdateClient(exchange);
                break;
            case "/api/v0/pool/client/all":
                handleGetAll(exchange);
                break;
            case "/api/v0/pool/client/get":
                handleGetById(exchange);
                break;
            default:
                sendError(exchange, 404, "Not Found");
        }
    }

    private void handleGetById(HttpExchange exchange) throws IOException {
        if (!"GET".equals(exchange.getRequestMethod())) {
            sendError(exchange, 405, "Method Not Allowed");
            return;
        }

        String body = new String(
                exchange.getRequestBody().readAllBytes(),
                StandardCharsets.UTF_8
        );

        Integer id = parseIdFromBody(body);
        if (id == null) {
            sendError(exchange, 400, "Invalid request body");
            return;
        }

        try {
            ClientValidator.validateClientId(id);
            
            Client client = clientService.getClientById(id);
            String response = client != null ? toJson(client) : "{}";
            sendJsonResponse(exchange, 200, response);
        } catch (IllegalArgumentException e) {
            sendError(exchange, 400, e.getMessage());
        } catch (SQLException e) {
            sendError(exchange, 500, "Internal server error");
        }
    }


    public void handleAddClient(HttpExchange exchange) throws IOException {
        if (!"POST".equals(exchange.getRequestMethod())) {
            sendError(exchange, 405, "Method Not Allowed");
            return;
        }

        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        CreateClientRequest req = parseCreateClientRequest(body);

        if (req == null) {
            sendError(exchange, 400, "Invalid request body");
            return;
        }

        try {
            ClientValidator.validateClientFields(req.getName(), req.getPhone(), req.getEmail());
            
            int clientId = clientService.createClient(req.getName(), req.getPhone(), req.getEmail());
            ClientResponse response = ClientResponse.success(clientId);
            sendJsonResponse(exchange, 201, toJson(response));
        } catch (IllegalArgumentException e) {
            sendError(exchange, 400, e.getMessage());
        } catch (SQLException e) {
            sendError(exchange, 500, "Internal server error");
        }
    }

    private CreateClientRequest parseCreateClientRequest(String jsonBody) {
        try {
            String name = JsonUtil.extractValue(jsonBody, "name");
            String phone = JsonUtil.extractValue(jsonBody, "phone");
            String email = JsonUtil.extractValue(jsonBody, "email");

            if (name != null && phone != null && email != null) {
                return new CreateClientRequest(name, phone, email);
            }
        } catch (Exception e) {
            System.err.println("Parse error: " + e.getMessage());
        }
        return null;
    }


    public void handleUpdateClient(HttpExchange exchange) throws IOException {
        if (!"POST".equals(exchange.getRequestMethod())) {
            sendError(exchange, 405, "Method Not Allowed");
            return;
        }

        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        UpdateClientRequest req = parseUpdateClientRequest(body);

        if (req == null) {
            sendError(exchange, 400, "Invalid request body");
            return;
        }

        try {
            ClientValidator.validateClientId(req.getId());
            ClientValidator.validateClientFieldsForUpdate(req.getName(), req.getPhone(), req.getEmail());
            
            Client client = clientService.updateClient(req.getId(), req.getName(), req.getPhone(), req.getEmail());
            ClientResponse response = ClientResponse.success(client.getId());
            sendJsonResponse(exchange, 200, toJson(response));
        } catch (IllegalArgumentException e) {
            sendError(exchange, 400, e.getMessage());
        } catch (SQLException e) {
            sendError(exchange, 500, "Internal server error");
        }
    }

    private UpdateClientRequest parseUpdateClientRequest(String jsonBody) {
        try {
            String idStr = JsonUtil.extractValue(jsonBody, "id");
            String name = JsonUtil.extractValue(jsonBody, "name");
            String phone = JsonUtil.extractValue(jsonBody, "phone");
            String email = JsonUtil.extractValue(jsonBody, "email");

            if (idStr != null && name != null && phone != null && email != null) {
                int id = Integer.parseInt(idStr);
                if (id > 0) {
                    return new UpdateClientRequest(id, name, phone, email);
                }
            }
        } catch (Exception e) {
            System.err.println("Parse error: " + e.getMessage());
        }
        return null;
    }


    public void handleGetAll(HttpExchange exchange) throws IOException {
        if (!"GET".equals(exchange.getRequestMethod())) {
            sendError(exchange, 405, "Method Not Allowed");
            return;
        }

        try {
            List<Client> clients = clientService.getAllClients();
            String response = toJson(clients);
            sendJsonResponse(exchange, 200, response);
        } catch (IllegalArgumentException e) {
            sendError(exchange, 400, e.getMessage());
        } catch (SQLException e) {
            sendError(exchange, 500, "Internal server error");
        }
    }


    private String toJson(ClientResponse response) {
        return String.format("{\"id\":%d}", response.getId());
    }

    private String toJson(List<Client> clients) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < clients.size(); i++) {
            Client c = clients.get(i);
            sb.append("{\"id\":").append(c.getId())
                    .append(",\"name\":\"").append(c.getName()).append("\"}");
            if (i < clients.size() - 1) sb.append(",");
        }
        sb.append("]");
        return sb.toString();
    }

    private String toJson(Client client) {
        return String.format("{\"id\":%d,\"name\":\"%s\"}", client.getId(), client.getName());
    }

    private Integer parseIdFromBody(String jsonBody) {
        try {
            String idStr = JsonUtil.extractValue(jsonBody, "id");
            if (idStr != null) {
                return Integer.parseInt(idStr);
            }
        } catch (Exception e) {
            System.err.println("Parse error: " + e.getMessage());
        }
        return null;
    }
}
