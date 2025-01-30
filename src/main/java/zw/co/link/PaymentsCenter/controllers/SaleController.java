package zw.co.link.PaymentsCenter.controllers;

import org.apache.coyote.Response;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import zw.co.link.PaymentsCenter.domain.Sale;
import zw.co.link.PaymentsCenter.services.SaleService;

@RestController
@RequestMapping("api/v1/sales")
public class SaleController {

    private final SaleService saleService;

    public SaleController(SaleService saleService) {
        this.saleService = saleService;
    }

    @GetMapping("user/{userId}")
    public ResponseEntity<?> getSales(@PathVariable Long userId, Pageable pageable) {
        return ResponseEntity.ok().body(saleService.getByUser(userId, pageable));
    }

    @GetMapping("/{saleId}")
    public ResponseEntity<?> getSale(@PathVariable Long saleId) {
        return ResponseEntity.ok().body(saleService.get(saleId));
    }

    @PostMapping
    public ResponseEntity<?> createSale(@RequestBody Sale sale) {
        return ResponseEntity.ok().body(saleService.create(sale));
    }

}
