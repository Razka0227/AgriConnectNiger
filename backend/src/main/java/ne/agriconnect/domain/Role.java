package ne.agriconnect.domain;

public enum Role {
    FARMER("Agriculteur"),
    BUYER("Acheteur"),
    TRANSPORTER("Transporteur"),
    ADMIN("Administrateur");

    private final String label;

    Role(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
