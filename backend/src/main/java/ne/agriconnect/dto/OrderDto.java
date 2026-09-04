package ne.agriconnect.dto;

import ne.agriconnect.domain.OrderStatus;

import java.time.Instant;
import java.util.List;

public record OrderDto(
        Long id,
        OrderStatus status,
        String statusLabel,
        Long buyerId,
        String buyerName,
        Long transporterId,
        String transporterName,
        Double totalAmount,
        String deliveryAddress,
        String deliveryLocality,
        Double deliveryLatitude,
        Double deliveryLongitude,
        String notes,
        List<OrderItemDto> items,
        Instant createdAt,
        Instant updatedAt
) {
}
