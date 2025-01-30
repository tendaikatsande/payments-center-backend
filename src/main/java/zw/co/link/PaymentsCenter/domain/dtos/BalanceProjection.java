package zw.co.link.PaymentsCenter.domain.dtos;

import java.math.BigDecimal;

public interface BalanceProjection {
    String getCurrency();
    BigDecimal getBalance();
}
