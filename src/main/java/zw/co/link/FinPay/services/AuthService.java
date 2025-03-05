package zw.co.link.FinPay.services;

import org.springframework.security.oauth2.core.user.OAuth2User;
import zw.co.link.FinPay.domain.User;
import zw.co.link.FinPay.domain.dtos.*;


import java.util.Optional;
import java.util.UUID;

public interface AuthService {
    LoginResponse login(LoginRequest loginRequest);
    InfoResponse register(RegistrationRequest registrationRequest);
    UserDto getUserById(Long id);
    Optional<User> getUserByEmail(String email);
    User createOauth2User(OAuth2User oAuth2User);
    InfoResponse requestLoginWithEmailLink(EmailLinkLoginRequest emailLinkLoginRequest);
    LoginResponse loginWithEmailLink(String email, UUID emailLoginToken);
    UserDto getUserProfile();
    Optional<User> findUserById(Long aLong);
    User saveUser(User adminUser);
}
