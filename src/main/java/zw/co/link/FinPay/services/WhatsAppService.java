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

    @Value("${whatsapp.business.phone.number.id}")
    private String phoneNumberId;

    @Value("${whatsapp.access.token}")
    private String accessToken;

    public WhatsAppService(
            HotUserApiService hotUserApiService,
            WhatsAppMessageSender whatsAppMessageSender
    ) {
        this.hotUserApiService = hotUserApiService;
        this.whatsAppMessageSender = whatsAppMessageSender;
    }

    public void processIncomingMessage(WhatsAppWebhookPayload payload) {
        WhatsAppMessage message = extractMessage(payload);
        if (message == null) return;

        try {
            switch (determineMessageIntent(message.getBody())) {
                case LOGIN:
                    handleLogin(message);
                    break;
                case BALANCE:
                    handleBalanceInquiry(message);
                    break;
                case FUND_ACCOUNT:
                    handleAccountFunding(message);
                    break;
                case PRODUCT_RECHARGE:
                    handleProductRecharge(message);
                    break;
                case TRANSACTION_QUERY:
                    handleTransactionQuery(message);
                    break;
                default:
                    sendHelpMessage(message.getFrom());
            }
        } catch (Exception e) {
            logger.error("Error processing message", e);
            sendErrorMessage(message.getFrom(), e.getMessage());
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

    private void handleLogin(WhatsAppMessage message) {
        String[] parts = message.getBody().split(" ");
        if (parts.length < 3) {
            sendErrorMessage(message.getFrom(), "Invalid login format. Use: login [accessCode] [password]");
            return;
        }

        AuthResponse response = hotUserApiService.login(parts[1], parts[2]);
        whatsAppMessageSender.sendMessage(
                message.getFrom(),
                "Login successful. Token issued."
        );
    }

    private void handleBalanceInquiry(WhatsAppMessage message) {
        String[] parts = message.getBody().split(" ");
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

        whatsAppMessageSender.sendMessage(message.getFrom(), responseMessage.toString());
    }

    private void handleAccountFunding(WhatsAppMessage message) {
        String[] parts = message.getBody().split(" ");
        if (parts.length < 5) {
            sendErrorMessage(message.getFrom(), "Invalid fund format. Use: fund [agentRef] [target] [amount] [sourceType]");
            return;
        }

        String targetAccount = parts[2];
        double amount = Double.parseDouble(parts[3]);

        FundAccountResponse response = hotUserApiService.fundAccount(
                "agentRef123", 1, targetAccount, amount, 1, ""
        );

        whatsAppMessageSender.sendMessage(
                message.getFrom(),
                response.isSuccessful()
                        ? "Account funded successfully"
                        : "Account funding failed: " + response.getMessage()
        );
    }

    private void handleProductRecharge(WhatsAppMessage message) {
        String[] parts = message.getBody().split(" ");
        if (parts.length < 4) {
            sendErrorMessage(message.getFrom(), "Invalid recharge format. Use: recharge [productId] [target] [amount]");
            return;
        }

        RechargeRequest rechargeRequest = new RechargeRequest(
                "agentRef123", Integer.parseInt(parts[1]), parts[2], Double.parseDouble(parts[3]), "", List.of()
        );

        RechargeResponse response = hotUserApiService.initiateRecharge(
                rechargeRequest// amount
        );

        whatsAppMessageSender.sendMessage(
                message.getFrom(),
                response.isSuccessful()
                        ? "Recharge successful."
                        : "Recharge failed: " + response.getMessage()
        );
    }

    private void handleTransactionQuery(WhatsAppMessage message) {
        String[] parts = message.getBody().split(" ");
        if (parts.length < 2) {
            sendErrorMessage(message.getFrom(), "Invalid transaction query format. Use: transaction [agentReference]");
            return;
        }

        QueryTransactionResponse response = hotUserApiService.queryTransaction(parts[1]);

        whatsAppMessageSender.sendMessage(
                message.getFrom(),
                "Transaction ref :" + response.getTransactionReference()

        );
    }

    private void sendHelpMessage(String phoneNumber) {
        String helpText = """
                    Available Commands:
                    - login [accessCode] [password]
                    - balance [MAIN/SECONDARY]
                    - fund [agentRef] [target] [amount] [sourceType]
                    - recharge [productId] [target] [amount]
                    - transaction [agentReference]
                """;
        whatsAppMessageSender.sendMessage(phoneNumber, helpText);
    }

    private void sendErrorMessage(String phoneNumber, String errorMessage) {
        whatsAppMessageSender.sendMessage(
                phoneNumber,
                "Error: " + errorMessage +
                        "\nType 'help' for available commands."
        );
    }

    private WhatsAppMessage extractMessage(WhatsAppWebhookPayload payload) {
        if (payload.getEntry() == null || payload.getEntry().isEmpty()) {
            return null;
        }

        WhatsAppWebhookPayload.Change change = payload.getEntry().get(0).getChanges().get(0);
        if (change.getValue().getMessages() == null || change.getValue().getMessages().isEmpty()) {
            return null;
        }

        WhatsAppWebhookPayload.Message msg = change.getValue().getMessages().get(0);
        return new WhatsAppMessage(
                msg.getFrom(),
                msg.getText() != null ? msg.getText().getBody() : ""
        );
    }
}
