package ne.agriconnect.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ne.agriconnect.domain.OfferStatus;
import ne.agriconnect.domain.User;
import ne.agriconnect.dto.OfferDto;
import ne.agriconnect.dto.OfferRequest;
import ne.agriconnect.service.OfferService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/offers")
@RequiredArgsConstructor
public class OfferController {

    private final OfferService offerService;

    @GetMapping
    public List<OfferDto> list(@RequestParam(required = false) Long productId,
                               @RequestParam(required = false) String region,
                               @RequestParam(required = false) Double maxPrice,
                               @RequestParam(required = false) String q) {
        return offerService.list(productId, region, maxPrice, q);
    }

    @GetMapping("/{id}")
    public OfferDto get(@PathVariable Long id) {
        return offerService.get(id);
    }

    @GetMapping("/mine/list")
    @PreAuthorize("hasAnyRole('FARMER','ADMIN')")
    public List<OfferDto> mine(@AuthenticationPrincipal User user) {
        return offerService.mine(user.getId());
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('FARMER','ADMIN')")
    public OfferDto create(@Valid @RequestBody OfferRequest request, @AuthenticationPrincipal User user) {
        return offerService.create(request, user.getId());
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('FARMER','ADMIN')")
    public OfferDto changeStatus(@PathVariable Long id, @RequestBody OfferStatus status,
                                 @AuthenticationPrincipal User user) {
        return offerService.changeStatus(id, status, user.getId());
    }
}
