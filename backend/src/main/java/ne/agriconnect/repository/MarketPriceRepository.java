package ne.agriconnect.repository;

import ne.agriconnect.domain.MarketPrice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MarketPriceRepository extends JpaRepository<MarketPrice, Long> {
    List<MarketPrice> findTop3ByProductIdAndRegionOrderByDateDesc(Long productId, ne.agriconnect.domain.Region region);
    List<MarketPrice> findByProductIdOrderByDateDesc(Long productId);
    List<MarketPrice> findByRegionOrderByDateDesc(ne.agriconnect.domain.Region region);
    List<MarketPrice> findTop10ByOrderByDateDesc();
}
