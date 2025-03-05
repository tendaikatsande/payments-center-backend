package zw.co.link.FinPay.domain.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import zw.co.link.FinPay.domain.HotProduct;

import java.util.Optional;

public interface HotProductRepository extends JpaRepository<HotProduct, Long>, JpaSpecificationExecutor<HotProduct> {
    Optional<HotProduct> findByProductId(int productId);
}