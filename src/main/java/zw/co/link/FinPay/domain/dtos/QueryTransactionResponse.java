package zw.co.link.FinPay.domain.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QueryTransactionResponse {
    private String transactionReference;
    private String rawData;
}
