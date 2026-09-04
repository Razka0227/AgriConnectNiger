package ne.agriconnect.dto;

import ne.agriconnect.domain.Region;

public record TransportRouteDto(
        Long id,
        String name,
        String fromCity,
        Region fromRegion,
        String toCity,
        Region toRegion,
        Double distanceKm,
        Double estimatedHours,
        Double costPerKgCfa,
        String provider
) {
}
