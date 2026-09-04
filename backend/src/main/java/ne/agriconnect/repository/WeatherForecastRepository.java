package ne.agriconnect.repository;

import ne.agriconnect.domain.Region;
import ne.agriconnect.domain.WeatherForecast;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface WeatherForecastRepository extends JpaRepository<WeatherForecast, Long> {
    List<WeatherForecast> findByRegionAndDateGreaterThanEqualOrderByDateAsc(Region region, LocalDate date);
    List<WeatherForecast> findByDateGreaterThanEqualOrderByDateAsc(LocalDate date);
}
