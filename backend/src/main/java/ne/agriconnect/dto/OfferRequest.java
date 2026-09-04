package ne.agriconnect.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import ne.agriconnect.domain.Region;

public record OfferRequest(
        @NotBlank String title,
        @NotNull Long productId,
        Double quantity,
        @NotBlank String unit,
        @NotNull Double pricePerUnit,
        Double minOrderQuantity,
        Region region,
        String locality,
        Double latitude,
        Double longitude,
        String description,
        String qualityGrade
) {
}
