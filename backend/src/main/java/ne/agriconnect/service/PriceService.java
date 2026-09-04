package ne.agriconnect.service;

import lombok.RequiredArgsConstructor;
import ne.agriconnect.dto.MarketPriceDto;
import ne.agriconnect.repository.MarketPriceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PriceService {

    private final MarketPriceRepository priceRepository;
    private final DtoMapper mapper;

    @Transactional(readOnly = true)
    public List<MarketPriceDto> list(Long productId, String region) {
        ne.agriconnect.domain.Region regionEnum = parseRegion(region);
        if (productId != null && regionEnum != null) {
            return priceRepository.findTop3ByProductIdAndRegionOrderByDateDesc(productId, regionEnum)
                    .stream().map(mapper::priceToDto).toList();
        }
        if (productId != null) {
            return priceRepository.findByProductIdOrderByDateDesc(productId)
                    .stream().map(mapper::priceToDto).toList();
        }
        if (regionEnum != null) {
            return priceRepository.findByRegionOrderByDateDesc(regionEnum)
                    .stream().map(mapper::priceToDto).toList();
        }
        return priceRepository.findTop10ByOrderByDateDesc().stream().map(mapper::priceToDto).toList();
    }

    private ne.agriconnect.domain.Region parseRegion(String region) {
        if (region == null || region.isBlank()) {
            return null;
        }
        try {
            return ne.agriconnect.domain.Region.valueOf(region.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
