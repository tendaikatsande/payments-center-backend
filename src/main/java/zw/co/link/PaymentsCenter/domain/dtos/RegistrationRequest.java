package zw.co.link.PaymentsCenter.domain.dtos;

public record RegistrationRequest(
        String email,
        String password,
        String firstName,
        String lastName,
        String phoneNumber
) {
}





