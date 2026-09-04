package ne.agriconnect.repository;

import ne.agriconnect.domain.Offer;
import ne.agriconnect.domain.OfferStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OfferRepository extends JpaRepository<Offer, Long> {
    List<Offer> findByStatusOrderByCreatedAtDesc(OfferStatus status);
    List<Offer> findBySellerIdOrderByCreatedAtDesc(Long sellerId);
    long countByStatus(OfferStatus status);
}
