package zw.co.link.PaymentsCenter.domain.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RechargeRequest {
    private String agentReference;
    private int productId;  // ID of the product to be recharged
    private String target;     // Target identifier (e.g., phone number or account ID)
    private double amount;     // Amount to recharge
    private String customerSMS;
    private List<RechargeOption> rechargeOptions;

}