package zw.co.link.FinPay.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import zw.co.link.FinPay.domain.dtos.*;
import zw.co.link.FinPay.services.WhatsAppService;

// WhatsApp Webhook Controller
@Slf4j
@RestController
@RequestMapping("api/wa")
@RequiredArgsConstructor
public class WhatsAppWebhookController {
    private static final String SUBSCRIBE = "subscribe";


    @Value("${wa.verify_token}")
    private String WA_VERIFY_TOKEN;
    private final WhatsAppService whatsAppService;

    @GetMapping
    ResponseEntity<?> verifyWaToken(@RequestParam("hub.mode") String mode,
                                    @RequestParam("hub.challenge") String challenge,
                                    @RequestParam("hub.verify_token") String verifyToken) {
        log.info("Incoming Verify Request : {},{},{}", challenge, mode, verifyToken);
        if (WA_VERIFY_TOKEN.equals(verifyToken) && mode.equals(SUBSCRIBE)) {
            return ResponseEntity.ok(challenge);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    // Webhook receiver for incoming messages
    @PostMapping
    public ResponseEntity<Void> receiveMessage(@RequestBody WhatsAppBusinessAccountEvent payload) {
        try {
            log.info("Incoming Wa Request : {},{}", payload.getFrom(), payload.getFirstMessageBody());
            whatsAppService.processIncomingMessage(payload);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}



