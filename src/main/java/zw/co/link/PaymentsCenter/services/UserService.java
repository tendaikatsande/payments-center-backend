package zw.co.link.PaymentsCenter.services;

import org.springframework.security.oauth2.core.user.OAuth2User;
import zw.co.link.PaymentsCenter.domain.User;
import zw.co.link.PaymentsCenter.domain.dtos.*;


import java.util.Optional;
import java.util.UUID;

public interface UserService {
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
