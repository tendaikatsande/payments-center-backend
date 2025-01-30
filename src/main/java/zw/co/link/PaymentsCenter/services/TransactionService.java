package zw.co.link.PaymentsCenter.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import zw.co.link.PaymentsCenter.domain.Transaction;
import zw.co.link.PaymentsCenter.domain.dtos.Balance;

import java.util.List;

public interface TransactionService {
    Transaction save(Transaction transaction);
    Transaction update(Long id,Transaction transaction);
    Transaction get(Long transactionId);
    Page<Transaction> getAll(Pageable pageable);
    Page<Transaction> getAll(Long userId,Pageable pageable);

    List<Balance> getUserBalances(Long userId);
}
