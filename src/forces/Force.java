package forces;

import java.util.ArrayList;
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

    double speed;

    //STATIC SECTION

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

    private void exclude(Force force) {
        double x = xp * strength;
        double m = morale * strength;
        double f = fatigue * strength;
        strength = strength - force.strength;
        foodLimit = foodLimit - force.foodLimit;
        ammoLimit = ammoLimit - force.ammoLimit;
        foodNeed = foodNeed - force.foodNeed;
        ammoNeed = ammoNeed - force.ammoNeed;
        foodStock = foodStock - force.foodStock;
        ammoStock = ammoStock - force.ammoStock;


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

    public double distributeFood(double food) {
        double free = food + unloadFood();
        if (free > foodLimit) {
            System.out.println("FIRST");
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
            free -= loadFoodTowagons(free);
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
        System.out.println("ATTENTION! : Name: " + name + " RATIO: " + ratio + " need: " + need);
        return need;
    }
    public double loadFoodTowagons(double food) {
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

}
