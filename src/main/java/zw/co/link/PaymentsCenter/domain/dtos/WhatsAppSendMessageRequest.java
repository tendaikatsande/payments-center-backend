package zw.co.link.PaymentsCenter.domain.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WhatsAppSendMessageRequest {
    @JsonProperty("messaging_product")
    private String messagingProduct = "whatsapp";

    @JsonProperty("recipient_type")
    private String recipientType = "individual";

    @JsonProperty("to")
    private String to;

    @JsonProperty("type")
    private String type = "text";

    @JsonProperty("text")
    private TextContent text;

    public WhatsAppSendMessageRequest(String phoneNumberId, String to, String messageBody) {
        this.to = to;
        this.text = new TextContent(messageBody);
    }

    @Data
    public static class TextContent {
        @JsonProperty("body")
        private String body;

        public TextContent(String body) {
            this.body = body;
        }
    }
}
