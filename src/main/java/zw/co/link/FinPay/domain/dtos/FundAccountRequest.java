package zw.co.link.FinPay.domain.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public  class FundAccountRequest {
    @NotNull(message = "Agent Reference cannot be null")
    private String agentReference;
    private int accountTypeId;
    private String target;
    @Positive(message = "Amount must be positive")
    private double amount;
    private int sourceTypeId;
    private String onBehalfOf;

}
