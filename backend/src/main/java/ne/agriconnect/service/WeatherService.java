package ne.agriconnect.service;

import lombok.RequiredArgsConstructor;
import ne.agriconnect.domain.Region;
import ne.agriconnect.dto.WeatherForecastDto;
import ne.agriconnect.repository.WeatherForecastRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WeatherService {

    private final WeatherForecastRepository weatherRepository;
    private final DtoMapper mapper;

    @Transactional(readOnly = true)
    public List<WeatherForecastDto> forecast(String region) {
        LocalDate today = LocalDate.now();
        if (region != null && !region.isBlank()) {
            try {
                Region r = Region.valueOf(region.toUpperCase());
                return weatherRepository.findByRegionAndDateGreaterThanEqualOrderByDateAsc(r, today)
                        .stream().map(mapper::weatherToDto).toList();
            } catch (IllegalArgumentException ignored) {
            }
        }
        return weatherRepository.findByDateGreaterThanEqualOrderByDateAsc(today)
                .stream().map(mapper::weatherToDto).toList();
    }
}
