package zw.co.link.FinPay.services.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import zw.co.link.FinPay.domain.Sale;
import zw.co.link.FinPay.domain.repositories.SaleRepository;
import zw.co.link.FinPay.services.SaleService;

@Service
public class SaleServiceImpl implements SaleService {

    private final SaleRepository saleRepository;

    public SaleServiceImpl(SaleRepository SaleRepository) {
        this.saleRepository = SaleRepository;
    }

    @Override
    public Sale create(Sale Sale) {
        return saleRepository.save(Sale);
    }

    @Override
    public Sale get(Long saleId) {
        return saleRepository.findById(saleId).orElseThrow();
    }

    @Override
    public Page<Sale> getByUser(Long userId, Pageable pageable) {
        return saleRepository.findByUserId(userId,pageable);
    }







}
