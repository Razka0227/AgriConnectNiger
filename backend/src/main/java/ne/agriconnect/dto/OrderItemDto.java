package ne.agriconnect.dto;

import ne.agriconnect.domain.OrderStatus;

public record OrderItemDto(
        Long id,
        Long offerId,
        String productName,
        String offerTitle,
        Long sellerId,
        String sellerName,
        Double quantity,
        String unit,
        Double unitPrice,
        Double subtotal
) {
}
