package zw.co.link.FinPay.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import zw.co.link.FinPay.domain.dtos.*;

import java.util.List;

// WhatsApp Service to handle message processing
@Service
public class WhatsAppService {
    private static final Logger logger = LoggerFactory.getLogger(WhatsAppService.class);

    private final HotUserApiService hotUserApiService;
    private final WhatsAppMessageSender whatsAppMessageSender;


    public WhatsAppService(
            HotUserApiService hotUserApiService,
            WhatsAppMessageSender whatsAppMessageSender
    ) {
        this.hotUserApiService = hotUserApiService;
        this.whatsAppMessageSender = whatsAppMessageSender;
    }

    public void processIncomingMessage(WhatsAppBusinessAccountEvent payload) {

        try {
            switch (determineMessageIntent(payload.getFirstMessageBody())) {
                case LOGIN:
                    handleLogin(payload.getFrom(),payload.getFirstMessageBody(), payload.getFirstMessageBodyId());
                    break;
                case BALANCE:
                    handleBalanceInquiry(payload.getFrom(),payload.getFirstMessageBody(), payload.getFirstMessageBodyId());
                    break;
                case FUND_ACCOUNT:
                    handleAccountFunding(payload.getFrom(),payload.getFirstMessageBody(), payload.getFirstMessageBodyId());
                    break;
                case PRODUCT_RECHARGE:
                    handleProductRecharge(payload.getFrom(),payload.getFirstMessageBody(), payload.getFirstMessageBodyId());
                    break;
                case TRANSACTION_QUERY:
                    handleTransactionQuery(payload.getFrom(),payload.getFirstMessageBody(), payload.getFirstMessageBodyId());
                    break;
                default:
                    sendHelpMessage(payload.getFrom(), payload.getFirstMessageBodyId());
            }
        } catch (Exception e) {
            logger.error("Error processing message", e);
            sendWhatsappMessage(payload.getFrom(),"Error processing message", payload.getFirstMessageBodyId());
        }
    }

    private MessageIntent determineMessageIntent(String messageBody) {
        messageBody = messageBody.toLowerCase().trim();

        if (messageBody.startsWith("login")) return MessageIntent.LOGIN;
        if (messageBody.startsWith("balance")) return MessageIntent.BALANCE;
        if (messageBody.startsWith("fund")) return MessageIntent.FUND_ACCOUNT;
        if (messageBody.startsWith("recharge")) return MessageIntent.PRODUCT_RECHARGE;
        if (messageBody.startsWith("transaction")) return MessageIntent.TRANSACTION_QUERY;

        return MessageIntent.UNKNOWN;
    }

    private void handleLogin(String phoneNumber, String message, String messageId) {
        String[] parts = message.split(" ");
        if (parts.length < 3) {
            sendWhatsappMessage(phoneNumber, "Invalid login format. Use: login [accessCode] [password]",messageId);
            return;
        }

        AuthResponse response = hotUserApiService.login(parts[1], parts[2]);
      sendWhatsappMessage(phoneNumber, response.getToken()!=null ? "Login successful" : "Login failed", messageId);
    }

    private void handleBalanceInquiry(String phoneNumber, String message, String messageId) {
        String[] parts = message.split(" ");
        AccountType accountType = parts.length > 1
                ? AccountType.valueOf(parts[1].toUpperCase())
                : AccountType.MAIN;

        List<AccountBalance> balances = hotUserApiService.getAccountBalances(accountType);

        StringBuilder responseMessage = new StringBuilder("Your Account Balances:\n");
        balances.forEach(balance ->
                responseMessage.append(String.format(
                        "%s: $%.2f\n",
                        balance.getName(),
                        balance.getBalance()
                ))
        );

        sendWhatsappMessage(phoneNumber,responseMessage.toString(),messageId);

    }

    private void handleAccountFunding(String phoneNumber, String message, String messageId) {
        String[] parts = message.split(" ");
        if (parts.length < 5) {
            sendWhatsappMessage(phoneNumber, "Invalid fund format. Use: fund [agentRef] [target] [amount] [sourceType]",messageId);
            return;
        }

        String targetAccount = parts[2];
        double amount = Double.parseDouble(parts[3]);

        FundAccountResponse response = hotUserApiService.fundAccount(
                "agentRef123", 1, targetAccount, amount, 1, ""
        );

        sendWhatsappMessage(
                phoneNumber,
                response.isSuccessful()
                        ? "Account funded successfully"
                        : "Account funding failed: " + response.getMessage(),messageId
        );
    }

    private void handleProductRecharge(String phoneNumber, String message, String messageId) {
        String[] parts = message.split(" ");
        if (parts.length < 4) {
            sendWhatsappMessage(phoneNumber, "Invalid recharge format. Use: recharge [productId] [target] [amount]",messageId);
            return;
        }

        RechargeRequest rechargeRequest = new RechargeRequest(
                "agentRef123", Integer.parseInt(parts[1]), parts[2], Double.parseDouble(parts[3]), "", List.of()
        );

        RechargeResponse response = hotUserApiService.initiateRecharge(
                rechargeRequest// amount
        );

        sendWhatsappMessage(
                phoneNumber,
                response.isSuccessful()
                        ? "Recharge successful."
                        : "Recharge failed: " + response.getMessage(),messageId
        );

    }

    private void handleTransactionQuery(String phoneNumber, String message, String messageId) {
        String[] parts = message.split(" ");
        if (parts.length < 2) {

            sendWhatsappMessage(phoneNumber, "Invalid transaction query format. Use: transaction [agentReference]", messageId);
            return;
        }

        QueryTransactionResponse response = hotUserApiService.queryTransaction(parts[1]);


        sendWhatsappMessage(phoneNumber, "Transaction ref :" + response.getTransactionReference(), messageId);
    }

    private void sendHelpMessage(String phoneNumber, String messageId) {
        String helpText = """
                    Available Commands:
                    - login [accessCode] [password]
                    - balance [MAIN/SECONDARY]
                    - fund [agentRef] [target] [amount] [sourceType]
                    - recharge [productId] [target] [amount]
                    - transaction [agentReference]
                """;

        sendWhatsappMessage(phoneNumber, helpText, messageId);
    }

    private void sendWhatsappMessage(String phoneNumber, String _message, String messageId) {
        WhatsAppMessage message = WhatsAppMessage.builder()
                .to(phoneNumber)
                .messagingProduct("whatsapp")
                .recipientType("individual")
                .type("text")
                .text(new WhatsAppMessage.Text(false, _message))
                .context(
                        new WhatsAppMessage.Context(messageId)
                )
                .build();
        whatsAppMessageSender.sendMessage(message);
    }


}
