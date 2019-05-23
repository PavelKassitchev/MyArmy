package forces;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static forces.Unit.*;

public class Force {

    List<Force> forces;
    List<Battalion> battalions;
    List<Squadron> squadrons;
    List<Battery> batteries;
    List<Wagon> wagons;

    Force superForce;

    Nation nation;
    String name;
    Hex hex;

    boolean isUnit;
    boolean isSub;

    int strength;

    double xp;
    double morale;
    double fatigue;

    double foodStock;
    double ammoStock;
    double foodNeed;
    double ammoNeed;
    double foodLimit;
    double ammoLimit;

    double fire;
    double charge;

    double speed;

    //STATIC SECTION

    //TODO exclude SUPPLY morale and xp

    public Force(Nation nation, Hex hex) {
        this.nation = nation;
        this.hex = hex;
        forces = new ArrayList<>();
        battalions = new ArrayList<>();
        squadrons = new ArrayList<>();
        batteries = new ArrayList<>();
        wagons = new ArrayList<>();
        speed = 120;
    }

    public Force(Force... subForces) {

        this(subForces[0].nation, subForces[0].hex);
        for (Force force : subForces) {
            attach(force);
        }

    }

    public Force attach(Force force) {

        force.isSub = true;
        force.superForce = this;
        forces.add(force);
        include(force);

        return this;
    }

    public Force detach(Force force) {
        force.isSub = false;
        force.superForce = null;
        forces.remove(force);
        exclude(force);

        return force;
    }

    public Unit getReplenished(Unit unit) {
        if (isUnit) return ((Unit)this).getReplenished(unit);
        else {
            for (Force force: forces) {
                if (unit == null) break;
                if (force.isUnit && ((Unit)force).type ==  unit.type) {
                    return ((Unit)force).getReplenished(unit);
                }
                else {
                    force.getReplenished(unit);
                }
            }
        }
        return unit;
    }
    // the methods takes into account Super Forces
    private void include(Force force) {
        double x = xp * strength;
        double m = morale * strength;
        double f = fatigue * strength;

        if (force.isUnit) {
            int type = ((Unit) force).type;
            if (type == SUPPLY) wagons.add((Wagon) force);
            if (type == INFANTRY) battalions.add((Battalion) force);
            if (type == CAVALRY) squadrons.add((Squadron) force);
            if (type == ARTILLERY) batteries.add((Battery) force);
        } else {
            battalions.addAll(force.battalions);
            squadrons.addAll(force.squadrons);
            batteries.addAll(force.batteries);
            wagons.addAll(force.wagons);
        }
        strength += force.strength;
        foodLimit += force.foodLimit;
        ammoLimit += force.ammoLimit;
        foodNeed += force.foodNeed;
        ammoNeed += force.ammoNeed;
        foodStock += force.foodStock;
        ammoStock += force.ammoStock;
        fire += force.fire;
        charge += force.charge;

        x += force.strength * force.xp;
        m += force.strength * force.morale;
        f += force.strength * force.fatigue;


        xp = x / strength;
        morale = m / strength;
        fatigue = f / strength;
        if (speed > force.speed) {
            speed = force.speed;
        }

        if (isSub) {
            superForce.include(force);
        }
    }

    public void getReinforced(int s, double x, double m, double f, double fStock, double aStock,
                              double fNeed, double aNeed, double fLimit, double aLimit, double fi, double c) {
        xp = (xp * strength + x * s) / (strength + s);
        morale = (morale * strength + m * s) / (strength + s);
        fatigue = (fatigue * strength + f * s) / (strength + s);

        strength += s;
        foodStock += fStock;
        ammoStock += aStock;
        foodNeed += fNeed;
        ammoNeed += aNeed;
        foodLimit += fLimit;
        ammoLimit += aLimit;
        fire += fi;
        charge += c;

        if (isSub) superForce.getReinforced(s, x, m, f, fStock, aStock, fNeed, aNeed, fLimit, aLimit, fi, c);
    }
    // the methods takes into account Super Forces
    public void moralize(double s, double change) {
        double c = s * change / strength;
        morale += c;
        if (isSub) superForce.moralize(strength, c);
    }

    private void exclude(Force force) {
        double x = xp * strength;
        double m = morale * strength;
        double f = fatigue * strength;
        strength -= force.strength;
        foodLimit -= force.foodLimit;
        ammoLimit -= force.ammoLimit;
        foodNeed -= force.foodNeed;
        ammoNeed -= force.ammoNeed;
        foodStock -= force.foodStock;
        ammoStock -= force.ammoStock;
        fire -= force.fire;
        charge -= force.charge;


        xp = (x - force.xp * force.strength) / strength;
        morale = (m - force.morale * force.strength) / strength;
        fatigue = (f - force.fatigue * force.strength) / strength;

        if (force.isUnit) {
            int type = ((Unit) force).type;
            if (type == SUPPLY) wagons.remove(force);
            if (type == INFANTRY) battalions.remove(force);
            if (type == CAVALRY) squadrons.remove(force);
            if (type == ARTILLERY) batteries.remove(force);
        } else {
            wagons.removeAll(force.wagons);
            battalions.removeAll(force.battalions);
            squadrons.removeAll(force.squadrons);
            batteries.removeAll(force.batteries);
        }
        if (wagons.size() > 0 || batteries.size() > 0) speed = Battery.SPEED;
        else if (battalions.size() > 0) speed = Battalion.SPEED;
        else speed = Squadron.SPEED;

        if (isSub) {
            superForce.exclude(force);
        }

    }

