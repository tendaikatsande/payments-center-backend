package zw.co.link.FinPay.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import zw.co.link.FinPay.domain.dtos.*;
import zw.co.link.FinPay.exceptions.HotUserApiException;
import zw.co.link.FinPay.services.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/hot")
public class HotUserApiController {

    private final HotUserApiService hotUserApiService;

    public HotUserApiController(HotUserApiService hotUserApiService) {
        this.hotUserApiService = hotUserApiService;

    }

    // Authentication Endpoints
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @RequestBody UserLoginRequest loginRequest
    ) {
        AuthResponse authResponse = hotUserApiService.login(
                loginRequest.getAccessCode(),
                loginRequest.getPassword()
        );
        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<AuthResponse> refreshToken(
            @RequestBody RefreshTokenRequest refreshTokenRequest
    ) {
        AuthResponse authResponse = hotUserApiService.refreshToken(
                refreshTokenRequest.getToken(),
                refreshTokenRequest.getRefreshToken()
        );
        return ResponseEntity.ok(authResponse);
    }

    // Account Balance Endpoints
    @GetMapping("/balances")
    public ResponseEntity<List<AccountBalance>> getAccountBalances(
            @RequestParam(defaultValue = "MAIN") AccountType accountType
    ) {
        List<AccountBalance> balances = hotUserApiService.getAccountBalances(accountType);
        return ResponseEntity.ok(balances);
    }

    @PostMapping("/fund-account")
    public ResponseEntity<FundAccountResponse> fundAccount(
            @RequestBody FundAccountRequest fundRequest
    ) {
        FundAccountResponse response = hotUserApiService.fundAccount(
                fundRequest.getAgentReference(),
                fundRequest.getAccountTypeId(),
                fundRequest.getTarget(),
                fundRequest.getAmount(),
                fundRequest.getSourceTypeId(),
                ""
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/products")
    public ResponseEntity<?> getProduct(
    ) {
        try {
            return ResponseEntity.ok(hotUserApiService.getProducts());
        } catch (HotUserApiException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Product Endpoints
    @GetMapping("/products/{productId}")
    public ResponseEntity<ProductsResponse> getProduct(
            @PathVariable int productId
    ) {
        try {
            ProductsResponse product = hotUserApiService.getProductDetails(productId);
            return ResponseEntity.ok(product);
        } catch (HotUserApiException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/products/recharge")
    public ResponseEntity<RechargeResponse> rechargeProduct(
            @RequestBody RechargeRequest rechargeRequest
    ) {
        RechargeResponse response = hotUserApiService.initiateRecharge(rechargeRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/products/recharge/complete")
    public ResponseEntity<RechargeResponse> completeRecharge(
            @RequestBody RechargeReserveCompleteRequest completeRequest
    ) {
        RechargeResponse response = hotUserApiService.completeRecharge(completeRequest);
        return ResponseEntity.ok(response);
    }

    // Transaction Query Endpoints
    @GetMapping("/transactions/{agentReference}")
    public ResponseEntity<QueryTransactionResponse> queryTransaction(
            @PathVariable String agentReference
    ) {
        QueryTransactionResponse transaction = hotUserApiService.queryTransaction(agentReference);
        return ResponseEntity.ok(transaction);
    }

    @GetMapping("/stock/{productId}")
    public ResponseEntity<QueryStockResponse> queryStock(
            @PathVariable int productId
    ) {
        QueryStockResponse stockResponse = hotUserApiService.queryStock(productId);
        return ResponseEntity.ok(stockResponse);
    }

    @GetMapping("/customer")
    public ResponseEntity<QueryCustomerResponse> queryCustomer(
            @RequestParam int productId,
            @RequestParam String accountNumber
    ) {
        QueryCustomerResponse customerResponse = hotUserApiService.queryCustomer(productId, accountNumber);
        return ResponseEntity.ok(customerResponse);
    }

    // Global Exception Handler
    @ExceptionHandler(HotUserApiException.class)
    public ResponseEntity<ErrorResponse> handleHotUserApiException(HotUserApiException ex) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }


}


