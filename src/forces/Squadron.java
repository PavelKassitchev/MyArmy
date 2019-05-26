package forces;

public class Squadron extends Unit {
    public static final int MAX_STRENGTH = 300;
    public final static double SPEED = 44;
    public final static double FOOD_NEED = 1.8;
    public final static double AMMO_NEED = 0.25;
    public final static double FOOD_LIMIT = 12;
    public final static double AMMO_LIMIT = 4;
    public final static double FIRE = 0.17;
    public final static double CHARGE = 4;

    public Squadron(Nation nation, Hex hex) {
        this(nation, hex, MAX_STRENGTH);
    }

    public Squadron(Nation nation, Hex hex, int strength) {
        super(nation, hex);
        isUnit = true;
        type = CAVALRY;
        maxStrength = MAX_STRENGTH;
        this.strength = strength;
        speed = SPEED;
        foodNeed = FOOD_NEED * strength / maxStrength;
        ammoNeed = AMMO_NEED * strength / maxStrength;
        foodLimit = FOOD_LIMIT * strength / maxStrength;
        ammoLimit = AMMO_LIMIT * strength / maxStrength;
        foodStock = FOOD_LIMIT * strength / maxStrength;
        ammoStock = AMMO_LIMIT * strength / maxStrength;
        fire = FIRE  * strength / maxStrength;
        charge = CHARGE * strength / maxStrength;
        xp = 0;
        fatigue = 0;
        morale = nation.getNationalMorale();
    }
}