    //this methods shows morale change dependency on inside Unit morale change

    public void moralize(int strength, double change) {
        double sChange = change * strength / this.strength;
        morale += sChange;
        if (isSub) superForce.moralize(this.strength, sChange);
    }

    public void throwWagons() {
        List<Wagon> toThrow = new ArrayList<>(wagons);

        for (Wagon wagon: toThrow) {
            wagon.superForce.detach(wagon);
        }
    }

    //This method distributs all ammo of the force plus extra ammo (argument double ammo) between all the units and sub-forces
    //Also below are auxillary methods

    public double distributeAmmo(double ammo) {
        double free = ammo + unloadAmmo();
        if (free > ammoLimit) {

            free -= loadAmmo(SUPPLY, INFANTRY, CAVALRY, ARTILLERY);

            while (free > 0) {
                Wagon wagon = new Wagon(nation, hex);
                wagon.name = "Extra Wagon";
                wagon.foodStock = 0;
                if (free <= wagon.ammoLimit) {
                    wagon.ammoStock = free;
                    free = 0;
                } else {
                    free -= wagon.ammoLimit;

                }
                attach(wagon);
            }
        }
        else if (free >= ammoLimit - wagons.size() * Wagon.AMMO_LIMIT) {

            free -= loadAmmo(INFANTRY, CAVALRY, ARTILLERY);
            free -= loadAmmoToWagons(free);
        }


        else if (free < ammoNeed * Battery.AMMO_LIMIT / Battery.AMMO_NEED) {

            double ratio = free / ammoNeed;
            free -= loadAmmo(ratio, INFANTRY, CAVALRY, ARTILLERY);
        }
        else if (free > ammoNeed * Battalion.AMMO_LIMIT / Battalion.AMMO_NEED) {

            free -= loadAmmo(INFANTRY, ARTILLERY);
            double ratio = free / ((ammoLimit - wagons.size() * Wagon.AMMO_LIMIT - ammoStock) * Squadron.AMMO_NEED / Squadron.AMMO_LIMIT);
            free -= loadAmmo(ratio, CAVALRY);
        }
        else {

            double distributed = loadAmmo(ARTILLERY);

            free -= distributed;
            double ratio = free / (ammoNeed - distributed * Battery.AMMO_NEED / Battery.AMMO_LIMIT);
            if (ratio < Battalion.AMMO_LIMIT / Battalion.AMMO_NEED) {

                free -= loadAmmo(ratio, CAVALRY, INFANTRY);
            }
            else {

                free -= loadAmmo(ratio, INFANTRY);
                ratio = free / ((ammoLimit - wagons.size() * Wagon.AMMO_LIMIT - ammoStock) * Squadron.AMMO_NEED / Squadron.AMMO_LIMIT);
                free -= loadAmmo(ratio, CAVALRY);
            }
        }

        return free;
    }

    public double loadAmmo(double ratio, int... types) {
        double need = 0;
        for (Force force: forces) {
            if (force.isUnit) {
                if (((Unit)force).belongsToTypes(types)){
                    double n = force.ammoNeed * ratio;
                    force.ammoStock = n;
                    ammoStock += n;
                    need += n;
                }
            }
            else {
                double n = force.loadAmmo(ratio, types);
                ammoStock += n;
                need += n;
            }
        }

        return need;
    }
    public double loadAmmoToWagons(double ammo) {
        for (Wagon wagon: wagons) {
            if (ammo <= Wagon.AMMO_LIMIT) {
                wagon.ammoStock = ammo;
                ammoStock +=ammo;
                ammo = 0;
                break;
            }
            else {
                wagon.ammoStock = Wagon.AMMO_LIMIT;
                ammoStock += Wagon.AMMO_LIMIT;
                ammo -= Wagon.AMMO_LIMIT;
            }
        }
        return ammo;
    }

    public double loadAmmo(int... types) {
        double need = 0;
        for (Force force : forces) {
            if (force.isUnit) {
                if (((Unit) force).belongsToTypes(types)) {
                    force.ammoStock = force.ammoLimit;
                    ammoStock += force.ammoLimit;
                    need += force.ammoLimit;
                }
            } else {
                double n = force.loadAmmo(types);
                need += n;
                ammoStock += n;
            }
        }
        return need;
    }

    public double unloadAmmo() {
        double ammo = 0;
        for (Force force : forces) {
            if (force.isUnit) {
                ammo += force.ammoStock;
                ammoStock -= force.ammoStock;
                force.ammoStock = 0;
            } else {
                double f = force.unloadAmmo();
                ammoStock -= f;
                ammo += f;
            }
        }
        return ammo;
    }

