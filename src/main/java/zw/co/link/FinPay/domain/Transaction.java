package zw.co.link.FinPay.domain;

import jakarta.persistence.*;
import lombok.*;
import zw.co.link.FinPay.domain.dtos.TransactionType;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Transaction extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String currency;

    private BigDecimal amount;
    private BigDecimal runningBalance;

    @Enumerated(EnumType.STRING)
    private TransactionType type; // DEBIT or CREDIT

    @Enumerated(EnumType.STRING)
    private TransactionStatus status;

}


