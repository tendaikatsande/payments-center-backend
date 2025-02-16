package zw.co.link.FinPay.configs;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import zw.co.link.FinPay.domain.User;
import zw.co.link.FinPay.services.UserService;


import java.util.Optional;

@Service
@Slf4j
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserService userService;

    public CustomOAuth2UserService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User user = super.loadUser(userRequest);
        try {
            return processOAuth2User(userRequest, user);
        } catch (Exception ex) {
            throw new OAuth2AuthenticationException(ex.getMessage());
        }
    }

    private OAuth2User processOAuth2User(OAuth2UserRequest userRequest, OAuth2User oAuth2User) {
        // Extract email from OAuth2User
        String email = oAuth2User.getAttribute("email");
        // Here you would typically:
        // 1. Check if user exists in your database
        Optional<User> user = userService.getUserByEmail(email);
        // 2. Create new user if they don't exist
        if (user.isEmpty()) {
            userService.createOauth2User(oAuth2User);
        }
        // 3. Update existing user information if needed

        return oAuth2User;
    }
}



