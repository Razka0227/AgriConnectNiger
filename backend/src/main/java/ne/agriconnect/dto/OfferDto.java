package ne.agriconnect.dto;

import ne.agriconnect.domain.OfferStatus;
import ne.agriconnect.domain.Region;

import java.time.Instant;

public record OfferDto(
        Long id,
        String title,
        ProductDto product,
        Long sellerId,
        String sellerName,
        String sellerOrganization,
        Double sellerRating,
        Region region,
        String locality,
        Double latitude,
        Double longitude,
        Double quantity,
        String unit,
        Double pricePerUnit,
        Double minOrderQuantity,
        String description,
        String qualityGrade,
        OfferStatus status,
        String statusLabel,
        Instant createdAt
) {
}
