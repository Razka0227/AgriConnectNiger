package ne.agriconnect.config;

import lombok.RequiredArgsConstructor;
import ne.agriconnect.domain.*;
import ne.agriconnect.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OfferRepository offerRepository;
    private final MarketPriceRepository priceRepository;
    private final WeatherForecastRepository weatherRepository;
    private final TransportRouteRepository routeRepository;
    private final ReviewRepository reviewRepository;
    private final NotificationRepository notificationRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        if (userRepository.count() > 0) {
            return;
        }
        seedUsers();
        seedProducts();
        seedOffers();
        seedPrices();
        seedWeather();
        seedTransport();
        seedReviews();
        seedNotifications();
    }

    private void seedUsers() {
        User admin = user("Administrateur AgriConnect", "97000000", "admin@agriconnect.ne", "admin123",
                Role.ADMIN, Region.NIAMEY, "Niamey", 13.5125, 2.1209, "AgriConnect Niger");
        User moussa = user("Moussa Ibrahim", "97000001", null, "farmer123",
                Role.FARMER, Region.ZINDER, "Matamèye", 13.3960, 8.4780, "Coopérative Matamèye");
        User aminatou = user("Hadjia Aminatou", "97000002", null, "farmer123",
                Role.FARMER, Region.MARADI, "Madarounfa", 13.3087, 7.1559, "GIE Marianna");
        User oumarou = user("Oumarou Bello", "97000003", null, "farmer123",
                Role.FARMER, Region.TAHOUA, "Boubon", 14.6818, 6.3077, "Groupement Boubon");
        User salifou = user("Salifou Djibo", "97000004", null, "farmer123",
                Role.FARMER, Region.DOSSO, "Dogondoutchi", 13.6415, 4.0307, "Coopérative Dogondoutchi");
        User ramatou = user("Ramatou Issa", "97000005", null, "farmer123",
                Role.FARMER, Region.TILLABERI, "Tillabéri", 14.2071, 1.4520, "GIE Tillabéri");
        User halidou = user("Halidou Seyni", "97000006", null, "farmer123",
                Role.FARMER, Region.MARADI, "Tessaoua", 13.7573, 7.9854, "Coopérative Tessaoua");

        User ibrahim = user("Ibrahim Sani (Grossiste)", "97000010", "ibrahim@marchene.ne", "buyer123",
                Role.BUYER, Region.NIAMEY, "Marché Katako", 13.5137, 2.1219, "SANI Distribution");
        User aichatou = user("Aïchatou Garba", "97000011", null, "buyer123",
                Role.BUYER, Region.MARADI, "Marché central", 13.4917, 7.0939, "Garba Alimentation");
        User sadou = user("Sadou Ousmane", "97000012", null, "buyer123",
                Role.BUYER, Region.ZINDER, "Grand Marché", 13.8049, 8.9832, "Ousmane Comptoir");

        User abdou = user("Abdou Mahamadou", "97000020", null, "transport123",
                Role.TRANSPORTER, Region.NIAMEY, "Niamey", 13.5125, 2.1209, "Transport Sahra");
    }

    private User user(String name, String phone, String email, String rawPassword, Role role,
                      Region region, String locality, Double lat, Double lng, String org) {
        User u = User.builder()
                .name(name).phone(phone).email(email).password(passwordEncoder.encode(rawPassword))
                .role(role).region(region).locality(locality).latitude(lat).longitude(lng)
                .organization(org).active(true).build();
        return userRepository.save(u);
    }

    private void seedProducts() {
        product("Mil", "Hatsi", ProductCategory.CEREALES, "sac de 100 kg");
        product("Sorgho", "Dawa", ProductCategory.CEREALES, "sac de 100 kg");
        product("Riz local", "Shinkafa", ProductCategory.CEREALES, "sac de 50 kg");
        product("Niébé", "Wake", ProductCategory.LEGUMINEUSES, "sac de 100 kg");
        product("Arachide", "Gya'da", ProductCategory.LEGUMINEUSES, "sac de 50 kg");
        product("Sésame", "Ridi", ProductCategory.LEGUMINEUSES, "sac de 50 kg");
        product("Oignon", "Albasa", ProductCategory.LEGUMES, "sac de 25 kg");
        product("Tomate", "Tumatir", ProductCategory.LEGUMES, "caisse de 20 kg");
        product("Gombo", "Kubewa", ProductCategory.LEGUMES, "caisse de 20 kg");
        product("Pomme de terre", "Dankali", ProductCategory.TUBERCULES, "sac de 50 kg");
        product("Igname", "Doya", ProductCategory.TUBERCULES, "sac de 50 kg");
        product("Mangue", "Mangwaro", ProductCategory.FRUITS, "caisse de 20 kg");
        product("Chèvre", "Akwiya", ProductCategory.ANIMAUX, "tête");
        product("Mouton", "Rago", ProductCategory.ANIMAUX, "tête");
        product("Lait frais", "Madara", ProductCategory.AUTRES, "litre");
    }

    private void product(String name, String local, ProductCategory cat, String unit) {
        productRepository.save(Product.builder().name(name).localName(local).category(cat).unit(unit).build());
    }

    private void seedOffers() {
        Product mil = productRepository.findAll().get(0);
        Product sorgho = productRepository.findAll().get(1);
        Product niebe = productRepository.findAll().get(3);
        Product oignon = productRepository.findAll().get(6);
        Product sesame = productRepository.findAll().get(5);
        Product chevre = productRepository.findAll().get(12);

        User moussa = userRepository.findByPhone("97000001").orElseThrow();
        User aminatou = userRepository.findByPhone("97000002").orElseThrow();
        User oumarou = userRepository.findByPhone("97000003").orElseThrow();
        User salifou = userRepository.findByPhone("97000004").orElseThrow();
        User ramatou = userRepository.findByPhone("97000005").orElseThrow();

        offer(moussa, mil, "Mil de Matamèye, très bonne qualité", 120.0, 28000.0, 20.0,
                "Récolte 2025, bien séché", "Premium");
        offer(moussa, niebe, "Niébé blanc trié", 80.0, 36000.0, 20.0, null, "Standard");
        offer(aminatou, mil, "Mil rouge Madarounfa", 90.0, 27500.0, 15.0, null, "Standard");
        offer(aminatou, sorgho, "Sorgho rouge", 60.0, 24000.0, 10.0, null, "Standard");
        offer(oumarou, oignon, "Oignon de Boubon", 200.0, 12500.0, 40.0,
                "Oignons de la vallée, gros calibre", "Premium");
        offer(oumarou, sorgho, "Sorgho blanc", 100.0, 23500.0, 20.0, null, "Standard");
        offer(salifou, mil, "Mil blanc Dogondoutchi", 150.0, 27000.0, 30.0, null, "Standard");
        offer(salifou, sesame, "Sésame graine", 40.0, 45000.0, 10.0, "Sésame de contre-saison", "Premium");
        offer(ramatou, niebe, "Niébé rouge Tillabéri", 50.0, 38000.0, 15.0, null, "Standard");
        offer(ramatou, chevre, "Chèvres vivantes", 30.0, 25000.0, 5.0, "Chèvres rousses", "Standard");
    }

    private void offer(User seller, Product product, String title, Double qty, Double price,
                       Double minQty, String description, String grade) {
        offerRepository.save(Offer.builder()
                .seller(seller).product(product).title(title).quantity(qty)
                .unit(product.getUnit()).pricePerUnit(price).minOrderQuantity(minQty)
                .region(seller.getRegion()).locality(seller.getLocality())
                .latitude(seller.getLatitude()).longitude(seller.getLongitude())
                .description(description).qualityGrade(grade).status(OfferStatus.ACTIVE).build());
    }

    private void seedPrices() {
        List<Product> products = productRepository.findAll();
        LocalDate today = LocalDate.now();
        addPrice(products.get(0), Region.ZINDER, "Grand Marché (Zinder)", 28000.0, today, "SIMA");
        addPrice(products.get(0), Region.MARADI, "Marché central (Maradi)", 27500.0, today, "SIMA");
        addPrice(products.get(0), Region.NIAMEY, "Marché Katako (Niamey)", 30000.0, today, "SIMA");
        addPrice(products.get(1), Region.MARADI, "Marché central (Maradi)", 24000.0, today, "SIMA");
        addPrice(products.get(1), Region.NIAMEY, "Marché Katako (Niamey)", 25500.0, today, "SIMA");
        addPrice(products.get(3), Region.ZINDER, "Grand Marché (Zinder)", 36000.0, today, "SIMA");
        addPrice(products.get(3), Region.DIFFA, "Marché de Diffa", 39000.0, today, "SIMA");
        addPrice(products.get(6), Region.TAHOUA, "Marché de Tahoua", 12500.0, today, "SIMA");
        addPrice(products.get(6), Region.NIAMEY, "Marché Katako (Niamey)", 14500.0, today, "SIMA");
        addPrice(products.get(5), Region.MARADI, "Marché central (Maradi)", 45000.0, today, "SIMA");
        addPrice(products.get(5), Region.ZINDER, "Grand Marché (Zinder)", 46500.0, today, "SIMA");
        addPrice(products.get(4), Region.DOSSO, "Marché de Dogondoutchi", 30000.0, today, "SIMA");
        addPrice(products.get(4), Region.NIAMEY, "Marché Katako (Niamey)", 33000.0, today.minusDays(1), "SIMA");
        addPrice(products.get(7), Region.MARADI, "Marché central (Maradi)", 7500.0, today, "SIMA");
        addPrice(products.get(7), Region.NIAMEY, "Marché Katako (Niamey)", 9000.0, today, "SIMA");
        addPrice(products.get(12), Region.ZINDER, "Grand Marché (Zinder)", 25000.0, today, "SIMA");
        addPrice(products.get(13), Region.NIAMEY, "Marché Katako (Niamey)", 35000.0, today, "SIMA");
    }

    private void addPrice(Product product, Region region, String market, Double price,
                          LocalDate date, String source) {
        priceRepository.save(MarketPrice.builder()
                .product(product).region(region).marketName(market).pricePerUnit(price)
                .unit(product.getUnit()).date(date).source(source).build());
    }

    private void seedWeather() {
        LocalDate today = LocalDate.now();
        String[][] data = {
                {Region.NIAMEY.name(), "CLEAR", "31.0", "42.0", "35.0", "0.0", "Très chaud. Arrosez tôt le matin."},
                {Region.NIAMEY.name(), "PARTLY_CLOUDY", "30.0", "41.0", "40.0", "0.0", "Semis possibles."},
                {Region.MARADI.name(), "RAINY", "26.0", "36.0", "70.0", "18.0", "Pluies attendues, propices aux cultures."},
                {Region.MARADI.name(), "RAINY", "25.0", "35.0", "75.0", "22.0", "Fortes pluies. Protégez les récoltes."},
                {Region.ZINDER.name(), "PARTLY_CLOUDY", "28.0", "39.0", "45.0", "3.0", "Conditions favorables."},
                {Region.ZINDER.name(), "CLEAR", "29.0", "40.0", "38.0", "0.0", "Journée sèche et ensoleillée."},
                {Region.DOSSO.name(), "DUSTY", "30.0", "41.0", "30.0", "0.0", "Vent de sable. Protégez les semis."},
                {Region.TAHOUA.name(), "CLOUDY", "28.0", "38.0", "50.0", "5.0", "Nuageux, léger risque de pluie."},
                {Region.TILLABERI.name(), "CLEAR", "31.0", "43.0", "32.0", "0.0", "Forte chaleur. Surveillez l'hydratation."},
                {Region.DIFFA.name(), "WINDY", "29.0", "40.0", "35.0", "0.0", "Vent fort. Évitez les brûlis."},
                {Region.AGADEZ.name(), "DUSTY", "28.0", "42.0", "20.0", "0.0", "Poussière. Conditions très sèches."},
        };
        for (String[] row : data) {
            for (int i = 0; i < 3; i++) {
                weatherRepository.save(WeatherForecast.builder()
                        .region(Region.valueOf(row[0]))
                        .date(today.plusDays(i))
                        .condition(WeatherCondition.valueOf(row[1]))
                        .tempMinC(Double.parseDouble(row[2]))
                        .tempMaxC(Double.parseDouble(row[3]))
                        .humidityPct(Double.parseDouble(row[4]))
                        .rainfallMm(Double.parseDouble(row[5]))
                        .advice(row[6])
                        .build());
            }
        }
    }

    private void seedTransport() {
        route("Niamey - Maradi", "Niamey", Region.NIAMEY, "Maradi", Region.MARADI, 600.0, 9.0, 45.0, "Transport Sahra");
        route("Maradi - Niamey", "Maradi", Region.MARADI, "Niamey", Region.NIAMEY, 600.0, 9.0, 40.0, "Transport Sahra");
        route("Niamey - Zinder", "Niamey", Region.NIAMEY, "Zinder", Region.ZINDER, 900.0, 13.0, 65.0, "SCOA Transport");
        route("Zinder - Niamey", "Zinder", Region.ZINDER, "Niamey", Region.NIAMEY, 900.0, 13.0, 60.0, "SCOA Transport");
        route("Niamey - Tahoua", "Niamey", Region.NIAMEY, "Tahoua", Region.TAHOUA, 380.0, 6.0, 30.0, "Transport Sahra");
        route("Tahoua - Niamey", "Tahoua", Region.TAHOUA, "Niamey", Region.NIAMEY, 380.0, 6.0, 28.0, "Transport Sahra");
        route("Maradi - Zinder", "Maradi", Region.MARADI, "Zinder", Region.ZINDER, 250.0, 4.0, 20.0, "Kano Express");
        route("Dosso - Niamey", "Dosso", Region.DOSSO, "Niamey", Region.NIAMEY, 140.0, 2.0, 12.0, "Transport Sahra");
        route("Zinder - Diffa", "Zinder", Region.ZINDER, "Diffa", Region.DIFFA, 470.0, 7.0, 40.0, "Aïr Transport");
    }

    private void route(String name, String fromCity, Region from, String toCity, Region to,
                       Double km, Double hours, Double cost, String provider) {
        routeRepository.save(TransportRoute.builder()
                .name(name).fromCity(fromCity).fromRegion(from).toCity(toCity).toRegion(to)
                .distanceKm(km).estimatedHours(hours).costPerKgCfa(cost).provider(provider).build());
    }

    private void seedReviews() {
        User ibrahim = userRepository.findByPhone("97000010").orElseThrow();
        User moussa = userRepository.findByPhone("97000001").orElseThrow();
        User aminatou = userRepository.findByPhone("97000002").orElseThrow();
        User aichatou = userRepository.findByPhone("97000011").orElseThrow();

        review(ibrahim, moussa, 5, "Très bon mil, bien séché, livraison rapide.");
        review(aichatou, aminatou, 4, "Bonnes céréales, qualité correcte.");
        review(ibrahim, aminatou, 5, "Marché sérieux et prix raisonnables.");
    }

    private void review(User reviewer, User reviewee, int rating, String comment) {
        reviewRepository.save(Review.builder()
                .reviewer(reviewer).reviewee(reviewee).rating(rating).comment(comment).build());
    }

    private void seedNotifications() {
        User admin = userRepository.findByPhone("97000000").orElseThrow();
        User moussa = userRepository.findByPhone("97000001").orElseThrow();
        notificationRepository.save(Notification.builder()
                .user(admin).title("Bienvenue sur AgriConnect Niger")
                .message("Le tableau de bord vous permet de piloter le marché agricole en temps réel.")
                .type(NotificationType.SYSTEM).channel(NotificationChannel.IN_APP).read(false).build());
        notificationRepository.save(Notification.builder()
                .user(moussa).title("Alerte météo")
                .message("Pluies attendues à Zinder. Pensez à protéger vos récoltes.")
                .type(NotificationType.WEATHER).channel(NotificationChannel.SMS).read(false).build());
        notificationRepository.save(Notification.builder()
                .user(moussa).title("Prix du mil en hausse")
                .message("Le mil se négocie à 30 000 FCFA au Marché Katako de Niamey.")
                .type(NotificationType.PRICE_ALERT).channel(NotificationChannel.SMS).read(false).build());
    }
}
