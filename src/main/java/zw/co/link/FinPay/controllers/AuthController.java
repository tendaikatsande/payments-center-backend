package zw.co.link.FinPay.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;
import zw.co.link.FinPay.domain.dtos.*;
import zw.co.link.FinPay.services.AuthService;


import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        LoginResponse response = authService.login(loginRequest);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile() {
        UserDto response = authService.getUserProfile();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public Map<String, Object> getUserInfo(@AuthenticationPrincipal OAuth2User oAuth2User) {
        // Return the user's attributes (e.g., email, name, etc.)
        return oAuth2User.getAttributes();
    }

    @PostMapping("/login-with-email")
    public ResponseEntity<?> getEmailLoginLink(@RequestBody EmailLinkLoginRequest emailLinkLoginRequest) {
        // TODO : implement login logic from a service and bind it here
        InfoResponse response = authService.requestLoginWithEmailLink(emailLinkLoginRequest);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/login-with-email/{email}/{token}")
    public ResponseEntity<?> loginWithEmailLink(@PathVariable String email, @PathVariable UUID token) {
        // TODO : implement login logic from a service and bind it here
        LoginResponse response = authService.loginWithEmailLink(email, token);
        return ResponseEntity.ok(response);
    }


    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegistrationRequest registrationRequest) {
        //TODO : implement registration here from a servcie and bind it here
        InfoResponse infoResponse = authService.register(registrationRequest);
        return ResponseEntity.status(201).body(infoResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable("id") Long id) {
        // TODO : Get user by id
        return ResponseEntity.ok().body(authService.getUserById(id));
    }



}


