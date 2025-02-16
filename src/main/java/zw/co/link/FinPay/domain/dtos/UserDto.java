package zw.co.link.FinPay.domain.dtos;


import zw.co.link.FinPay.domain.Role;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public record UserDto(
        Long id,
        String firstName,
        String middleName,
        String lastName,
        String gender,
        String email,
        Boolean emailVerified,
        String phoneNumber,
        Set<Role> roles,
        String imageUrl,
        String provider,
        UUID emailLoginToken,
        Instant emailLoginTokenExpiresAt
) {
}

