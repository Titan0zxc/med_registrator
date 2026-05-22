package main.med_registrator.pipeline;

import main.med_registrator.model.Appeal;
import main.med_registrator.model.Questionnaire;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class InsuranceFilter implements Filter {

    private static final String SERVER_URL = "http://localhost:8181/check?policy=";
    private static final int TIMEOUT_SEC = 3;

    @Override
    public void process(Questionnaire questionnaire, Appeal appeal) {
        if (appeal.hasError()) return;

        String policy = questionnaire.getInsurancePolicy();

        // Полис обязателен
        if (policy == null || policy.isBlank()) {
            appeal.setErrorMessage("Введите номер полиса ОМС.");
            return;
        }

        try {
            HttpClient client = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(TIMEOUT_SEC))
                    .build();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(SERVER_URL + policy))
                    .timeout(Duration.ofSeconds(TIMEOUT_SEC))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request,
                    HttpResponse.BodyHandlers.ofString());

            String body = response.body();
            System.out.println("[Фильтр 2] Ответ сервера: " + body);

            if (body.contains("\"valid\":false")) {
                appeal.setErrorMessage(
                        "Полис ОМС не найден в системе.\n" +
                                "Номер: " + policy + "\n" +
                                "Обратитесь в регистратуру лично."
                );
            } else {
                System.out.println("[Фильтр 2] Полис подтверждён: " + policy);
            }

        } catch (Exception e) {
            // Сервер недоступен — блокируем
            System.out.println("[Фильтр 2] Сервер МИС недоступен: " + e.getMessage());
            appeal.setErrorMessage(
                    "Сервер проверки полисов недоступен.\n" +
                            "Запустите InsuranceServer и попробуйте снова."
            );
        }
    }
}