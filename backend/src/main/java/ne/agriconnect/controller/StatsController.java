package ne.agriconnect.controller;

import lombok.RequiredArgsConstructor;
import ne.agriconnect.domain.User;
import ne.agriconnect.dto.DashboardStatsDto;
import ne.agriconnect.service.StatsService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/stats")
@RequiredArgsConstructor
public class StatsController {

    private final StatsService statsService;

    @GetMapping("/dashboard")
    public DashboardStatsDto dashboard(@AuthenticationPrincipal User user) {
        return statsService.dashboard(user.getId());
    }
}
