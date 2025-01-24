package zw.co.link.PaymentsCenter.services;

import org.springframework.stereotype.Service;
import zw.co.link.PaymentsCenter.domain.dtos.USSDSession;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// USSD Session Management
@Service
public class USSDSessionManager {
    private final Map<String, USSDSession> activeSessions = new ConcurrentHashMap<>();

    public USSDSession getOrCreateSession(String phoneNumber) {
        return activeSessions.computeIfAbsent(
                phoneNumber,
                k -> new USSDSession(phoneNumber)
        );
    }

    public void removeSession(String phoneNumber) {
        activeSessions.remove(phoneNumber);
    }


}
