package ne.agriconnect.dto;

import ne.agriconnect.domain.Region;
import ne.agriconnect.domain.Role;

import java.time.Instant;

public record CurrentUserDto(
        Long id,
        String name,
        String phone,
        String email,
        Role role,
        String roleLabel,
        Region region,
        String locality,
        Double latitude,
        Double longitude,
        String organization,
        Instant createdAt
) {
}
