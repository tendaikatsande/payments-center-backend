package zw.co.link.FinPay.domain.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Products {
    private int productId;
    private String name;
    private int accountTypeId;
    private List<RechargeOptionDescription> requiredOptions;
    private Object metaData;
}


