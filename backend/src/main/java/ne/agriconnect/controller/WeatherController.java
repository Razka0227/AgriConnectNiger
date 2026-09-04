package ne.agriconnect.controller;

import lombok.RequiredArgsConstructor;
import ne.agriconnect.dto.WeatherForecastDto;
import ne.agriconnect.service.WeatherService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/weather")
@RequiredArgsConstructor
public class WeatherController {

    private final WeatherService weatherService;

    @GetMapping
    public List<WeatherForecastDto> forecast(@RequestParam(required = false) String region) {
        return weatherService.forecast(region);
    }
}
