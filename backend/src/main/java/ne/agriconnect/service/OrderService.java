package ne.agriconnect.service;

import lombok.RequiredArgsConstructor;
import ne.agriconnect.domain.*;
import ne.agriconnect.dto.OrderDto;
import ne.agriconnect.dto.OrderRequest;
import ne.agriconnect.exception.BadRequestException;
import ne.agriconnect.exception.NotFoundException;
import ne.agriconnect.repository.OfferRepository;
import ne.agriconnect.repository.OrderRepository;
import ne.agriconnect.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class OrderService {

    private static final Map<OrderStatus, Set<OrderStatus>> ALLOWED_TRANSITIONS = Map.of(
            OrderStatus.PENDING, Set.of(OrderStatus.CONFIRMED, OrderStatus.CANCELLED),
            OrderStatus.CONFIRMED, Set.of(OrderStatus.PACKED, OrderStatus.CANCELLED),
            OrderStatus.PACKED, Set.of(OrderStatus.IN_TRANSIT, OrderStatus.CANCELLED),
            OrderStatus.IN_TRANSIT, Set.of(OrderStatus.DELIVERED),
            OrderStatus.DELIVERED, Set.of(),
            OrderStatus.CANCELLED, Set.of());

    private final OrderRepository orderRepository;
    private final OfferRepository offerRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final DtoMapper mapper;

    @Transactional
    public OrderDto create(OrderRequest req, Long buyerId) {
        User buyer = userRepository.findById(buyerId)
                .orElseThrow(() -> new NotFoundException("Utilisateur introuvable"));
        Order order = Order.builder()
                .buyer(buyer)
                .status(OrderStatus.PENDING)
                .deliveryAddress(req.deliveryAddress())
                .deliveryLocality(req.deliveryLocality())
                .deliveryLatitude(req.deliveryLatitude())
                .deliveryLongitude(req.deliveryLongitude())
                .notes(req.notes())
                .build();

        double total = 0.0;
        for (var itemReq : req.items()) {
            Offer offer = offerRepository.findById(itemReq.offerId())
                    .orElseThrow(() -> new NotFoundException("Offre introuvable : " + itemReq.offerId()));
            if (offer.getStatus() != OfferStatus.ACTIVE) {
                throw new BadRequestException("L'offre \"" + offer.getTitle() + "\" n'est plus disponible");
            }
            if (offer.getSeller().getId().equals(buyerId)) {
                throw new BadRequestException("Vous ne pouvez pas commander votre propre offre");
            }
            if (offer.getMinOrderQuantity() != null && itemReq.quantity() < offer.getMinOrderQuantity()) {
                throw new BadRequestException("Quantité minimale de commande pour \"" + offer.getTitle() +
                        "\" : " + offer.getMinOrderQuantity() + " " + offer.getUnit());
            }
            OrderItem item = OrderItem.builder()
                    .order(order)
                    .offer(offer)
                    .quantity(itemReq.quantity())
                    .unitPrice(offer.getPricePerUnit())
                    .subtotal(Math.round(itemReq.quantity() * offer.getPricePerUnit() * 100.0) / 100.0)
                    .build();
            order.getItems().add(item);
            total += item.getSubtotal();
            offer.setStatus(OfferStatus.RESERVED);
            offerRepository.save(offer);
        }
        order.setTotalAmount(Math.round(total * 100.0) / 100.0);
        orderRepository.save(order);

        for (var item : order.getItems()) {
            User seller = item.getOffer().getSeller();
            notificationService.notify(seller.getId(), "Nouvelle commande #" + order.getId(),
                    item.getOffer().getTitle() + " : " + item.getQuantity() + " " + item.getOffer().getUnit()
                            + " demandé par " + buyer.getName() + " (" + buyer.getPhone() + ").",
                    NotificationType.ORDER, NotificationChannel.SMS);
        }
        notificationService.notify(buyerId, "Commande #" + order.getId() + " enregistrée",
                "Votre commande de " + order.getItems().size() + " article(s) pour " + order.getTotalAmount()
                        + " FCFA a été envoyée aux vendeurs.",
                NotificationType.ORDER, NotificationChannel.IN_APP);
        return mapper.orderToDto(order);
    }

    @Transactional(readOnly = true)
    public List<OrderDto> forBuyer(Long buyerId) {
        return orderRepository.findByBuyerIdOrderByCreatedAtDesc(buyerId).stream()
                .map(mapper::orderToDto).toList();
    }

    @Transactional(readOnly = true)
    public List<OrderDto> forSeller(Long sellerId) {
        return orderRepository.findOrdersForSeller(sellerId).stream()
                .map(mapper::orderToDto).toList();
    }

    @Transactional(readOnly = true)
    public List<OrderDto> forTransporter(Long transporterId) {
        return orderRepository.findByTransporterIdOrderByCreatedAtDesc(transporterId).stream()
                .map(mapper::orderToDto).toList();
    }

    @Transactional
    public OrderDto transition(Long orderId, OrderStatus target, Long userId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Commande introuvable"));
        Set<OrderStatus> allowed = ALLOWED_TRANSITIONS.get(order.getStatus());
        if (allowed == null || !allowed.contains(target)) {
            throw new BadRequestException("Transition de statut non autorisée : "
                    + order.getStatus().name() + " -> " + target.name());
        }
        requireRoleForTransition(order, target, userId);

        order.setStatus(target);

        if (target == OrderStatus.CONFIRMED && order.getTransporter() == null) {
            userRepository.findAll().stream()
                    .filter(u -> u.getRole() == Role.TRANSPORTER)
                    .findFirst()
                    .ifPresent(transporter -> order.setTransporter(transporter));
        }

        if (target == OrderStatus.DELIVERED) {
            order.getItems().forEach(item -> {
                item.getOffer().setStatus(OfferStatus.CLOSED);
                offerRepository.save(item.getOffer());
            });
        }
        if (target == OrderStatus.CANCELLED) {
            order.getItems().forEach(item -> {
                if (item.getOffer().getStatus() == OfferStatus.RESERVED) {
                    item.getOffer().setStatus(OfferStatus.ACTIVE);
                    offerRepository.save(item.getOffer());
                }
            });
        }
        orderRepository.save(order);

        String buyerMsg = "Votre commande #" + order.getId() + " est maintenant : "
                + target.getLabel() + ".";
        String sellerMsg = "La commande #" + order.getId() + " (" + order.getTotalAmount()
                + " FCFA) est maintenant : " + target.getLabel() + ".";
        notificationService.notify(order.getBuyer().getId(), "Commande #" + order.getId(),
                buyerMsg, NotificationType.ORDER, NotificationChannel.SMS);
        order.getItems().forEach(item -> notificationService.notify(item.getOffer().getSeller().getId(),
                "Commande #" + order.getId(), sellerMsg, NotificationType.ORDER,
                NotificationChannel.IN_APP));
        if (order.getTransporter() != null) {
            notificationService.notify(order.getTransporter().getId(), "Livraison commande #" + order.getId(),
                    "La commande #" + order.getId() + " est : " + target.getLabel() + ".",
                    NotificationType.ORDER, NotificationChannel.IN_APP);
        }
        return mapper.orderToDto(order);
    }

    private void requireRoleForTransition(Order order, OrderStatus target, Long userId) {
        boolean isAdmin = userRepository.findById(userId)
                .map(u -> u.getRole() == Role.ADMIN).orElse(false);
        if (isAdmin) {
            return;
        }
        boolean isBuyer = order.getBuyer().getId().equals(userId);
        boolean isSeller = order.getItems().stream()
                .anyMatch(i -> i.getOffer().getSeller().getId().equals(userId));
        boolean isTransporter = order.getTransporter() != null
                && order.getTransporter().getId().equals(userId);

        switch (target) {
            case CONFIRMED -> {
                if (!isBuyer) {
                    throw new BadRequestException("Seul l'acheteur peut confirmer la commande");
                }
            }
            case PACKED -> {
                if (!isSeller) {
                    throw new BadRequestException("Seul le vendeur peut marquer la commande comme préparée");
                }
            }
            case IN_TRANSIT -> {
                if (!isTransporter && !isSeller) {
                    throw new BadRequestException("Réservé au transporteur ou au vendeur");
                }
            }
            case DELIVERED -> {
                if (!isTransporter && !isBuyer) {
                    throw new BadRequestException("Réservé au transporteur ou à l'acheteur");
                }
            }
            case CANCELLED -> {
                if (!isBuyer && !isSeller) {
                    throw new BadRequestException("Seul l'acheteur ou le vendeur peut annuler");
                }
            }
            default -> {
            }
        }
    }
}
