package ne.agriconnect.domain;

public enum ProductCategory {
    CEREALES("Céréales"),
    LEGUMINEUSES("Légumineuses"),
    LEGUMES("Légumes"),
    FRUITS("Fruits"),
    TUBERCULES("Tubercules"),
    ANIMAUX("Bétail et volaille"),
    AUTRES("Autres");

    private final String label;

    ProductCategory(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
