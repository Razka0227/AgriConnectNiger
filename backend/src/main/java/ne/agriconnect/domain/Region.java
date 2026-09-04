package ne.agriconnect.domain;

public enum Region {
    NIAMEY("Niamey"),
    DOSSO("Dosso"),
    TILLABERI("Tillabéri"),
    TAHOUA("Tahoua"),
    MARADI("Maradi"),
    ZINDER("Zinder"),
    DIFFA("Diffa"),
    AGADEZ("Agadez");

    private final String label;

    Region(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
