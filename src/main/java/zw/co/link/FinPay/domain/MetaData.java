package zw.co.link.FinPay.domain;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class MetaData{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    private String TargetInfo;
    private String RechargeType;
    private String MinimumRecharge;
    private String MaxRecharge;
    private String TargetType;
    private String RechargeAmountIncrement;
    private String Currency;
    private String Network;
    private String RechargeTypeInformation;

}
