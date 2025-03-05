package zw.co.link.FinPay.domain.dtos;

import lombok.*;

import java.util.List;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class EmailDetails {

    private List<String> recipients;
    private String msgBody;
    private String subject;
    private String attachment;
}

