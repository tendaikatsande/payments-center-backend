package zw.co.link.FinPay.domain.dtos;

import java.math.BigDecimal;

public interface BalanceProjection {
    String getCurrency();
    BigDecimal getBalance();
}
