package forces;

public class Battery extends Unit {
    public static final int MAX_STRENGTH = 104;
    public final static double SPEED = 20;
    public final static double FOOD_NEED = 0.6;
    public final static double AMMO_NEED = 2;
    public final static double FOOD_LIMIT = 8;
    public final static double AMMO_LIMIT = 5;
    public final  static double FIRE = 4;
    public final static double CHARGE = 0.0;

    public Battery(Nation nation, Hex hex) {
        this(nation, hex, MAX_STRENGTH);
    }
    public Battery(Nation nation, Hex hex, int strength) {
        super(nation, hex);
        isUnit = true;
        type = ARTILLERY;
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
