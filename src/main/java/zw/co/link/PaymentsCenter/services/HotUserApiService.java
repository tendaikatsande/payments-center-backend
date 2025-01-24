package zw.co.link.PaymentsCenter.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import zw.co.link.PaymentsCenter.domain.dtos.*;
import zw.co.link.PaymentsCenter.exceptions.HotUserApiException;

import java.util.Collections;
import java.util.List;

@Service
public class HotUserApiService {

    private final RestTemplate restTemplate;
    @Value("${hot-user.api.key}")
    private  String apiKey;
    @Value("${hot-user.api.base-url}")
    private String BASE_URL;

    public HotUserApiService(
            RestTemplate restTemplate
    ) {
        this.restTemplate = restTemplate;
    }

    // Authentication methods
    public AuthResponse login(String accessCode, String password) {
        try {
            AuthRequest request = new AuthRequest();
            request.setAccessCode(accessCode);
            request.setPassword(password);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<AuthRequest> entity = new HttpEntity<>(request, headers);

            ResponseEntity<AuthResponse> response = restTemplate.postForEntity(
                    BASE_URL + "/identity/login",
                    entity,
                    AuthResponse.class
            );

            return response.getBody();
        } catch (RestClientException e) {
            throw new HotUserApiException("Login failed", e);
        }
    }

    public AuthResponse refreshToken(String token, String refreshToken) {
        try {
            RefreshTokenRequest request = new RefreshTokenRequest();
            request.setToken(token);
            request.setRefreshToken(refreshToken);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<RefreshTokenRequest> entity = new HttpEntity<>(request, headers);

            ResponseEntity<AuthResponse> response = restTemplate.postForEntity(
                    BASE_URL + "/identity/refresh",
                    entity,
                    AuthResponse.class
            );

            return response.getBody();
        } catch (RestClientException e) {
            throw new HotUserApiException("Token refresh failed", e);
        }
    }

    // Account Balance Retrieval
    public List<AccountBalance> getAccountBalances(AccountType accountType) {
        try {
            HttpHeaders headers = createAuthenticatedHeaders();
            HttpEntity<?> entity = new HttpEntity<>(headers);

            ResponseEntity<BalanceResponse> response = restTemplate.exchange(
                    BASE_URL + "/account/balance/{accountTypeId}",
                    HttpMethod.GET,
                    entity,
                    BalanceResponse.class,
                    accountType.getId()
            );

            return response.getBody() != null ? response.getBody().getBalances() : Collections.emptyList();
        } catch (RestClientException e) {
            throw new HotUserApiException("Failed to retrieve account balances", e);
        }
    }

    // Fund Account Method
    public FundAccountResponse fundAccount(
            String agentReference,
            int accountTypeId,
            String target,
            double amount,
            int sourceTypeId,
            String onBehalfOf
    ) {
        try {
            FundAccountRequest request = new FundAccountRequest();
            request.setAgentReference(agentReference);
            request.setAccountTypeId(accountTypeId);
            request.setTarget(target);
            request.setAmount(amount);
            request.setSourceTypeId(sourceTypeId);

            HttpHeaders headers = createAuthenticatedHeaders();
            HttpEntity<FundAccountRequest> entity = new HttpEntity<>(request, headers);

            ResponseEntity<FundAccountResponse> response = restTemplate.postForEntity(
                    BASE_URL + "/account/fund",
                    entity,
                    FundAccountResponse.class
            );

            return response.getBody();
        } catch (RestClientException e) {
            throw new HotUserApiException("Account funding failed", e);
        }
    }

