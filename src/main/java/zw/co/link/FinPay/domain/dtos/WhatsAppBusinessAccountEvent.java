package zw.co.link.FinPay.domain.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.hibernate.boot.Metadata;

import java.util.List;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class WhatsAppBusinessAccountEvent {
    @JsonProperty("object")
    private String object;

    @JsonProperty("entry")
    private List<Entry> entry;


    public String getFrom() {
        Entry firstEntry = getFirstEntryOrThrow();
        Change firstChange = getFirstChangeOrThrow(firstEntry);
        Message firstMessage = getFirstMessageOrThrow(firstChange);
        return firstMessage.getFrom();
    }

    public String getFirstMessageBody() {
        Entry firstEntry = getFirstEntryOrThrow();
        Change firstChange = getFirstChangeOrThrow(firstEntry);
        Message firstMessage = getFirstMessageOrThrow(firstChange);
        Text text = firstMessage.getText();
        return text.getBody();
    }

    public String getFirstMessageBodyId() {
        Entry firstEntry = getFirstEntryOrThrow();
        Change firstChange = getFirstChangeOrThrow(firstEntry);
        Message firstMessage = getFirstMessageOrThrow(firstChange);
        return firstMessage.getId();
    }

    private Entry getFirstEntryOrThrow() {
        return getEntry().stream().findFirst().orElseThrow();
    }

    private Change getFirstChangeOrThrow(Entry entry) {
        return entry.getChanges().stream().findFirst().orElseThrow();
    }

    private Message getFirstMessageOrThrow(Change change) {
        Value value = change.getValue();
        if (value != null && value.getMessages() != null) {
            return value.getMessages().stream().findFirst().orElseThrow();
        } else {
            throw new IllegalStateException("Messages or Value is null");
        }
    }


    @Getter
    @Setter
    public static class Entry {
        private String id;
        private List<Change> changes;
    }

    @Getter
    @Setter
    public static class Change {
        private Value value;
        private String field;
    }

    @Getter
    @Setter
    public static class Value {
        @JsonProperty("messaging_product")
        private String messagingProduct;

        private Metadata metadata;
        private List<Contact> contacts;
        private List<Message> messages;
    }

    @Getter
    @Setter
    public static class Metadata {
        @JsonProperty("display_phone_number")
        private String displayPhoneNumber;

        @JsonProperty("phone_number_id")
        private String phoneNumberId;
    }

    @Getter
    @Setter
    public static class Contact {
        private Profile profile;
        @JsonProperty("wa_id")
        private String waId;
    }

    @Getter
    @Setter
    public static class Profile {
        private String name;
    }

    @Getter
    @Setter
    public static class Message {
        private String from;
        private String id;
        private long timestamp;
        private Text text;
        private String type;
    }

    @Getter
    @Setter
    public static class Text {
        private String body;
    }
}

