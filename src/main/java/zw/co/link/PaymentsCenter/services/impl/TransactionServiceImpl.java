package zw.co.link.PaymentsCenter.services.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import zw.co.link.PaymentsCenter.domain.Transaction;
import zw.co.link.PaymentsCenter.domain.dtos.Balance;
import zw.co.link.PaymentsCenter.domain.dtos.TransactionType;
import zw.co.link.PaymentsCenter.domain.repositories.TransactionRepository;
import zw.co.link.PaymentsCenter.services.TransactionService;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransactionServiceImpl implements TransactionService {
    private final TransactionRepository transactionRepository;

    public TransactionServiceImpl(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Override
    public Transaction save( Transaction transaction) {
        Long userId = transaction.getUser().getId();
        String currency =  transaction.getCurrency();

        // Fetch the most recent transaction for the user in the same currency
        BigDecimal lastBalance = transactionRepository.findLastRunningBalanceByUserAndCurrency(userId, currency)
                .orElse(BigDecimal.ZERO);

        // Calculate the new running balance
        BigDecimal newBalance = transaction.getType() == TransactionType.CREDIT
                ? lastBalance.add(transaction.getAmount())
                : lastBalance.subtract(transaction.getAmount());

        // Set the running balance in the new transaction
        transaction.setRunningBalance(newBalance);
        transaction.setId(null);

        // Save the transaction
        return transactionRepository.save(transaction);
    }

    @Override
    public Transaction update(Long id, Transaction transaction) {
        Transaction existingTransaction = transactionRepository.findById(id).orElseThrow();
        existingTransaction.setCurrency(transaction.getCurrency());
        existingTransaction.setType(transaction.getType());
        return transactionRepository.save(existingTransaction);
    }

    @Override
    public Transaction get(Long id) {
        return transactionRepository.findById(id).orElseThrow();
    }

    @Override
    public Page<Transaction> getAll(Pageable pageable) {
        return transactionRepository.findAll(pageable);
    }

    @Override
    public Page<Transaction> getAll(Long userId, Pageable pageable) {
        return transactionRepository.findByUserId(userId, pageable);
    }
    @Override
    public List<Balance> getUserBalances(Long userId) {
        return transactionRepository.findBalancesByUser(userId).stream()
                .map(balanceProjection -> Balance.builder()
                        .currency(balanceProjection.getCurrency())  // from BalanceProjection
                        .balance(balanceProjection.getBalance())    // from BalanceProjection
                        .build())
                .collect(Collectors.toList());
    }

}
