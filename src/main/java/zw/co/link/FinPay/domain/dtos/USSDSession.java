package zw.co.link.FinPay.domain.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Data
@AllArgsConstructor
@NoArgsConstructor
public  class USSDSession {
    private String phoneNumber;
    private USSDMenuState currentState;
    private Map<String, Object> sessionData;

    public USSDSession(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        this.currentState = USSDMenuState.INITIAL;
        this.sessionData = new ConcurrentHashMap<>();
    }

}
