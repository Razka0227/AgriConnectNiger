package ne.agriconnect.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ne.agriconnect.domain.User;
import ne.agriconnect.dto.ReviewDto;
import ne.agriconnect.dto.ReviewRequest;
import ne.agriconnect.service.ReviewService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping("/{revieweeId}")
    public ReviewDto create(@PathVariable Long revieweeId, @Valid @RequestBody ReviewRequest request,
                            @AuthenticationPrincipal User user) {
        return reviewService.create(revieweeId, request, user.getId());
    }

    @GetMapping("/{revieweeId}")
    public List<ReviewDto> forUser(@PathVariable Long revieweeId) {
        return reviewService.forUser(revieweeId);
    }
}
