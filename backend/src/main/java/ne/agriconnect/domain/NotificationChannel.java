package ne.agriconnect.domain;

public enum NotificationChannel {
    IN_APP("In-app"),
    SMS("SMS"),
    EMAIL("Email"),
    USSD("USSD");

    private final String label;

    NotificationChannel(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
