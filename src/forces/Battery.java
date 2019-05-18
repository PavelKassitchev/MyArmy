package forces;

public class Battery extends Unit {
    public static final int MAX_STRENGTH = 104;
    public final static double SPEED = 20;
    public final static double FOOD_NEED = 0.6;
    public final static double AMMO_NEED = 2;
    public final static double FOOD_LIMIT = 8;
    public final static double AMMO_LIMIT = 5;

    public Battery(Nation nation, Hex hex) {
        super(nation, hex);
        isUnit = true;
        type = ARTILLERY;
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
