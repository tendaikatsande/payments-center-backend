package zw.co.link.FinPay.domain.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FundAccountResponse {
    private AccountType accountType;
    private String message;
    private boolean successful;
    private Object paymentData;
}
