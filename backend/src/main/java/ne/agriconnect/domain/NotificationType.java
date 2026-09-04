package ne.agriconnect.domain;

public enum NotificationType {
    INFO("Information"),
    ORDER("Commande"),
    PRICE_ALERT("Alerte prix"),
    WEATHER("Météo"),
    SYSTEM("Système");

    private final String label;

    NotificationType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
