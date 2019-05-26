package forces;

public abstract class Unit extends Force {

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
            double ratio = (double) (replenish / unit.strength);
            getReinforced(replenish, unit.xp, unit.morale, unit.fatigue, unit.foodStock * ratio, unit.ammoStock * ratio,
                    unit.foodNeed * ratio, unit.ammoNeed * ratio, unit.foodLimit * ratio, unit.ammoLimit * ratio,
                    unit.fire * ratio, unit.charge * ratio);


            unit.foodStock *= (1 - ratio);
            unit.ammoStock *= (1 - ratio);
            unit.foodNeed *= (1 - ratio);
            unit.ammoNeed *= (1 - ratio);
            unit.foodLimit *= (1 - ratio);
            unit.ammoLimit *= (1 - ratio);
            unit.fire *= (1 - ratio);
            unit.charge *= (1 - ratio);

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

    public Unit bearLoss(double ratio) {
        if (type == ARTILLERY) ratio *=0.8;
        int s = (int) (strength * ratio);
        double fS = foodStock * ratio;
        double aS = ammoStock * ratio;
        double fN = foodNeed * ratio;
        double aN = ammoNeed * ratio;
        double fL = foodLimit * ratio;
        double aL = ammoLimit * ratio;
        double f = fire * ratio;
        double c = charge * ratio;
        strength -= s;
        foodStock -= fS;
        ammoStock -= aS;
        foodNeed -= fN;
        ammoNeed -= aN;
        foodLimit -= fL;
        ammoLimit -= aL;
        fire -= f;
        charge -= c;

        if (isSub)
            superForce.getReinforced(-s, this.xp, this.morale, this.fatigue, -fS, -aS, -fN, -aN, -fL, -aL, -f, -c);

        return this;
    }

    public Unit changeMorale(double change) {
        if (type == CAVALRY) change *= 0.8;
        morale += change;
        if (isSub) superForce.moralize(strength, change);
        return this;
    }
}
