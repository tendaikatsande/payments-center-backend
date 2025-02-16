package zw.co.link.FinPay.services.impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import zw.co.link.FinPay.domain.User;
import zw.co.link.FinPay.domain.dtos.EmailDetails;
import zw.co.link.FinPay.services.EmailService;

import java.io.File;
import java.util.Collections;
import java.util.Objects;
import java.util.UUID;

@Service
@Slf4j
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String sender;

    @Value("${domain.url}")
    private String domain_url;

    private static final String SUCCESS_MESSAGE = "Mail Sent Successfully...";
    private static final String ERROR_MESSAGE = "Error while Sending Mail";
    private static final String ATTACHMENT_ERROR_MESSAGE = "Error while sending mail with attachment!!!";
    private static final String RESET_PASSWORD_SUBJECT = "Password Reset Link";
    private static final String EMAIL_LINK_LOGIN = "Email Link Login";


    public EmailServiceImpl(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }


    @Override
    public String sendSimpleMail(EmailDetails details) {
        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setFrom(sender);
            mailMessage.setTo(details.getRecipients().toArray(new String[0])); // Set multiple recipients
            mailMessage.setText(details.getMsgBody());
            mailMessage.setSubject(details.getSubject());

            javaMailSender.send(mailMessage);
            return SUCCESS_MESSAGE;
        } catch (MailException e) {
            log.error("Error sending simple mail: {}", e.getMessage());
            return ERROR_MESSAGE;
        }
    }

    @Override
    public String sendMailWithAttachment(EmailDetails details) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
            mimeMessageHelper.setFrom(sender);
            mimeMessageHelper.setTo(details.getRecipients().toArray(new String[0])); // Set multiple recipients
            mimeMessageHelper.setText(details.getMsgBody());
            mimeMessageHelper.setSubject(details.getSubject());

            if (details.getAttachment() != null) {
                FileSystemResource file = new FileSystemResource(new File(details.getAttachment()));
                mimeMessageHelper.addAttachment(Objects.requireNonNull(file.getFilename()), file);
            }

            javaMailSender.send(mimeMessage);
            log.debug(SUCCESS_MESSAGE);
            return SUCCESS_MESSAGE;
        } catch (MessagingException | MailException e) {
            log.error("Error sending mail with attachment: {}", e.getMessage());
            return ATTACHMENT_ERROR_MESSAGE;
        }
    }


    @Override
    public void sendPasswordResetEmail(User user) {
        EmailDetails details = EmailDetails.builder()
                .recipients(Collections.singletonList(user.getEmail()))
                .subject(RESET_PASSWORD_SUBJECT)
                .msgBody(buildResetPasswordEmailBody(user))
                .build();
        sendSimpleMail(details);
    }

    private String buildResetPasswordEmailBody(User user) {
        String resetLink = String.format("%s/reset-password/%s", domain_url, user.getResetToken());
        return String.format("Click the following link to reset your password: %s", resetLink);
    }

    private String buildEmailLoginBody(UUID token) {
        String resetLink = String.format("%s/login-email-link/%s", domain_url, token);
        return String.format("Click the following link to login: %s", resetLink);
    }

    @Override
    public String loginWithEmail(String email, UUID token) {
        EmailDetails emailDetails = new EmailDetails();
        emailDetails.setRecipients(Collections.singletonList(email));
        emailDetails.setSubject(EMAIL_LINK_LOGIN);
        emailDetails.setMsgBody(buildEmailLoginBody(token));
        sendSimpleMail(emailDetails);
        return SUCCESS_MESSAGE;

    }


}

