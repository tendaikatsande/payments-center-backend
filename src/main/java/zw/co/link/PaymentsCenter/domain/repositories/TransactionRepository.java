package zw.co.link.PaymentsCenter.domain.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import zw.co.link.PaymentsCenter.domain.Transaction;
import zw.co.link.PaymentsCenter.domain.dtos.Balance;
import zw.co.link.PaymentsCenter.domain.dtos.BalanceProjection;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long>, JpaSpecificationExecutor<Transaction> {
    Page<Transaction> findByUserId(Long userId, Pageable pageable);
    @Query("SELECT t.runningBalance " +
            "FROM Transaction t " +
            "WHERE t.user.id = :userId AND t.currency = :currency " +
            "ORDER BY t.createdDate DESC LIMIT 1")
    Optional<BigDecimal> findLastRunningBalanceByUserAndCurrency(@Param("userId") Long userId,
                                                                 @Param("currency") String currency);

    @Query("SELECT t.currency AS currency, MAX(t.runningBalance) AS balance " +
            "FROM Transaction t " +
            "WHERE t.user.id = :userId " +
            "GROUP BY t.currency")
    List<BalanceProjection> findBalancesByUser(@Param("userId") Long userId);

}



