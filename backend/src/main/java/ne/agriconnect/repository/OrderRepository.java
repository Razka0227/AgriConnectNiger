package ne.agriconnect.repository;

import ne.agriconnect.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByBuyerIdOrderByCreatedAtDesc(Long buyerId);
    List<Order> findByTransporterIdOrderByCreatedAtDesc(Long transporterId);

    @Query("select distinct o from Order o join o.items i where i.offer.seller.id = :sellerId order by o.createdAt desc")
    List<Order> findOrdersForSeller(@Param("sellerId") Long sellerId);
}
