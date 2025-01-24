package zw.co.link.PaymentsCenter.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import zw.co.link.PaymentsCenter.domain.dtos.USSDRequest;
import zw.co.link.PaymentsCenter.domain.dtos.USSDSession;
import zw.co.link.PaymentsCenter.domain.dtos.USSDSimulationRequest;
import zw.co.link.PaymentsCenter.domain.dtos.USSDSimulationStep;
import zw.co.link.PaymentsCenter.services.USSDService;

import java.util.ArrayList;
import java.util.List;

// USSD Simulation Controller (for testing)
@RestController
@RequestMapping("/ussd/simulation")
public class USSDSimulationController {
    private final USSDService ussdService;

    public USSDSimulationController(USSDService ussdService) {
        this.ussdService = ussdService;
    }

    @PostMapping("/simulate")
    public ResponseEntity<List<USSDSimulationStep>> simulateUSSDFlow(
            @RequestBody USSDSimulationRequest simulationRequest
    ) {
        List<USSDSimulationStep> simulationSteps = new ArrayList<>();
       USSDSession mockSession =
                new USSDSession(simulationRequest.getPhoneNumber());

        for (String userInput : simulationRequest.getUserInputs()) {
            USSDRequest request = new USSDRequest();
            request.setPhoneNumber(simulationRequest.getPhoneNumber());
            request.setUserInput(userInput);

            String response = ussdService.processUSSDRequest(request);

            simulationSteps.add(new USSDSimulationStep(userInput, response));
        }

        return ResponseEntity.ok(simulationSteps);
    }
}




