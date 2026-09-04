package ne.agriconnect.dto;

import jakarta.validation.constraints.NotNull;
import ne.agriconnect.domain.OrderStatus;

public record OrderStatusRequest(
        @NotNull OrderStatus status
) {
}
