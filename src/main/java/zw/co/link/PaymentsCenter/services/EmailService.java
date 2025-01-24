package zw.co.link.PaymentsCenter.services;



import zw.co.link.PaymentsCenter.domain.User;
import zw.co.link.PaymentsCenter.domain.dtos.EmailDetails;

import java.util.UUID;

public interface EmailService {
    String sendSimpleMail(EmailDetails details);
    String sendMailWithAttachment(EmailDetails details);
    void sendPasswordResetEmail(User user);
    String loginWithEmail(String email, UUID token);
}
