package forces;

public enum Nation {
    AUSTRIA,
    BRITAIN,
    FRANCE,
    PRUSSIA,
    RUSSIA;

    private float nationalMorale;

    public float getNationalMorale() {
        return nationalMorale;
    }

    public void setNationalMorale(float nationalMorale) {
        this.nationalMorale = nationalMorale;
    }
}
