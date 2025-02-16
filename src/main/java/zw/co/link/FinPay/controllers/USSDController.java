package zw.co.link.FinPay.controllers;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import zw.co.link.FinPay.domain.dtos.USSDRequest;
import zw.co.link.FinPay.domain.dtos.USSDResponse;
import zw.co.link.FinPay.domain.dtos.USSDResponseType;
import zw.co.link.FinPay.services.USSDService;



// USSD REST Controller
@RestController
@RequestMapping("/ussd/v1")
public class USSDController {
    private final USSDService ussdService;

    public USSDController(USSDService ussdService) {
        this.ussdService = ussdService;
    }

    @PostMapping("/process")
    public ResponseEntity<USSDResponse> processUSSDRequest(
            @Valid @RequestBody USSDRequest request
    ) {
        String responseMessage = ussdService.processUSSDRequest(request);

        USSDResponse response = new USSDResponse(
                responseMessage,
                determineResponseType(responseMessage)
        );

        return ResponseEntity.ok(response);
    }

    private USSDResponseType determineResponseType(String responseMessage) {
        if (responseMessage.contains("Welcome") ||
                responseMessage.contains("Menu")) {
            return USSDResponseType.CONTINUE;
        }
        return USSDResponseType.TERMINATE;
    }
}





