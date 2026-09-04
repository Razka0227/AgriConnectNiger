package ne.agriconnect.controller;

import lombok.RequiredArgsConstructor;
import ne.agriconnect.dto.TransportRouteDto;
import ne.agriconnect.service.TransportService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/transport")
@RequiredArgsConstructor
public class TransportController {

    private final TransportService transportService;

    @GetMapping
    public List<TransportRouteDto> list(@RequestParam(required = false) String fromRegion,
                                        @RequestParam(required = false) String toRegion) {
        return transportService.list(fromRegion, toRegion);
    }
}
