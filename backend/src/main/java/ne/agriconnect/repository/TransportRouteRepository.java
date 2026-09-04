package ne.agriconnect.repository;

import ne.agriconnect.domain.Region;
import ne.agriconnect.domain.TransportRoute;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransportRouteRepository extends JpaRepository<TransportRoute, Long> {
    List<TransportRoute> findByFromRegionAndToRegion(Region from, Region to);
    List<TransportRoute> findByFromRegion(Region from);
}
