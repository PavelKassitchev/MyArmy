package forces;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;

public class Battle {
    Force attacker;
    Force defender;
    int attackerInit;
    int defenderInit;
    int winner = 0;
    HashSet<Unit> rootedDef = new HashSet<>();
    HashSet<Unit> rootedAtt = new HashSet<>();

    public Battle(Force attacker, Force defender) {
        this.attacker = attacker;
        this.defender = defender;
        attackerInit = attacker.strength;
        defenderInit = defender.strength;
    }

    public int pursuit(int rooted) {
        int prisoners = 0;
        if (winner == 1) {
            for (Unit unit: rootedDef) {
                prisoners += unit.strength * 40 * attacker.charge / rooted;
                unit.bearLoss(40 * attacker.charge / rooted);
            }
        }
        if (winner == -1) {
            for (Unit unit: rootedAtt) {
                prisoners += unit.strength * 40 * defender.charge / rooted;
                unit.bearLoss(40 * defender.charge / rooted);
            }
        }
        return prisoners;
    }

    public int resolve() {
        String s;
        int count = 0;
        while (winner == 0) {
            s = resolveStage();
            count++;
        }
        int attackerEnd = attacker.strength;
        int defenderEnd = defender.strength;
        int attackerRooted = 0;
        int defenderRooted = 0;
        for (Unit unit : rootedDef) defenderRooted += unit.strength;

        for (Unit unit : rootedAtt) attackerRooted += unit.strength;

        if (winner == 1) {
            System.out.println("Attacker took prisoners: " + pursuit(defenderRooted));
        }
        else {
            System.out.println("Defender took prisoners: " + pursuit(attackerRooted));
        }

        System.out.println("ATTACKER: Initial - " + attackerInit + " Casualities - " + (attackerInit - attackerEnd - attackerRooted) +
                " Percentage - " + (attackerInit - attackerEnd - attackerRooted) * 100 / attackerInit + " Rooted - " + attackerRooted);
        System.out.println("DEFENDER: Initial - " + defenderInit + " Casualities - " + (defenderInit - defenderEnd - defenderRooted) +
                " Percentage - " + (defenderInit - defenderEnd - defenderRooted) * 100 / defenderInit + " Rooted - " + defenderRooted);
        System.out.println("Number of stages: " + count);
        System.out.println("WINNER = " + winner);
        System.out.println();

        return winner;
    }

    public void hitUnit(Unit unit, double fire, double charge) {

        Force opponent = (unit.nation == attacker.nation) ? defender : attacker;
        HashSet<Unit> rooted = (unit.nation == attacker.nation) ? rootedAtt : rootedDef;

        unit.bearLoss(fire);
        unit.changeMorale(charge);
        if (unit.strength <= 6 || unit.morale <= 0.2) {
            rooted.add(unit);
            selectRandomUnit(opponent).changeMorale(0.03);
            if (unit.isSub) {
                for (Force force : unit.superForce.forces) {
                    if (force.isUnit && force != unit) {
                        ((Unit) force).changeMorale(-0.03);
                        if (force.morale <= 0) rooted.add((Unit) force);
                        selectRandomUnit(opponent).changeMorale(0.02);
                    }
                }
            }
        }

    }


    public String resolveStage() {

        double fireOnDefender = 38 * attacker.fire / defender.strength;
        System.out.println("Fire on def " + fireOnDefender);

        double fireOnAttacker = 40 * defender.fire / attacker.strength;
        System.out.println("Fire on att " + fireOnAttacker);

        double chargeOnDefender = -(3.3 * fireOnDefender + 30 * attacker.charge / defender.strength);
        System.out.println("Charge on def " + chargeOnDefender);

        double chargeOnAttacker = -(3.3 * fireOnAttacker + 30 * defender.charge / attacker.strength);
        System.out.println("Charge on att " + chargeOnAttacker);


        Random random = new Random();

        StringBuilder result = new StringBuilder("Victory of ");


        ArrayList<Unit> attackerUnits = new ArrayList<>();
        if (!attacker.isUnit) {
            attackerUnits.addAll(attacker.battalions);
            attackerUnits.addAll(attacker.batteries);
            attackerUnits.addAll(attacker.squadrons);
        } else {
            attackerUnits.add((Unit) attacker);
        }

        ArrayList<Unit> defenderUnits = new ArrayList<>();
        if (!defender.isUnit) {
            defenderUnits.addAll(defender.battalions);
            defenderUnits.addAll(defender.batteries);
            defenderUnits.addAll(defender.squadrons);
        } else {
            defenderUnits.add((Unit) defender);
        }

        int min = Math.min(attackerUnits.size(), defenderUnits.size());

        int defenderStep = defenderUnits.size() / min;

        int attackerStep = attackerUnits.size() / min;


        Iterator<Unit> defIterator = defenderUnits.iterator();
        Iterator<Unit> attIterator = attackerUnits.iterator();


        while (defIterator.hasNext() || attIterator.hasNext()) {

            for (int i = 0; i < defenderStep; i++) {
                if (defIterator.hasNext()) {
                    Unit b = defIterator.next();

                    double fluke = 0.7 + 0.6 * random.nextDouble();
                    //fluke = 1;
                    hitUnit(b, fluke * fireOnDefender, fluke * chargeOnDefender);
                    System.out.println(b.name + " " + b.nation + " hit, morale = " + b.morale);
                }
            }
            for (int i = 0; i < attackerStep; i++) {
                if (attIterator.hasNext()) {
                    Unit b = attIterator.next();

                    double fluke = 0.7 + 0.6 * random.nextDouble();
                    //fluke = 1;
                    hitUnit(b, fluke * fireOnAttacker, fluke * chargeOnAttacker);
                    System.out.println(b.name + " " + b.nation + " hit, morale = " + b.morale);
                }

            }
            if (rootedDef.size() > 0) {

                for (Unit unit : rootedDef) {
                    if (unit.isSub) unit.superForce.detach(unit);

                }
                if (defender.forces.size() < defenderInit * 0.4) {
                    winner = 1;
                    return result.toString();
                }
            }
            if (rootedAtt.size() > 0) {

                for (Unit unit : rootedAtt) {
                    if (unit.isSub) unit.superForce.detach(unit);

                }
                if (attacker.forces.size() < attackerInit * 0.4) winner = -1;
            }

            if (winner != 0) break;

        }
        Test.list(attacker);
        Test.list(defender);


        return result.toString();
    }

    public Unit selectRandomUnit(Force force) {
        if (force.isUnit) return (Unit) force;

        Random random = new Random();
        ArrayList<Unit> units = new ArrayList<>();
        units.addAll(force.battalions);
        units.addAll(force.squadrons);
        units.addAll(force.batteries);
        int index = random.nextInt(units.size());
        return units.get(index);
    }
}


