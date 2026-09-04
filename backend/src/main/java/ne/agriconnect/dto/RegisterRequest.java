package ne.agriconnect.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import ne.agriconnect.domain.Region;
import ne.agriconnect.domain.Role;

public record RegisterRequest(
        @NotBlank String name,
        @NotBlank String phone,
        String email,
        @NotBlank String password,
        @NotNull Role role,
        Region region,
        String locality,
        Double latitude,
        Double longitude,
        String organization
) {
}
