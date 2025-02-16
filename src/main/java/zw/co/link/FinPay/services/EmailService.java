package zw.co.link.FinPay.services;



import zw.co.link.FinPay.domain.User;
import zw.co.link.FinPay.domain.dtos.EmailDetails;

import java.util.UUID;

public interface EmailService {
    String sendSimpleMail(EmailDetails details);
    String sendMailWithAttachment(EmailDetails details);
    void sendPasswordResetEmail(User user);
    String loginWithEmail(String email, UUID token);
}
