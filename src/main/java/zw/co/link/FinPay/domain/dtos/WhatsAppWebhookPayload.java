package zw.co.link.FinPay.domain.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.boot.Metadata;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WhatsAppWebhookPayload {
    @JsonProperty("object")
    private String object;

    @JsonProperty("entry")
    private List<Entry> entry;

    // Inner classes for nested structure
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Entry {
        @JsonProperty("id")
        private String id;

        @JsonProperty("changes")
        private List<Change> changes;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Change {
        @JsonProperty("value")
        private Value value;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Value {
        @JsonProperty("messaging_product")
        private String messagingProduct;

        @JsonProperty("metadata")
        private Metadata metadata;

        @JsonProperty("contacts")
        private List<Contact> contacts;

        @JsonProperty("messages")
        private List<Message> messages;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Contact {
        @JsonProperty("wa_id")
        private String waId;

        @JsonProperty("profile")
        private Profile profile;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Profile {
        @JsonProperty("name")
        private String name;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Message {
        @JsonProperty("from")
        private String from;

        @JsonProperty("text")
        private Text text;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Text {
        @JsonProperty("body")
        private String body;
    }
}

