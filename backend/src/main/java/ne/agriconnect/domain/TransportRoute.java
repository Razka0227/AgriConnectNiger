package ne.agriconnect.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "transport_routes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransportRoute {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String fromCity;

    @Enumerated(EnumType.STRING)
    private Region fromRegion;

    private String toCity;

    @Enumerated(EnumType.STRING)
    private Region toRegion;

    private Double distanceKm;

    private Double estimatedHours;

    private Double costPerKgCfa;

    private String provider;
}
