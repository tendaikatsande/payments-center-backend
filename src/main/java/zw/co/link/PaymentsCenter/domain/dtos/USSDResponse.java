package zw.co.link.PaymentsCenter.domain.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class USSDResponse {
    private String message;
    private USSDResponseType responseType;
}
