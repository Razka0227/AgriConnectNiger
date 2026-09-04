package ne.agriconnect.service;

import lombok.RequiredArgsConstructor;
import ne.agriconnect.config.JwtService;
import ne.agriconnect.domain.NotificationChannel;
import ne.agriconnect.domain.NotificationType;
import ne.agriconnect.domain.User;
import ne.agriconnect.dto.AuthResponse;
import ne.agriconnect.dto.LoginRequest;
import ne.agriconnect.dto.RegisterRequest;
import ne.agriconnect.dto.UserDto;
import ne.agriconnect.exception.BadRequestException;
import ne.agriconnect.exception.NotFoundException;
import ne.agriconnect.repository.ReviewRepository;
import ne.agriconnect.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final NotificationService notificationService;
    private final SmsService smsService;
    private final DtoMapper mapper;

    @Transactional
    public AuthResponse register(RegisterRequest req) {
        if (userRepository.existsByPhone(req.phone())) {
            throw new BadRequestException("Ce numéro de téléphone est déjà utilisé");
        }
        if (req.email() != null && !req.email().isBlank() && userRepository.existsByEmail(req.email())) {
            throw new BadRequestException("Cet email est déjà utilisé");
        }
        User user = User.builder()
                .name(req.name().trim())
                .phone(req.phone().trim())
                .email(req.email() == null || req.email().isBlank() ? null : req.email().trim())
                .password(passwordEncoder.encode(req.password()))
                .role(req.role())
                .region(req.region())
                .locality(req.locality())
                .latitude(req.latitude())
                .longitude(req.longitude())
                .organization(req.organization())
                .build();
        userRepository.save(user);

        smsService.send(user.getPhone(),
                "Bienvenue sur AgriConnect Niger, " + user.getName() + " ! Votre compte a été créé avec succès.");
        notificationService.notify(user.getId(), "Bienvenue sur AgriConnect Niger",
                "Votre compte " + user.getRole().getLabel() + " est prêt. Découvrez le marché agricole en direct.",
                NotificationType.SYSTEM, NotificationChannel.IN_APP);

        return new AuthResponse(jwtService.generateToken(user), mapper.userToDto(user, 0.0));
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest req) {
        User user = userRepository.findByPhone(req.phone().trim())
                .orElseThrow(() -> new BadRequestException("Numéro ou mot de passe incorrect"));
        if (!passwordEncoder.matches(req.password(), user.getPassword())) {
            throw new BadRequestException("Numéro ou mot de passe incorrect");
        }
        if (!user.isActive()) {
            throw new BadRequestException("Compte désactivé. Contactez l'administration.");
        }
        double rating = averageRating(user.getId());
        return new AuthResponse(jwtService.generateToken(user), mapper.userToDto(user, rating));
    }

    @Transactional(readOnly = true)
    public UserDto me(String phone) {
        User user = userRepository.findByPhone(phone)
                .orElseThrow(() -> new NotFoundException("Utilisateur introuvable"));
        return mapper.userToDto(user, averageRating(user.getId()));
    }

    @Transactional(readOnly = true)
    public UserDto userDto(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Utilisateur introuvable"));
        return mapper.userToDto(user, averageRating(userId));
    }

    public double averageRating(Long userId) {
        var reviews = reviewRepository.findByRevieweeIdOrderByCreatedAtDesc(userId);
        if (reviews.isEmpty()) {
            return 0.0;
        }
        return Math.round(reviews.stream().mapToInt(r -> r.getRating()).average().orElse(0.0) * 10.0) / 10.0;
    }
}
