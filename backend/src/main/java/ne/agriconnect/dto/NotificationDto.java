package ne.agriconnect.dto;

import ne.agriconnect.domain.NotificationChannel;
import ne.agriconnect.domain.NotificationType;

import java.time.Instant;

public record NotificationDto(
        Long id,
        String title,
        String message,
        NotificationType type,
        NotificationChannel channel,
        boolean read,
        Instant createdAt
) {
}
