package ne.agriconnect.domain;

public enum OrderStatus {
    PENDING("En attente de confirmation"),
    CONFIRMED("Confirmée"),
    PACKED("Préparée"),
    IN_TRANSIT("En transit"),
    DELIVERED("Livrée"),
    CANCELLED("Annulée");

    private final String label;

    OrderStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
