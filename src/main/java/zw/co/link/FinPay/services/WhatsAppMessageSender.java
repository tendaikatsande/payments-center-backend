package zw.co.link.FinPay.services;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import zw.co.link.FinPay.domain.dtos.WhatsAppBusinessAccountEvent;
import zw.co.link.FinPay.domain.dtos.WhatsAppMessage;

// WhatsApp Message Sender
@Service
@Slf4j
public class WhatsAppMessageSender {
    private final RestTemplate restTemplate;

    @Value("${wa.url}")
    private String WA_URL;


    @Value("${wa.bearer_token}")
    private String BEARER_TOKEN;

    public WhatsAppMessageSender(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void sendMessage(WhatsAppMessage message) {


        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(BEARER_TOKEN);

        try {

            var response = restTemplate.exchange(WA_URL, HttpMethod.POST,
                    new HttpEntity<>(message, headers), WhatsAppMessage.class
            );
            log.info("Response : {}", response.getBody());


        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
