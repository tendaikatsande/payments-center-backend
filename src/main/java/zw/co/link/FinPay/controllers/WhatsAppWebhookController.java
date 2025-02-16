package zw.co.link.FinPay.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import zw.co.link.FinPay.domain.dtos.*;
import zw.co.link.FinPay.services.WhatsAppService;

// WhatsApp Webhook Controller
@RestController
@RequestMapping("/whatsapp/webhook")
public class WhatsAppWebhookController {
    private final WhatsAppService whatsAppService;

    @Value("${whatsapp.verify.token}")
    private String verifyToken;

    public WhatsAppWebhookController(
            WhatsAppService whatsAppService
    ) {
        this.whatsAppService = whatsAppService;
    }

    // Webhook verification endpoint (required by WhatsApp)
    @GetMapping
    public ResponseEntity<String> verifyWebhook(
            @RequestParam("hub.mode") String mode,
            @RequestParam("hub.challenge") String challenge,
            @RequestParam("hub.verify_token") String token
    ) {
        if ("subscribe".equals(mode) && verifyToken.equals(token)) {
            return ResponseEntity.ok(challenge);
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    // Webhook receiver for incoming messages
    @PostMapping
    public ResponseEntity<Void> receiveMessage(@RequestBody WhatsAppWebhookPayload payload) {
        try {
            whatsAppService.processIncomingMessage(payload);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}



