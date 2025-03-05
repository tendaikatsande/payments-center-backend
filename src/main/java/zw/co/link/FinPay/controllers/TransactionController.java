package zw.co.link.FinPay.controllers;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import zw.co.link.FinPay.domain.Transaction;
import zw.co.link.FinPay.domain.dtos.Balance;
import zw.co.link.FinPay.services.TransactionService;

import java.util.List;

@RestController
@RequestMapping("api/v1/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping
    public ResponseEntity<?> createTransaction(@RequestBody Transaction transaction) {
        return ResponseEntity.ok().body(transactionService.save(transaction));
    }

    @GetMapping
    public ResponseEntity<?> getTransactions(Pageable pageable) {
        return ResponseEntity.ok().body(transactionService.getAll(pageable));
    }

   @GetMapping("/{transactionId}")
    public ResponseEntity<?> getTransaction(Long transactionId) {
        return ResponseEntity.ok().body(transactionService.get(transactionId));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getAccountTransactions(@PathVariable Long userId, Pageable pageable) {
        return ResponseEntity.ok().body(transactionService.getAll(userId,pageable));
    }

    @GetMapping("/balances/{userId}")
    public ResponseEntity<List<Balance>> getUserBalances(@PathVariable Long userId) {
        List<Balance> accountBalances = transactionService.getUserBalances(userId);
        return ResponseEntity.ok(accountBalances);
    }
}
