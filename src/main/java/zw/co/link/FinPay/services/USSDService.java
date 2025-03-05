package zw.co.link.FinPay.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import zw.co.link.FinPay.domain.dtos.*;

import java.util.ArrayList;
import java.util.List;

@Service
public class USSDService {
    private final HotUserApiService hotUserApiService;
    private final USSDSessionManager sessionManager;
    private final Logger logger = LoggerFactory.getLogger(USSDService.class);

    public USSDService(
            HotUserApiService hotUserApiService,
            USSDSessionManager sessionManager
    ) {
        this.hotUserApiService = hotUserApiService;
        this.sessionManager = sessionManager;
    }

    public String processUSSDRequest(USSDRequest request) {
        USSDSession session = sessionManager.getOrCreateSession(request.getPhoneNumber());

        try {
            return processMenuNavigation(session, request);
        } catch (Exception e) {
            logger.error("USSD Processing Error", e);
            return buildErrorResponse(session, e);
        }
    }

    private String processMenuNavigation(USSDSession session, USSDRequest request) {
        switch (session.getCurrentState()) {
            case INITIAL:
            case MAIN_MENU:
                return handleMainMenu(session, request);
            case LOGIN:
                return handleLogin(session, request);
            case BALANCE_INQUIRY:
                return handleBalanceInquiry(session, request);
            case FUND_ACCOUNT:
                return handleFundAccount(session, request);
            case RECHARGE:
                return handleRecharge(session, request);
            case TRANSACTION_QUERY:
                return handleTransactionQuery(session, request);
            case CONFIRM_ACTION:
                return handleConfirmation(session, request);
            default:
                return buildMainMenu(session);
        }
    }

    private String handleMainMenu(USSDSession session, USSDRequest request) {
        String userInput = request.getUserInput();
        switch (userInput) {
            case "1":
                session.setCurrentState(USSDMenuState.LOGIN);
                return buildLoginPrompt();
            case "2":
                session.setCurrentState(USSDMenuState.BALANCE_INQUIRY);
                return buildBalanceInquiryMenu();
            case "3":
                session.setCurrentState(USSDMenuState.FUND_ACCOUNT);
                return buildFundAccountPrompt();
            case "4":
                session.setCurrentState(USSDMenuState.RECHARGE);
                return buildRechargePrompt();
            case "5":
                session.setCurrentState(USSDMenuState.TRANSACTION_QUERY);
                return buildTransactionQueryPrompt();
            default:
                return buildMainMenu(session);
        }
    }

    private String handleLogin(USSDSession session, USSDRequest request) {
        String[] loginCredentials = request.getUserInput().split("\\*");
        if (loginCredentials.length < 2) {
            return "Invalid login format. Enter access code*password";
        }

        try {
            AuthResponse authResponse = hotUserApiService.login(
                    loginCredentials[0],
                    loginCredentials[1]
            );

            session.getSessionData().put("token", authResponse.getToken());
            session.setCurrentState(USSDMenuState.MAIN_MENU);

            return "Login Successful! Press 0 to continue.";
        } catch (Exception e) {
            return "Login Failed. " + e.getMessage();
        }
    }

    private String handleBalanceInquiry(USSDSession session, USSDRequest request) {
        try {
            String token = (String) session.getSessionData().get("token");
            List<AccountBalance> balances = hotUserApiService.getAccountBalances(AccountType.MAIN);
            StringBuilder response = new StringBuilder("Your Account Balances:\n");
            for (AccountBalance balance : balances) {
                response.append(AccountType.MAIN).append(": ").append(balance.getBalance()).append("\n");
            }
            session.setCurrentState(USSDMenuState.MAIN_MENU);
            return response.toString();
        } catch (Exception e) {
            return "Failed to retrieve account balance. " + e.getMessage();
        }
    }

    private String handleFundAccount(USSDSession session, USSDRequest request) {
        String[] inputs = request.getUserInput().split("\\*");
        if (inputs.length < 2) {
            return "Invalid format. Enter target account*amount";
        }

        try {
            String token = (String) session.getSessionData().get("token");
            String targetAccount = inputs[0];
            double amount = Double.parseDouble(inputs[1]);

            FundAccountResponse response = hotUserApiService.fundAccount(
                    "agentRef123", 1, targetAccount, amount, 1, ""
            );

            session.setCurrentState(USSDMenuState.MAIN_MENU);
            return "Account funded successfully. Ref: " + response.getMessage();
        } catch (Exception e) {
            return "Failed to fund account. " + e.getMessage();
        }
    }

    private String handleRecharge(USSDSession session, USSDRequest request) {
        String[] inputs = request.getUserInput().split("\\*");
        if (inputs.length < 2) {
            return "Invalid format. Enter product ID*amount";
        }

        try {
            String token = (String) session.getSessionData().get("token");
            int productId = Integer.parseInt(inputs[0]);
            double amount = Double.parseDouble(inputs[1]);
            String target = "";
            String customerSMS = "";
            List<RechargeOption> rechargeOptions = new ArrayList<>();


            RechargeRequest rechargeRequest = new RechargeRequest(
                    "agentRef123", productId, target, amount, customerSMS, rechargeOptions);
            RechargeResponse response = hotUserApiService.initiateRecharge(rechargeRequest);

            session.setCurrentState(USSDMenuState.MAIN_MENU);
            return "Recharge successful. Ref: " + response.getRechargeId();
        } catch (Exception e) {
            return "Recharge failed. " + e.getMessage();
        }
    }

    private String handleTransactionQuery(USSDSession session, USSDRequest request) {
        try {
            String token = (String) session.getSessionData().get("token");
            String agentReference = request.getUserInput();

            QueryTransactionResponse response = hotUserApiService.queryTransaction(agentReference);
            session.setCurrentState(USSDMenuState.MAIN_MENU);
            return "Transaction Details: " + response.getTransactionReference();
        } catch (Exception e) {
            return "Transaction query failed. " + e.getMessage();
        }
    }

    private String handleConfirmation(USSDSession session, USSDRequest request) {
        session.setCurrentState(USSDMenuState.MAIN_MENU);
        return "Action confirmed. Returning to main menu.";
    }

    private String buildMainMenu(USSDSession session) {
        session.setCurrentState(USSDMenuState.MAIN_MENU);
        return "Welcome to Hot User Services:\n" +
                "1. Login\n" +
                "2. Check Balance\n" +
                "3. Fund Account\n" +
                "4. Recharge Product\n" +
                "5. Transaction Query\n" +
                "0. Exit";
    }

    private String buildLoginPrompt() {
        return "Enter your login credentials in the format access code*password";
    }

    private String buildBalanceInquiryMenu() {
        return "Fetching your account balance...";
    }

    private String buildFundAccountPrompt() {
        return "Enter the target account and amount in the format target account*amount";
    }

    private String buildRechargePrompt() {
        return "Enter the product ID and amount in the format product ID*amount";
    }

    private String buildTransactionQueryPrompt() {
        return "Enter the agent reference for the transaction query.";
    }

    private String buildErrorResponse(USSDSession session, Exception e) {
        sessionManager.removeSession(session.getPhoneNumber());
        return "Error occurred. Please try again. " + e.getMessage();
    }
}
