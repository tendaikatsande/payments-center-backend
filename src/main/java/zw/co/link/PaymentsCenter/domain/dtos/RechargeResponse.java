package zw.co.link.PaymentsCenter.domain.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class RechargeResponse {
    private boolean successful;
    private int rechargeId;
    private double amount;
    private double discount;
    private AccountBalance balance;
    private String message;
    private Object rechargeData;
}
