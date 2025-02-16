package zw.co.link.FinPay.domain.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockItem{

    private int productId;
    private String name;
    private String productCode;
    private String description;
    private double amount;
    private String currency;
    private int walletTypeId;
}
