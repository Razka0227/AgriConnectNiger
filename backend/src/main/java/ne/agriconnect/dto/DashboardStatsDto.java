package ne.agriconnect.dto;

public record DashboardStatsDto(
        long activeOffers,
        long totalOffers,
        long myOffers,
        long myOrders,
        long ordersToDeliver,
        long unreadNotifications,
        double myRating,
        String currency
) {
}
