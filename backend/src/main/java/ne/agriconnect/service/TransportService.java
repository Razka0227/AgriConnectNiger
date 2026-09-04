package ne.agriconnect.service;

import lombok.RequiredArgsConstructor;
import ne.agriconnect.domain.Region;
import ne.agriconnect.dto.TransportRouteDto;
import ne.agriconnect.repository.TransportRouteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TransportService {

    private final TransportRouteRepository routeRepository;
    private final DtoMapper mapper;

    @Transactional(readOnly = true)
    public List<TransportRouteDto> list(String fromRegion, String toRegion) {
        Region from = parse(fromRegion);
        Region to = parse(toRegion);
        if (from != null && to != null) {
            return routeRepository.findByFromRegionAndToRegion(from, to)
                    .stream().map(mapper::routeToDto).toList();
        }
        if (from != null) {
            return routeRepository.findByFromRegion(from)
                    .stream().map(mapper::routeToDto).toList();
        }
        return routeRepository.findAll().stream().map(mapper::routeToDto).toList();
    }

    private Region parse(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return Region.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
