package ne.agriconnect.dto;

public record AuthResponse(
        String token,
        UserDto user
) {
}
