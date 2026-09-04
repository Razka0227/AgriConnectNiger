package ne.agriconnect.service;

import ne.agriconnect.domain.*;
import ne.agriconnect.dto.*;
import org.springframework.stereotype.Component;

@Component
public class DtoMapper {

    public ProductDto productToDto(Product p) {
        return new ProductDto(p.getId(), p.getName(), p.getLocalName(), p.getCategory(),
                p.getCategory().getLabel(), p.getUnit());
    }

    public UserDto userToDto(User u, Double rating) {
        return new UserDto(u.getId(), u.getName(), u.getPhone(), u.getEmail(), u.getRole(),
                u.getRole().getLabel(), u.getRegion(), u.getLocality(), u.getLatitude(),
                u.getLongitude(), u.getOrganization(), rating, u.getCreatedAt());
    }

    public CurrentUserDto currentUserToDto(User u) {
        return new CurrentUserDto(u.getId(), u.getName(), u.getPhone(), u.getEmail(), u.getRole(),
                u.getRole().getLabel(), u.getRegion(), u.getLocality(), u.getLatitude(),
                u.getLongitude(), u.getOrganization(), u.getCreatedAt());
    }

    public OfferDto offerToDto(Offer o, ProductDto product, Double sellerRating) {
        return new OfferDto(o.getId(), o.getTitle(), product, o.getSeller().getId(),
                o.getSeller().getName(), o.getSeller().getOrganization(), sellerRating,
                o.getRegion(), o.getLocality(), o.getLatitude(), o.getLongitude(),
                o.getQuantity(), o.getUnit(), o.getPricePerUnit(), o.getMinOrderQuantity(),
                o.getDescription(), o.getQualityGrade(), o.getStatus(), o.getStatus().getLabel(),
                o.getCreatedAt());
    }

    public OrderDto orderToDto(Order o) {
        var items = o.getItems().stream()
                .map(i -> new OrderItemDto(i.getId(), i.getOffer().getId(),
                        i.getOffer().getProduct().getName(), i.getOffer().getTitle(),
                        i.getOffer().getSeller().getId(), i.getOffer().getSeller().getName(),
                        i.getQuantity(), i.getOffer().getUnit(), i.getUnitPrice(), i.getSubtotal()))
                .toList();
        return new OrderDto(o.getId(), o.getStatus(), o.getStatus().getLabel(),
                o.getBuyer().getId(), o.getBuyer().getName(),
                o.getTransporter() != null ? o.getTransporter().getId() : null,
                o.getTransporter() != null ? o.getTransporter().getName() : null,
                o.getTotalAmount(), o.getDeliveryAddress(), o.getDeliveryLocality(),
                o.getDeliveryLatitude(), o.getDeliveryLongitude(), o.getNotes(),
                items, o.getCreatedAt(), o.getUpdatedAt());
    }

    public MarketPriceDto priceToDto(MarketPrice p) {
        return new MarketPriceDto(p.getId(), p.getProduct().getId(), p.getProduct().getName(),
                p.getRegion(), p.getMarketName(), p.getPricePerUnit(), p.getUnit(), p.getDate(),
                p.getSource());
    }

    public WeatherForecastDto weatherToDto(WeatherForecast w) {
        return new WeatherForecastDto(w.getId(), w.getRegion(), w.getDate(), w.getCondition(),
                w.getCondition().getLabel(), w.getTempMinC(), w.getTempMaxC(), w.getHumidityPct(),
                w.getRainfallMm(), w.getAdvice());
    }

    public TransportRouteDto routeToDto(TransportRoute r) {
        return new TransportRouteDto(r.getId(), r.getName(), r.getFromCity(), r.getFromRegion(),
                r.getToCity(), r.getToRegion(), r.getDistanceKm(), r.getEstimatedHours(),
                r.getCostPerKgCfa(), r.getProvider());
    }

    public NotificationDto notificationToDto(Notification n) {
        return new NotificationDto(n.getId(), n.getTitle(), n.getMessage(), n.getType(),
                n.getChannel(), n.isRead(), n.getCreatedAt());
    }

    public ReviewDto reviewToDto(Review r) {
        return new ReviewDto(r.getId(), r.getReviewer().getId(), r.getReviewer().getName(),
                r.getRating(), r.getComment(), r.getCreatedAt());
    }
}
