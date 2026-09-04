package ne.agriconnect.service;

import lombok.RequiredArgsConstructor;
import ne.agriconnect.domain.Offer;
import ne.agriconnect.domain.OfferStatus;
import ne.agriconnect.domain.Product;
import ne.agriconnect.domain.User;
import ne.agriconnect.dto.OfferDto;
import ne.agriconnect.dto.OfferRequest;
import ne.agriconnect.dto.ProductDto;
import ne.agriconnect.exception.BadRequestException;
import ne.agriconnect.exception.NotFoundException;
import ne.agriconnect.repository.OfferRepository;
import ne.agriconnect.repository.ProductRepository;
import ne.agriconnect.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OfferService {

    private final OfferRepository offerRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final AuthService authService;
    private final DtoMapper mapper;

    @Transactional
    public OfferDto create(OfferRequest req, Long userId) {
        User seller = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Utilisateur introuvable"));
        Product product = productRepository.findById(req.productId())
                .orElseThrow(() -> new NotFoundException("Produit introuvable"));
        Offer offer = Offer.builder()
                .seller(seller)
                .product(product)
                .title(req.title().trim())
                .quantity(req.quantity())
                .unit(req.unit().trim())
                .pricePerUnit(req.pricePerUnit())
                .minOrderQuantity(req.minOrderQuantity())
                .region(req.region() != null ? req.region() : seller.getRegion())
                .locality(req.locality() != null ? req.locality() : seller.getLocality())
                .latitude(req.latitude() != null ? req.latitude() : seller.getLatitude())
                .longitude(req.longitude() != null ? req.longitude() : seller.getLongitude())
                .description(req.description())
                .qualityGrade(req.qualityGrade())
                .status(OfferStatus.ACTIVE)
                .build();
        offerRepository.save(offer);
        return toDto(offer);
    }

    @Transactional(readOnly = true)
    public List<OfferDto> list(Long productId, String region, Double maxPrice, String q) {
        List<Offer> offers = offerRepository.findByStatusOrderByCreatedAtDesc(OfferStatus.ACTIVE);
        List<Offer> filtered = offers.stream()
                .filter(o -> productId == null || o.getProduct().getId().equals(productId))
                .filter(o -> region == null || region.isBlank() || o.getRegion().name().equalsIgnoreCase(region))
                .filter(o -> maxPrice == null || o.getPricePerUnit() <= maxPrice)
                .filter(o -> q == null || q.isBlank()
                        || o.getTitle().toLowerCase().contains(q.toLowerCase())
                        || o.getProduct().getName().toLowerCase().contains(q.toLowerCase())
                        || (o.getLocality() != null && o.getLocality().toLowerCase().contains(q.toLowerCase())))
                .sorted(Comparator.comparing(Offer::getCreatedAt).reversed())
                .toList();
        return filtered.stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public List<OfferDto> mine(Long userId) {
        return offerRepository.findBySellerIdOrderByCreatedAtDesc(userId)
                .stream().map(this::toDto).toList();
    }

    @Transactional
    public OfferDto changeStatus(Long id, OfferStatus status, Long userId) {
        Offer offer = offerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Offre introuvable"));
        if (!offer.getSeller().getId().equals(userId) && !isAdmin(userId)) {
            throw new BadRequestException("Vous n'êtes pas autorisé à modifier cette offre");
        }
        if (status == OfferStatus.CLOSED && offer.getStatus() == OfferStatus.RESERVED) {
            throw new BadRequestException("Impossible de clôturer une offre réservée par une commande en cours");
        }
        offer.setStatus(status);
        offerRepository.save(offer);
        return toDto(offer);
    }

    @Transactional(readOnly = true)
    public OfferDto get(Long id) {
        Offer offer = offerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Offre introuvable"));
        return toDto(offer);
    }

    private OfferDto toDto(Offer offer) {
        ProductDto product = mapper.productToDto(offer.getProduct());
        double rating = authService.averageRating(offer.getSeller().getId());
        return mapper.offerToDto(offer, product, rating);
    }

    private boolean isAdmin(Long userId) {
        return userRepository.findById(userId).map(u -> u.getRole().name().equals("ADMIN")).orElse(false);
    }
}
