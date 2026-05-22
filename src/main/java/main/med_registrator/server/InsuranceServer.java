package main.med_registrator.server;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

public class InsuranceServer {

    private static final int PORT = 8181;

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/check", InsuranceServer::handleCheck);
        server.start();
        System.out.println("[InsuranceServer] Запущен на порту " + PORT);
        System.out.println("[InsuranceServer] Пример: http://localhost:8181/check?policy=2222222222222222");
    }

    private static void handleCheck(HttpExchange exchange) throws IOException {
        // Разрешаем только GET
        if (!exchange.getRequestMethod().equalsIgnoreCase("GET")) {
            sendResponse(exchange, 405, "{\"error\":\"Method not allowed\"}");
            return;
        }

        // Достаём параметр policy из query string
        String query = exchange.getRequestURI().getQuery(); // "policy=1234..."
        String policy = null;
        if (query != null) {
            for (String param : query.split("&")) {
                if (param.startsWith("policy=")) {
                    policy = param.substring("policy=".length());
                }
            }
        }

        if (policy == null || policy.isBlank()) {
            sendResponse(exchange, 400, "{\"error\":\"policy parameter required\"}");
            return;
        }

        boolean valid = checkPolicy(policy);
        String json = String.format(
                "{\"policy\":\"%s\",\"valid\":%b,\"message\":\"%s\"}",
                policy, valid,
                valid ? "Полис ОМС подтверждён" : "Полис ОМС не найден"
        );

        sendResponse(exchange, 200, json);
        System.out.println("[InsuranceServer] Запрос: " + policy + " → " + valid);
    }

    /**
     * Логическое правило вместо БД:
     * Полис валиден, если состоит ровно из 16 цифр И все цифры чётные (0,2,4,6,8)
     */
    private static boolean checkPolicy(String policy) {
        if (!policy.matches("\\d{16}")) return false;
        for (char c : policy.toCharArray()) {
            int digit = c - '0';
            if (digit % 2 != 0) return false;
        }
        return true;
    }

    private static void sendResponse(HttpExchange exchange, int code, String body) throws IOException {
        byte[] bytes = body.getBytes();
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(code, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }
}