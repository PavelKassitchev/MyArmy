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

    @Override
    public Unit getReplenished(Unit unit) {
        if (unit.type == type) {
            int replenish = maxStrength - strength;
            if (unit.strength < replenish) {
                replenish = unit.strength;
            }
            double ratio = (double)(replenish / unit.strength);
            getReinforced(replenish, unit.xp, unit.morale, unit.fatigue, unit.foodStock * ratio, unit.ammoStock * ratio,
                    unit.foodNeed * ratio, unit.ammoNeed * ratio, unit.foodLimit * ratio, unit.ammoLimit * ratio);


            unit.foodStock *= (1 - ratio);
            unit.ammoStock *= (1 - ratio);
            unit.foodNeed *= (1 - ratio);
            unit.ammoNeed *= (1 - ratio);
            unit.foodLimit *= (1 - ratio);
            unit.ammoLimit *= (1 - ratio);

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
