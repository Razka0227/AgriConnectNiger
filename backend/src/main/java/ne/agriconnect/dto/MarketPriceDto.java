package ne.agriconnect.dto;

import ne.agriconnect.domain.Region;

import java.time.LocalDate;

public record MarketPriceDto(
        Long id,
        Long productId,
        String productName,
        Region region,
        String marketName,
        Double pricePerUnit,
        String unit,
        LocalDate date,
        String source
) {
}
