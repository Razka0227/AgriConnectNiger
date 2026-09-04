package ne.agriconnect.dto;

import ne.agriconnect.domain.ProductCategory;

public record ProductDto(
        Long id,
        String name,
        String localName,
        ProductCategory category,
        String categoryLabel,
        String unit
) {
}
