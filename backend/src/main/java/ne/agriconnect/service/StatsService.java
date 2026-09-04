package ne.agriconnect.service;

import lombok.RequiredArgsConstructor;
import ne.agriconnect.domain.OrderStatus;
import ne.agriconnect.domain.Role;
import ne.agriconnect.domain.User;
import ne.agriconnect.dto.DashboardStatsDto;
import ne.agriconnect.exception.NotFoundException;
import ne.agriconnect.repository.NotificationRepository;
import ne.agriconnect.repository.OfferRepository;
import ne.agriconnect.repository.OrderRepository;
import ne.agriconnect.repository.ReviewRepository;
import ne.agriconnect.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StatsService {

    private final OfferRepository offerRepository;
    private final OrderRepository orderRepository;
    private final NotificationRepository notificationRepository;
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public DashboardStatsDto dashboard(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Utilisateur introuvable"));

        long activeOffers = offerRepository.countByStatus(ne.agriconnect.domain.OfferStatus.ACTIVE);
        long totalOffers = offerRepository.count();
        long myOffers = offerRepository.findBySellerIdOrderByCreatedAtDesc(userId).size();
        long unread = notificationRepository.countByUserIdAndReadFalse(userId);
        double myRating = reviewRepository.findByRevieweeIdOrderByCreatedAtDesc(userId).stream()
                .mapToInt(r -> r.getRating()).average().orElse(0.0);

        long myOrders;
        long ordersToDeliver;
        if (user.getRole() == Role.FARMER) {
            myOrders = orderRepository.findOrdersForSeller(userId).size();
            ordersToDeliver = orderRepository.findOrdersForSeller(userId).stream()
                    .filter(o -> o.getStatus() == OrderStatus.PACKED
                            || o.getStatus() == OrderStatus.IN_TRANSIT)
                    .count();
        } else if (user.getRole() == Role.TRANSPORTER) {
            myOrders = orderRepository.findByTransporterIdOrderByCreatedAtDesc(userId).size();
            ordersToDeliver = orderRepository.findByTransporterIdOrderByCreatedAtDesc(userId).stream()
                    .filter(o -> o.getStatus() == OrderStatus.IN_TRANSIT)
                    .count();
        } else {
            myOrders = orderRepository.findByBuyerIdOrderByCreatedAtDesc(userId).size();
            ordersToDeliver = 0;
        }

        myRating = Math.round(myRating * 10.0) / 10.0;
        return new DashboardStatsDto(activeOffers, totalOffers, myOffers, myOrders,
                ordersToDeliver, unread, myRating, "FCFA");
    }
}
