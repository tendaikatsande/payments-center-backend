package zw.co.link.PaymentsCenter.domain.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BalanceResponse {
    private List<AccountBalance> balances;
}
