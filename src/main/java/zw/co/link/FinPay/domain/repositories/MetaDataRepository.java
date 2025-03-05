package zw.co.link.FinPay.domain.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import zw.co.link.FinPay.domain.MetaData;

public interface MetaDataRepository extends JpaRepository<MetaData, Long> , JpaSpecificationExecutor<MetaData> {
  }