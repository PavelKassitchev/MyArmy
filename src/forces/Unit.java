package forces;

public abstract class Unit extends Force{

    public static final int SUPPLY = 0;
    public static final int INFANTRY = 1;
    public static final int CAVALRY = 2;
    public static final int ARTILLERY = 3;

    int type;
    int maxStrength;

    public Unit(Nation nation, Hex hex) {
        super(nation, hex);
    }

    public Unit getReplenished(Unit unit) {
        if (unit.type == type) {
            int replenish = maxStrength - strength;
            if (unit.strength < replenish) {
                replenish = unit.strength;
            }
            double ratio = replenish / unit.strength;

            xp = (xp * strength + replenish * unit.xp) / (strength + replenish);
            morale = (morale * strength + replenish * unit.morale) / (strength + replenish);
            fatigue = (fatigue * strength + replenish * unit.fatigue) / (strength + replenish);

            foodStock += unit.foodStock * ratio;
            ammoStock += unit.ammoStock * ratio;
            foodNeed += unit.foodNeed * ratio;
            ammoNeed += unit.ammoNeed * ratio;
            foodLimit += unit.foodLimit * ratio;
            ammoLimit += unit.ammoLimit * ratio;

            unit.foodStock *= (1 - ratio);
            unit.ammoStock *= (1 - ratio);
            unit.foodNeed *= (1 - ratio);
            unit.ammoNeed *= (1 - ratio);
            unit.foodLimit *= (1 - ratio);
            unit.ammoLimit *= (1 - ratio);

            strength += replenish;
            unit.strength -= replenish;

        }
        if (unit.strength == 0) return null;
        return unit;
    }

    public boolean belongsToTypes(int... types) {
        for (int i = 0; i < types.length; i++) {
            if (type == types[i]) return true;
        }
        return false;
    }
}
