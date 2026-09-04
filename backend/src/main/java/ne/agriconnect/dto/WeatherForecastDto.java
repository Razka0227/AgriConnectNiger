package ne.agriconnect.dto;

import ne.agriconnect.domain.Region;
import ne.agriconnect.domain.WeatherCondition;

import java.time.LocalDate;

public record WeatherForecastDto(
        Long id,
        Region region,
        LocalDate date,
        WeatherCondition condition,
        String conditionLabel,
        Double tempMinC,
        Double tempMaxC,
        Double humidityPct,
        Double rainfallMm,
        String advice
) {
}
