package zw.co.link.PaymentsCenter.domain.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountBalance {
    private int accountTypeId;
    private String name;
    private double balance;
}


