package ne.agriconnect.service;

import ne.agriconnect.domain.*;
import ne.agriconnect.dto.OrderItemDto;
import ne.agriconnect.dto.ProductDto;
import ne.agriconnect.dto.UserDto;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DtoMapperTest {

    private final DtoMapper mapper = new DtoMapper();

    @Test
    void productToDto_shouldMapFieldsCorrectly() {
        Product product = Product.builder()
                .id(10L)
                .name("Maïs")
                .localName("Mais local")
                .category(ProductCategory.CEREALES)
                .unit("kg")
                .build();

        ProductDto dto = mapper.productToDto(product);

        assertEquals(10L, dto.id());
        assertEquals("Maïs", dto.name());
        assertEquals("Mais local", dto.localName());
        assertEquals(ProductCategory.CEREALES, dto.category());
        assertEquals("Céréales", dto.categoryLabel());
        assertEquals("kg", dto.unit());
    }

    @Test
    void userToDto_shouldIncludeRatingAndRoleLabel() {
        User user = User.builder()
                .id(20L)
                .name("Alice")
                .phone("+22790000000")
                .email("alice@example.com")
                .role(Role.BUYER)
                .region(Region.NIAMEY)
                .locality("Niamey Centre")
                .latitude(13.5123)
                .longitude(2.1123)
                .organization("Ferme Test")
                .createdAt(Instant.now())
                .build();

        UserDto dto = mapper.userToDto(user, 4.5);

        assertEquals(user.getId(), dto.id());
        assertEquals(user.getName(), dto.name());
        assertEquals(user.getPhone(), dto.phone());
        assertEquals(user.getEmail(), dto.email());
        assertEquals(Role.BUYER, dto.role());
        assertEquals(Role.BUYER.getLabel(), dto.roleLabel());
        assertEquals(user.getRegion(), dto.region());
        assertEquals(user.getLocality(), dto.locality());
        assertEquals(user.getLatitude(), dto.latitude());
        assertEquals(user.getLongitude(), dto.longitude());
        assertEquals(user.getOrganization(), dto.organization());
        assertEquals(4.5, dto.rating());
        assertEquals(user.getCreatedAt(), dto.createdAt());
    }

    @Test
    void orderToDto_shouldMapOrderItemsAndTransporterMetadata() {
        User buyer = User.builder().id(30L).name("Buyer").build();
        User seller = User.builder().id(40L).name("Seller").build();
        Product product = Product.builder().id(50L).name("Sorgho").build();
        Offer offer = Offer.builder()
                .id(60L)
                .seller(seller)
                .product(product)
                .title("Offre Sorgho")
                .unit("kg")
                .pricePerUnit(150.0)
                .build();

        OrderItem item = OrderItem.builder()
                .id(70L)
                .offer(offer)
                .quantity(5.0)
                .unitPrice(150.0)
                .subtotal(750.0)
                .build();

        Order order = Order.builder()
                .id(80L)
                .buyer(buyer)
                .transporter(User.builder().id(90L).name("Transporteur").build())
                .status(OrderStatus.CONFIRMED)
                .totalAmount(750.0)
                .deliveryAddress("Route 1")
                .deliveryLocality("Niamey")
                .deliveryLatitude(13.5)
                .deliveryLongitude(2.1)
                .notes("Livraison rapide")
                .items(List.of(item))
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
        item.setOrder(order);

        var dto = mapper.orderToDto(order);

        assertEquals(order.getId(), dto.id());
        assertEquals(order.getStatus(), dto.status());
        assertEquals(order.getStatus().getLabel(), dto.statusLabel());
        assertEquals(order.getBuyer().getId(), dto.buyerId());
        assertEquals(order.getBuyer().getName(), dto.buyerName());
        assertEquals(order.getTransporter().getId(), dto.transporterId());
        assertEquals(order.getTransporter().getName(), dto.transporterName());
        assertEquals(order.getTotalAmount(), dto.totalAmount());
        assertEquals(order.getDeliveryAddress(), dto.deliveryAddress());
        assertEquals(1, dto.items().size());

        OrderItemDto itemDto = dto.items().get(0);
        assertEquals(item.getId(), itemDto.id());
        assertEquals(offer.getId(), itemDto.offerId());
        assertEquals(product.getName(), itemDto.productName());
        assertEquals(offer.getTitle(), itemDto.offerTitle());
        assertEquals(seller.getId(), itemDto.sellerId());
        assertEquals(seller.getName(), itemDto.sellerName());
        assertEquals(item.getQuantity(), itemDto.quantity());
        assertEquals(offer.getUnit(), itemDto.unit());
        assertEquals(item.getUnitPrice(), itemDto.unitPrice());
        assertEquals(item.getSubtotal(), itemDto.subtotal());
    }
}
