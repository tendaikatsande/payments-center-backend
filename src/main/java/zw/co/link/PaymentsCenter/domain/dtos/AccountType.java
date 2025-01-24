package zw.co.link.PaymentsCenter.domain.dtos;

// Enum for better type safety
public enum AccountType {
    MAIN(1),
    SECONDARY(2);

    private final int id;

    AccountType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
