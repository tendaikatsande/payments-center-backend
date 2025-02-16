package zw.co.link.FinPay.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import zw.co.link.FinPay.domain.dtos.WhatsAppSendMessageRequest;

// WhatsApp Message Sender
@Service
public class WhatsAppMessageSender {
    private final RestTemplate restTemplate;

    @Value("${whatsapp.business.phone.number.id}")
    private String phoneNumberId;

    @Value("${whatsapp.access.token}")
    private String accessToken;

    public WhatsAppMessageSender(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void sendMessage(String phoneNumber, String message) {
        WhatsAppSendMessageRequest request = new WhatsAppSendMessageRequest(
                phoneNumberId,
                phoneNumber,
                message
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);

        HttpEntity<WhatsAppSendMessageRequest> entity =
                new HttpEntity<>(request, headers);

        try {
            restTemplate.postForEntity(
                    "https://graph.facebook.com/v17.0/{phone_number_id}/messages",
                    entity,
                    Void.class,
                    phoneNumberId
            );
        } catch (Exception e) {
            // Log error, potentially retry or handle failure
        }
    }
}
