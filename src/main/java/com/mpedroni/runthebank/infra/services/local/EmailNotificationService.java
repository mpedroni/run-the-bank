package com.mpedroni.runthebank.infra.services.local;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mpedroni.runthebank.infra.services.NotificationService;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class EmailNotificationService implements NotificationService {
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void notify(UUID clientId, String message) {
        System.out.println("Sending email to " + clientId + " with message: " + message);
        sendEmail(clientId, message, 3);
    }

    private void sendEmail(UUID clientId, String message, int attempts) {
        var request = HttpRequest.newBuilder().GET().uri(URI.create("https://run.mocky.io/v3/9769bf3a-b0b6-477a-9ff5-91f63010c9d3")).build();
        try {
            var response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            var body = mapper.readValue(response.body(), ApiResponse.class);
            if (!body.messageSent()) {
                throw new IOException("Failed to send email");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (IOException e) {
            var remainingAttempts = attempts - 1;
            System.out.printf("Failed to notify %s. %d attempts remaining. Cause: %s%n", clientId, remainingAttempts, e.getMessage());

            if(remainingAttempts > 0)
                sendEmail(clientId, message, remainingAttempts);
        }
    }

    private static record ApiResponse(boolean messageSent) {}
}
