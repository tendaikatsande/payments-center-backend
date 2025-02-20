package zw.co.link.FinPay.domain.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WhatsAppMessage {
    @JsonProperty("messaging_product")
    private String messagingProduct;

    @JsonProperty("recipient_type")
    private String recipientType;

    private String to;
    private String type;
    private Text text;
    private Context context;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Text {
        @JsonProperty("preview_url")
        private boolean previewUrl;

        private String body;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Context {
        @JsonProperty("message_id")
        private String messageId;
    }
}
