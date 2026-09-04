package ne.agriconnect.domain;

public enum OfferStatus {
    ACTIVE("Active"),
    RESERVED("Réservée"),
    CLOSED("Clôturée");

    private final String label;

    OfferStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
