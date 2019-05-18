package forces;

public class Squadron extends Unit {
    public static final int MAX_STRENGTH = 300;
    public final static double SPEED = 60;
    public final static double FOOD_NEED = 1.8;
    public final static double AMMO_NEED = 0.25;
    public final static double FOOD_LIMIT = 12;
    public final static double AMMO_LIMIT = 4;

    public Squadron(Nation nation, Hex hex) {
        super(nation, hex);
        isUnit = true;
        type = CAVALRY;
        maxStrength = MAX_STRENGTH;
        strength = MAX_STRENGTH;
        speed = SPEED;
        foodNeed = FOOD_NEED;
        ammoNeed = AMMO_NEED;
        foodLimit = FOOD_LIMIT;
        ammoLimit = AMMO_LIMIT;
        foodStock = FOOD_LIMIT;
        ammoStock = AMMO_LIMIT;
        xp = 0;
        fatigue = 0;
        morale = nation.getNationalMorale();
    }
}
