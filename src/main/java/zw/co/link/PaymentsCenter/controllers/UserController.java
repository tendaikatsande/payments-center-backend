package zw.co.link.PaymentsCenter.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;
import zw.co.link.PaymentsCenter.domain.dtos.*;
import zw.co.link.PaymentsCenter.services.UserService;


import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/users")
@CrossOrigin
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        // TODO : implement login logic from a service and bind it here
        LoginResponse response = userService.login(loginRequest);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile() {
        // TODO : implement login logic from a service and bind it here
        UserDto response = userService.getUserProfile();
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
        InfoResponse response = userService.requestLoginWithEmailLink(emailLinkLoginRequest);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/login-with-email/{email}/{token}")
    public ResponseEntity<?> loginWithEmailLink(@PathVariable String email, @PathVariable UUID token) {
        // TODO : implement login logic from a service and bind it here
        LoginResponse response = userService.loginWithEmailLink(email, token);
        return ResponseEntity.ok(response);
    }


    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegistrationRequest registrationRequest) {
        //TODO : implement registration here from a servcie and bind it here
        InfoResponse infoResponse = userService.register(registrationRequest);
        return ResponseEntity.status(201).body(infoResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable("id") Long id) {
        // TODO : Get user by id
        return ResponseEntity.ok().body(userService.getUserById(id));
    }



}