    public double unloadAmmo(double ammo) {
        double toUnload = ammo;
        for (Force force: forces) {
            if (force.isUnit) {
                if (toUnload <= force.ammoStock) {
                    force.ammoStock -= toUnload;
                    ammoStock -= toUnload;
                    toUnload = 0;
                    break;
                }
                else {
                    ammoStock -= force.ammoStock;
                    toUnload -= force.ammoStock;
                    force.ammoStock = 0;
                }
            }
            else {
                double tU = force.unloadAmmo(toUnload);
                ammoStock -= (toUnload - tU);
                toUnload = tU;
            }
        }
        return toUnload;
    }

    //Pair methods for food
    //
    public double distributeFood(double food) {
        double free = food + unloadFood();
        if (free > foodLimit) {

            free -= loadFood(SUPPLY, INFANTRY, CAVALRY, ARTILLERY);

            while (free > 0) {
                Wagon wagon = new Wagon(nation, hex);
                wagon.name = "Extra Wagon";
                wagon.ammoStock = 0;
                if (free <= wagon.foodLimit) {
                    wagon.foodStock = free;
                    free = 0;
                } else {
                    free -= wagon.foodLimit;

                }
                attach(wagon);
            }
        }
        else if (free >= foodLimit - wagons.size() * Wagon.FOOD_LIMIT) {

            free -= loadFood(INFANTRY, CAVALRY, ARTILLERY);
            free -= loadFoodToWagons(free);
        }
        else if (free < foodNeed * Battalion.FOOD_LIMIT / Battalion.FOOD_NEED) {

            double ratio = free / foodNeed;
            free -= loadFood(ratio, INFANTRY, CAVALRY, ARTILLERY);
        }
        else if (free > foodNeed * Squadron.FOOD_LIMIT / Squadron.FOOD_NEED) {

            free -= loadFood(INFANTRY, CAVALRY);
            double ratio = free / ((foodLimit - wagons.size() * Wagon.FOOD_LIMIT - foodStock) * Battery.FOOD_NEED / Battery.FOOD_LIMIT);
            free -= loadFood(ratio, ARTILLERY);
        }
        else {

            double distributed = loadFood(INFANTRY);

            free -= distributed;
            double ratio = free / (foodNeed - distributed * Battalion.FOOD_NEED / Battalion.FOOD_LIMIT);
            if (ratio < Squadron.FOOD_LIMIT / Squadron.FOOD_NEED) {

                free -= loadFood(ratio, CAVALRY, ARTILLERY);
            }
            else {

                free -= loadFood(ratio, CAVALRY);
                ratio = free / ((foodLimit - wagons.size() * Wagon.FOOD_LIMIT - foodStock) * Battery.FOOD_NEED / Battery.FOOD_LIMIT);
                free -= loadFood(ratio, ARTILLERY);
            }
        }

        return free;
    }

    public double loadFood(double ratio, int... types) {
        double need = 0;
        for (Force force: forces) {
            if (force.isUnit) {
                if (((Unit)force).belongsToTypes(types)){
                    double n = force.foodNeed * ratio;
                    force.foodStock = n;
                    foodStock += n;
                    need += n;
                }
            }
            else {
                double n = force.loadFood(ratio, types);
                foodStock += n;
                need += n;
            }
        }

        return need;
    }
    public double loadFoodToWagons(double food) {
        for (Wagon wagon: wagons) {
            if (food <= Wagon.FOOD_LIMIT) {
                wagon.foodStock = food;
                foodStock +=food;
                food = 0;
                break;
            }
            else {
                wagon.foodStock = Wagon.FOOD_LIMIT;
                foodStock += Wagon.FOOD_LIMIT;
                food -= Wagon.FOOD_LIMIT;
            }
        }
        return food;
    }

    public double loadFood(int... types) {
        double need = 0;
        for (Force force : forces) {
            if (force.isUnit) {
                if (((Unit) force).belongsToTypes(types)) {
                    force.foodStock = force.foodLimit;
                    foodStock += force.foodLimit;
                    need += force.foodLimit;
                }
            } else {
                double n = force.loadFood(types);
                need += n;
                foodStock += n;
            }
        }
        return need;
    }

    public double unloadFood() {
        double food = 0;
        for (Force force : forces) {
            if (force.isUnit) {
                food += force.foodStock;
                foodStock -= force.foodStock;
                force.foodStock = 0;
            } else {
                double f = force.unloadFood();
                foodStock -= f;
                food += f;
            }
        }
        return food;
    }

    public double unloadFood(double food) {
        double toUnload = food;
        for (Force force: forces) {
            if (force.isUnit) {
                if (toUnload <= force.foodStock) {
                    force.foodStock -= toUnload;
                    foodStock -= toUnload;
                    toUnload = 0;
                    break;
                }
                else {
                    foodStock -= force.foodStock;
                    toUnload -= force.foodStock;
                    force.foodStock = 0;
                }
            }
            else {
                double tU = force.unloadFood(toUnload);
                foodStock -= (toUnload - tU);
                toUnload = tU;
            }
        }
        return toUnload;
    }


}
