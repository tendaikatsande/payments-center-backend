package zw.co.link.FinPay.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import zw.co.link.FinPay.domain.dtos.*;
import zw.co.link.FinPay.exceptions.HotUserApiException;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class HotUserApiService {
    private final RestTemplate restTemplate;

    private final RestTemplate noAuthRestTemplate;

    @Value("${hot-user.api.base-url}")
    private String BASE_URL;

    @Value("${hot-user.api.access-code}")
    private String accessCode;

    @Value("${hot-user.api.password}")
    private String password;

    private String currentAccessToken;
    private String currentRefreshToken;
    private LocalDateTime tokenExpiryTime;

    public HotUserApiService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.noAuthRestTemplate = new RestTemplate();
        this.restTemplate.setInterceptors(Collections.singletonList((request, body, execution) -> {
            request.getHeaders().setBearerAuth(getCurrentValidToken());
            return execution.execute(request, body);
        }));
    }

    public synchronized String getCurrentValidToken() {
        if (isTokenExpiredOrInvalid()) {
            refreshToken();
        }
        return currentAccessToken;
    }

    private boolean isTokenExpiredOrInvalid() {
        return currentAccessToken == null ||
                tokenExpiryTime == null ||
                LocalDateTime.now().isAfter(tokenExpiryTime);
    }

    private void refreshToken() {
        try {
            if (currentRefreshToken != null) {
                try {
                    AuthResponse refreshResponse = refreshTokenWithCurrentRefreshToken();
                    updateTokenDetails(refreshResponse);
                    return;
                } catch (Exception refreshException) {
                    log.warn("Token refresh failed, falling back to login", refreshException);
                }
            }

            AuthResponse loginResponse = login(accessCode, password);
            updateTokenDetails(loginResponse);
        } catch (Exception e) {
            log.error("Failed to obtain new token", e);
            throw new RuntimeException("Unable to authenticate", e);
        }
    }

    private void updateTokenDetails(AuthResponse authResponse) {
        currentAccessToken = authResponse.getToken();
        currentRefreshToken = authResponse.getRefreshToken();
        tokenExpiryTime = LocalDateTime.now().plusHours(1);
    }

    private AuthResponse refreshTokenWithCurrentRefreshToken() {
        RefreshTokenRequest refreshRequest = new RefreshTokenRequest(currentAccessToken, currentRefreshToken);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<RefreshTokenRequest> request = new HttpEntity<>(refreshRequest, headers);

        ResponseEntity<AuthResponse> response = noAuthRestTemplate.postForEntity(
                BASE_URL + "/identity/refresh",
                request,
                AuthResponse.class
        );

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            return response.getBody();
        } else {
            throw new RuntimeException("Token refresh failed");
        }
    }

    public AuthResponse login(String accessCode, String password) {
        try {
            AuthRequest request = new AuthRequest(accessCode, password);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<AuthRequest> entity = new HttpEntity<>(request, headers);

            ResponseEntity<AuthResponse> response = noAuthRestTemplate.postForEntity(
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
            RefreshTokenRequest request = new RefreshTokenRequest(token, refreshToken);
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

    public List<AccountBalance> getAccountBalances(AccountType accountType) {
        try {
            ResponseEntity<BalanceResponse> response = restTemplate.exchange(
                    BASE_URL + "/account/balance/{accountTypeId}",
                    HttpMethod.GET,
                    null,
                    BalanceResponse.class,
                    accountType.getId()
            );

            return response.getBody() != null ? response.getBody().getBalances() : Collections.emptyList();
        } catch (RestClientException e) {
            throw new HotUserApiException("Failed to retrieve account balances", e);
        }
    }

    public FundAccountResponse fundAccount(
            String agentReference,
            int accountTypeId,
            String target,
            double amount,
            int sourceTypeId,
            String onBehalfOf
    ) {
        try {
            FundAccountRequest request = new FundAccountRequest(
                    agentReference,
                    accountTypeId,
                    target,
                    amount,
                    sourceTypeId,
                    onBehalfOf
            );

            ResponseEntity<FundAccountResponse> response = restTemplate.postForEntity(
                    BASE_URL + "/account/fund",
                    request,
                    FundAccountResponse.class
            );

            return response.getBody();
        } catch (RestClientException e) {
            throw new HotUserApiException("Account funding failed", e);
        }
    }

    public ProductsResponse getProducts() {
        try {
            ResponseEntity<ProductsResponse> response = restTemplate.exchange(
                    BASE_URL + "/products/0",
                    HttpMethod.GET,
                    null,
                    ProductsResponse.class
            );

            return response.getBody();
        } catch (RestClientException e) {
            throw new HotUserApiException("Failed to retrieve products", e);
        }
    }

    public ProductsResponse getProductDetails(int productId) {
        try {
            ResponseEntity<ProductsResponse> response = restTemplate.exchange(
                    BASE_URL + "/products/{productId}",
                    HttpMethod.GET,
                    null,
                    ProductsResponse.class,
                    productId
            );

            return response.getBody();
        } catch (RestClientException e) {
            throw new HotUserApiException("Failed to retrieve product details", e);
        }
    }

    public RechargeResponse initiateRecharge(RechargeRequest rechargeRequest) {
        try {
            ResponseEntity<RechargeResponse> response = restTemplate.postForEntity(
                    BASE_URL + "/products/recharge",
                    rechargeRequest,
                    RechargeResponse.class
            );

            return response.getBody();
        } catch (RestClientException e) {
            throw new HotUserApiException("Recharge initiation failed", e);
        }
    }

    public RechargeResponse completeRecharge(RechargeReserveCompleteRequest completeRequest) {
        try {
            ResponseEntity<RechargeResponse> response = restTemplate.postForEntity(
                    BASE_URL + "/products/recharge/complete",
                    completeRequest,
                    RechargeResponse.class
            );

            return response.getBody();
        } catch (RestClientException e) {
            throw new HotUserApiException("Recharge completion failed", e);
        }
    }

    public QueryTransactionResponse queryTransaction(String agentReference) {
        try {
            ResponseEntity<QueryTransactionResponse> response = restTemplate.exchange(
                    BASE_URL + "/query/transaction/{agentReference}",
                    HttpMethod.GET,
                    null,
                    QueryTransactionResponse.class,
                    agentReference
            );

            return response.getBody();
        } catch (RestClientException e) {
            throw new HotUserApiException("Transaction query failed", e);
        }
    }

    public QueryStockResponse queryStock(int productId) {
        try {
            ResponseEntity<QueryStockResponse> response = restTemplate.exchange(
                    BASE_URL + "/query/stock/{productId}",
                    HttpMethod.GET,
                    null,
                    QueryStockResponse.class,
                    productId
            );

            return response.getBody();
        } catch (RestClientException e) {
            throw new HotUserApiException("Stock query failed", e);
        }
    }

    public QueryCustomerResponse queryCustomer(int productId, String accountNumber) {
        try {
            ResponseEntity<QueryCustomerResponse> response = restTemplate.exchange(
                    BASE_URL + "/query/customer/{productId}/{accountNumber}",
                    HttpMethod.GET,
                    null,
                    QueryCustomerResponse.class,
                    productId,
                    accountNumber
            );

            return response.getBody();
        } catch (RestClientException e) {
            throw new HotUserApiException("Customer query failed", e);
        }
    }
}