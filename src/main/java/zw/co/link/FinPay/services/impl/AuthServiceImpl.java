package zw.co.link.FinPay.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import zw.co.link.FinPay.configs.JwtTokenProvider;
import zw.co.link.FinPay.domain.Role;
import zw.co.link.FinPay.domain.User;
import zw.co.link.FinPay.domain.dtos.*;
import zw.co.link.FinPay.domain.repositories.UserRepository;
import zw.co.link.FinPay.exceptions.UserNotFoundException;
import zw.co.link.FinPay.services.EmailService;
import zw.co.link.FinPay.services.AuthService;


import java.time.Instant;
import java.util.*;

@Service
@Slf4j
public class AuthServiceImpl implements AuthService, UserDetailsService {
    private final UserRepository userRepository;

    private final JwtTokenProvider jwtTokenProvider;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    public AuthServiceImpl(UserRepository userRepository, JwtTokenProvider jwtTokenProvider, EmailService emailService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        Optional<User> optionalUser = userRepository.findByEmail(loginRequest.getUsername());
        if (optionalUser.isEmpty()) {
            throw new RuntimeException("Invalid Username or Password");
        }
        boolean matches = passwordEncoder.matches(loginRequest.getPassword(), optionalUser.get().getPassword());
        if (!matches) {
            throw new RuntimeException("Invalid Username or Password");
        }
        String token = jwtTokenProvider.generateAccessTokenByEmail(loginRequest.getUsername());
        String refreshToken = jwtTokenProvider.generateRefreshToken(loginRequest.getUsername());

        return new LoginResponse(token, refreshToken);
    }

    @Override
    public InfoResponse register(RegistrationRequest registrationRequest) {
        Optional<User> optionalUser = userRepository.findByEmail(registrationRequest.email());
        if (optionalUser.isPresent()) {
            throw new RuntimeException("User already exists");
        }
        User user = new User();
        user.setFirstName(registrationRequest.firstName());
        user.setLastName(registrationRequest.lastName());
        user.setEmail(registrationRequest.email());
        user.setPhoneNumber(registrationRequest.phoneNumber());
        user.setProvider(AuthProvider.LOCAL);
        user.setPassword(passwordEncoder.encode(registrationRequest.password()));
        user.setRoles(Set.of(Role.builder().id(2L).build()));
        userRepository.save(user);

        return new InfoResponse("Registration Successful. Please Login");
    }

    @Override
    public UserDto getUserById(Long id) {
        UserDto userDto = null;
        User user = userRepository.findById(id).orElseThrow();
        BeanUtils.copyProperties(user, userDto);
        return userDto;
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public User createOauth2User(OAuth2User oAuth2User) {
        User user = new User();
        user.setFirstName(oAuth2User.getAttribute("given_name"));
        user.setLastName(oAuth2User.getAttribute("family_name"));
        user.setEmail(oAuth2User.getAttribute("email"));
        user.setProvider(AuthProvider.GOOGLE);
        user.setImageUrl(oAuth2User.getAttribute("picture"));
        user.setEmailVerified(oAuth2User.getAttribute("email_verified"));
        user.setRoles(Set.of(Role.builder().id(2L).build()));
        return userRepository.save(user);
    }

    @Override
    public InfoResponse requestLoginWithEmailLink(EmailLinkLoginRequest emailLinkLoginRequest) {
        User user = userRepository.findByEmail(emailLinkLoginRequest.email())
                .orElseThrow(() -> new UserNotFoundException(
                        String.format("No account found with email: %s", emailLinkLoginRequest.email())));
        user.setEmailLoginToken(UUID.randomUUID());
        user.setEmailLoginTokenExpiresAt(Instant.now().plusSeconds(600));
        userRepository.save(user);
        return new InfoResponse(emailService.loginWithEmail(emailLinkLoginRequest.email(), user.getEmailLoginToken()));
    }

    @Override
    public LoginResponse loginWithEmailLink(String email, UUID emailLoginToken) {
        Optional<User> userOptional = userRepository.findByEmailAndEmailLoginToken(email, emailLoginToken);
        if (userOptional.isEmpty()) {
            throw new RuntimeException("Invalid login link");
        }


        User user = userOptional.get();

        if (Instant.now().isAfter(user.getEmailLoginTokenExpiresAt())) {
            throw new RuntimeException("login link Expired");
        }
        user.setEmailLoginToken(null);
        userRepository.save(user);
        String token = jwtTokenProvider.generateAccessTokenByEmail(email);
        String refreshToken = jwtTokenProvider.generateRefreshToken(email);
        return new LoginResponse(token, refreshToken);
    }

    @Override
    public UserDto getUserProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String currentUserName = authentication.getName();

            User user = userRepository.findByEmail(currentUserName)
                    .orElseThrow(() -> new UserNotFoundException("User not found"));

            // Map User entity to UserDto
            return new UserDto(
                    user.getId(),
                    user.getFirstName(),
                    user.getMiddleName(),
                    user.getLastName(),
                    user.getGender(),
                    user.getEmail(),
                    user.getEmailVerified(),
                    user.getPhoneNumber(),
                    user.getRoles(),
                    user.getImageUrl(),
                    user.getProvider() != null ? user.getProvider().name() : null,
                    user.getEmailLoginToken(),
                    user.getEmailLoginTokenExpiresAt()
            );
        }

        throw new UserNotFoundException("User not found");
    }

    @Override
    public Optional<User> findUserById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public User saveUser(User adminUser) {
        adminUser.setPassword(passwordEncoder.encode(adminUser.getPassword()));
        return userRepository.save(adminUser);
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username).orElseThrow();
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        user.getRoles().forEach(role -> authorities.add(new SimpleGrantedAuthority(role.getName())));
        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), authorities);
    }

    public String refreshAccessToken(String refreshToken) {
        if (jwtTokenProvider.validateToken(refreshToken) && !jwtTokenProvider.isTokenExpired(refreshToken)) {
            String email = jwtTokenProvider.getEmailFromToken(refreshToken);
            return jwtTokenProvider.generateAccessTokenByEmail(email);
        } else {
            throw new IllegalArgumentException("Invalid or expired refresh token");
        }
    }
}
