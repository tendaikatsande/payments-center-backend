package zw.co.link.PaymentsCenter.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import zw.co.link.PaymentsCenter.domain.Sale;

import java.util.List;

public interface SaleService {

    public Sale create(@Validated Sale sale);
    public Sale get(Long saleId);
    public Page<Sale> getByUser(Long userId, Pageable pageable);

}
