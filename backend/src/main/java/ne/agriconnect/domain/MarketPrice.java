package ne.agriconnect.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "market_prices")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MarketPrice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Enumerated(EnumType.STRING)
    private Region region;

    private String marketName;

    @Column(nullable = false)
    private Double pricePerUnit;

    @Column(nullable = false)
    private String unit;

    private LocalDate date;

    private String source;
}
