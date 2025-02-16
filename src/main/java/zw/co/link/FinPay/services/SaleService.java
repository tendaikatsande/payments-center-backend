package zw.co.link.FinPay.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import zw.co.link.FinPay.domain.Sale;

public interface SaleService {

    public Sale create(@Validated Sale sale);
    public Sale get(Long saleId);
    public Page<Sale> getByUser(Long userId, Pageable pageable);

}
