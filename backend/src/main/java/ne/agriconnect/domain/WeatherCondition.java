package ne.agriconnect.domain;

public enum WeatherCondition {
    CLEAR("Ensoleillé"),
    PARTLY_CLOUDY("Partiellement nuageux"),
    CLOUDY("Nuageux"),
    RAINY("Pluvieux"),
    WINDY("Venteux"),
    DUSTY("Poussiéreux"),
    STORMY("Orageux");

    private final String label;

    WeatherCondition(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
