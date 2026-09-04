package ne.agriconnect.service;

import lombok.RequiredArgsConstructor;
import ne.agriconnect.domain.NotificationChannel;
import ne.agriconnect.domain.NotificationType;
import ne.agriconnect.domain.Review;
import ne.agriconnect.domain.User;
import ne.agriconnect.dto.ReviewDto;
import ne.agriconnect.dto.ReviewRequest;
import ne.agriconnect.exception.BadRequestException;
import ne.agriconnect.exception.NotFoundException;
import ne.agriconnect.repository.ReviewRepository;
import ne.agriconnect.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final DtoMapper mapper;

    @Transactional
    public ReviewDto create(Long revieweeId, ReviewRequest req, Long reviewerId) {
        if (revieweeId.equals(reviewerId)) {
            throw new BadRequestException("Vous ne pouvez pas vous noter vous-même");
        }
        User reviewer = userRepository.findById(reviewerId)
                .orElseThrow(() -> new NotFoundException("Utilisateur introuvable"));
        User reviewee = userRepository.findById(revieweeId)
                .orElseThrow(() -> new NotFoundException("Utilisateur introuvable"));
        Review review = Review.builder()
                .reviewer(reviewer)
                .reviewee(reviewee)
                .rating(req.rating())
                .comment(req.comment())
                .build();
        reviewRepository.save(review);
        notificationService.notify(revieweeId, "Nouvelle évaluation",
                reviewer.getName() + " vous a attribué " + req.rating() + "/5.",
                NotificationType.INFO, NotificationChannel.IN_APP);
        return mapper.reviewToDto(review);
    }

    @Transactional(readOnly = true)
    public List<ReviewDto> forUser(Long revieweeId) {
        return reviewRepository.findByRevieweeIdOrderByCreatedAtDesc(revieweeId)
                .stream().map(mapper::reviewToDto).toList();
    }
}
