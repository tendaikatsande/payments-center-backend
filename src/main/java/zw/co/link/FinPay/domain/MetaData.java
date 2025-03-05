package zw.co.link.FinPay.domain;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.*;
import org.apache.commons.lang3.StringUtils;


import java.lang.reflect.Field;
import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MetaData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    private String targetInfo;
    private String rechargeType;
    private BigDecimal minimumRecharge;
    private String maxRecharge;
    private String targetType;
    private String rechargeAmountIncrement;
    private String currency;
    private String network;
    private String rechargeTypeInformation;
    private String stockQuery;
    private String accountQuery;
    private String customSMSTagBundle;
    private String customSMSTagAccountName;
    private String customSMSTagKwh;
    private String customSMSTagMeterNumber;
    private String description;
    private String networkPrefix;

    @JsonAnySetter
    public void setAdditionalProperty(String key, String value) {
        // Convert from UpperCamel to lowerCamel case
        String fieldName = StringUtils.uncapitalize(key);
        try {
            Field field = this.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(this, value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            // Handle unknown properties silently or log if needed
        }
    }
}
