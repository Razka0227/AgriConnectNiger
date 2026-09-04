package ne.agriconnect.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ne.agriconnect.domain.User;
import ne.agriconnect.dto.AuthResponse;
import ne.agriconnect.dto.CurrentUserDto;
import ne.agriconnect.dto.LoginRequest;
import ne.agriconnect.dto.RegisterRequest;
import ne.agriconnect.dto.UserDto;
import ne.agriconnect.service.AuthService;
import ne.agriconnect.service.DtoMapper;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final DtoMapper mapper;

    @PostMapping("/register")
    public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @GetMapping("/me")
    public CurrentUserDto me(@AuthenticationPrincipal User user) {
        return mapper.currentUserToDto(user);
    }

    @GetMapping("/user/{id}")
    public UserDto user(@PathVariable Long id) {
        return authService.userDto(id);
    }
}