    // Helper method to create authenticated headers
    private HttpHeaders createAuthenticatedHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);
        headers.set("authorization","Bearer "+apiKey);
        return headers;
    }

    // Product Details Retrieval
    public ProductsResponse getProductDetails(int productId) {
        try {
            HttpHeaders headers = createAuthenticatedHeaders();
            HttpEntity<?> entity = new HttpEntity<>(headers);

            ResponseEntity<ProductsResponse> response = restTemplate.exchange(
                    BASE_URL + "/products/{productId}",
                    HttpMethod.GET,
                    entity,
                    ProductsResponse.class,
                    productId
            );

            return response.getBody();
        } catch (RestClientException e) {
            throw new HotUserApiException("Failed to retrieve product details", e);
        }
    }

    // Recharge Initiation
    public RechargeResponse initiateRecharge(RechargeRequest rechargeRequest) {
        try {
            HttpHeaders headers = createAuthenticatedHeaders();
            HttpEntity<RechargeRequest> entity = new HttpEntity<>(rechargeRequest, headers);

            ResponseEntity<RechargeResponse> response = restTemplate.postForEntity(
                    BASE_URL + "/products/recharge",
                    entity,
                    RechargeResponse.class
            );

            return response.getBody();
        } catch (RestClientException e) {
            throw new HotUserApiException("Recharge initiation failed", e);
        }
    }

    // Complete Recharge
    public RechargeResponse completeRecharge(RechargeReserveCompleteRequest completeRequest) {
        try {
            HttpHeaders headers = createAuthenticatedHeaders();
            HttpEntity<RechargeReserveCompleteRequest> entity = new HttpEntity<>(completeRequest, headers);

            ResponseEntity<RechargeResponse> response = restTemplate.postForEntity(
                    BASE_URL + "/products/recharge/complete",
                    entity,
                    RechargeResponse.class
            );

            return response.getBody();
        } catch (RestClientException e) {
            throw new HotUserApiException("Recharge completion failed", e);
        }
    }

    // Transaction Query
    public QueryTransactionResponse queryTransaction(String agentReference) {
        try {
            HttpHeaders headers = createAuthenticatedHeaders();
            HttpEntity<?> entity = new HttpEntity<>(headers);

            ResponseEntity<QueryTransactionResponse> response = restTemplate.exchange(
                    BASE_URL + "/query/transaction/{agentReference}",
                    HttpMethod.GET,
                    entity,
                    QueryTransactionResponse.class,
                    agentReference
            );

            return response.getBody();
        } catch (RestClientException e) {
            throw new HotUserApiException("Transaction query failed", e);
        }
    }

    // Stock Query
    public QueryStockResponse queryStock(int productId) {
        try {
            HttpHeaders headers = createAuthenticatedHeaders();
            HttpEntity<?> entity = new HttpEntity<>(headers);

            ResponseEntity<QueryStockResponse> response = restTemplate.exchange(
                    BASE_URL + "/query/stock/{productId}",
                    HttpMethod.GET,
                    entity,
                    QueryStockResponse.class,
                    productId
            );

            return response.getBody();
        } catch (RestClientException e) {
            throw new HotUserApiException("Stock query failed", e);
        }
    }

    // Customer Query
    public QueryCustomerResponse queryCustomer(int productId, String accountNumber) {
        try {
            HttpHeaders headers = createAuthenticatedHeaders();
            HttpEntity<?> entity = new HttpEntity<>(headers);

            ResponseEntity<QueryCustomerResponse> response = restTemplate.exchange(
                    BASE_URL + "/query/customer/{productId}/{accountNumber}",
                    HttpMethod.GET,
                    entity,
                    QueryCustomerResponse.class,
                    productId,
                    accountNumber
            );

            return response.getBody();
        } catch (RestClientException e) {
            throw new HotUserApiException("Customer query failed", e);
        }
    }


    public RechargeResponse rechargeProduct(RechargeRequest rechargeRequest) {
        return null;
    }

    public ProductsResponse getProducts() {
        try {
            HttpHeaders headers = createAuthenticatedHeaders();
            HttpEntity<?> entity = new HttpEntity<>(headers);

            ResponseEntity<ProductsResponse> response = restTemplate.exchange(
                    BASE_URL + "/products/0",
                    HttpMethod.GET,
                    entity,
                    ProductsResponse.class
            );

            return response.getBody();
        } catch (RestClientException e) {
            throw new HotUserApiException("Failed to retrieve products", e);
        }
    }
}

