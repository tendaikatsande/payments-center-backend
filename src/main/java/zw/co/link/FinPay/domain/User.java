package zw.co.link.FinPay.domain;


import jakarta.persistence.*;
import lombok.*;
import zw.co.link.FinPay.domain.dtos.AuthProvider;
import zw.co.link.FinPay.domain.dtos.UserDto;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "s_user")
@Entity
// User Entity
public class User extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;
    private String firstName;
    private String middleName;
    private String lastName;
    private String gender;
    private String email;
    private Boolean emailVerified;
    private String phoneNumber;
    private String password; // Encrypted for security
    @ManyToMany
    private Set<Role> roles;
    private String imageUrl;
    @Enumerated(EnumType.STRING)
    private AuthProvider provider;

    private String providerId;

    private String refreshToken;
    private String resetToken;

    private UUID emailLoginToken;
    private Instant emailLoginTokenExpiresAt;


    public UserDto toDto() {
        return UserDto.builder()
                .id(id)
                .firstName(firstName)
                .middleName(middleName)
                .lastName(lastName)
                .gender(gender)
                .email(email)
                .phoneNumber(phoneNumber)
                .imageUrl(imageUrl)
                .build();
    }
}


