package zw.co.link.FinPay.domain;

import jakarta.persistence.*;
import lombok.*;
import zw.co.link.FinPay.domain.dtos.RechargeOptionDescription;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class HotProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;
    private int productId;
    private String name;
    private int accountTypeId;
    @OneToMany(cascade = CascadeType.ALL)
    private List<RechargeOptionDescription> requiredOptions;
    @OneToOne(cascade = CascadeType.ALL)
    private MetaData metaData;
}



