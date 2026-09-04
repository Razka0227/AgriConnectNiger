package ne.agriconnect.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record OrderItemRequest(
        @NotNull Long offerId,
        @NotNull @Positive Double quantity
) {
}
