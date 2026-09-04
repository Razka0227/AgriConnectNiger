package ne.agriconnect.controller;

import lombok.RequiredArgsConstructor;
import ne.agriconnect.dto.MarketPriceDto;
import ne.agriconnect.service.PriceService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/prices")
@RequiredArgsConstructor
public class PriceController {

    private final PriceService priceService;

    @GetMapping
    public List<MarketPriceDto> list(@RequestParam(required = false) Long productId,
                                     @RequestParam(required = false) String region) {
        return priceService.list(productId, region);
    }
}
