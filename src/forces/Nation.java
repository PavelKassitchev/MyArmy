package forces;

public enum Nation {
    AUSTRIA,
    BRITAIN,
    FRANCE,
    PRUSSIA,
    RUSSIA;

    private float nationalMorale;
    static
    {
        FRANCE.nationalMorale = 1;
        AUSTRIA.nationalMorale = 1;
    }

    public float getNationalMorale() {
        return nationalMorale;
    }

    public void setNationalMorale(float nationalMorale) {
        this.nationalMorale = nationalMorale;
    }
}
