package ne.agriconnect.dto;

import java.time.Instant;

public record ReviewDto(
        Long id,
        Long reviewerId,
        String reviewerName,
        Integer rating,
        String comment,
        Instant createdAt
) {
}
