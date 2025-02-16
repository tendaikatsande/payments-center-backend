package zw.co.link.FinPay.domain.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RechargeReserveCompleteRequest {
    private String agentReference;
    private String originalReference;
    private boolean confirmed;
    private String confirmationData;
}
