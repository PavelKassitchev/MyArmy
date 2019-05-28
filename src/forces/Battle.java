package forces;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;


public class Battle {
    public static final int FIRE_ON_ATTACKER = 40;
    public static final int FIRE_ON_DEFENDER = 38;
    public static final double CASUALITY_INTO_MORALE = 3.3;
    public static final int CHARGE_ON_ENEMY = 30;
    public static final int PURSUIT_CHARGE = 40;
    public static final int MIN_SOLDIERS = 6;
    public static final double MIN_MORALE = 0.2;
    public static final double MORALE_PENALTY = -0.03;
    public static final double MORALE_BONUS = 0.03;
    public static final double SMALL_MORALE_BONUS = 0.02;
    public static final double VICTORY_BONUS = 0.5;
    public static final double SMALL_VICTORY_BONUS = 0.2;


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
        double prisonnedShare;
        if (winner == 1) {
            if (PURSUIT_CHARGE * attacker.charge > rooted) {
                prisonnedShare = 1;
            } else {
                prisonnedShare = PURSUIT_CHARGE * attacker.charge / rooted;
            }
            for (Unit unit : rootedDef) {
                prisoners += unit.strength * prisonnedShare;
                unit.bearLoss(prisonnedShare);
            }
        }
        if (winner == -1) {
            if (PURSUIT_CHARGE * defender.charge > rooted) {
                prisonnedShare = 1;
            } else {
                prisonnedShare = PURSUIT_CHARGE * defender.charge / rooted;
            }
            for (Unit unit : rootedAtt) {
                prisoners += unit.strength * prisonnedShare;
                unit.bearLoss(prisonnedShare);
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

        int attackerRooted = 0;
        int defenderRooted = 0;


        if (winner == 1) {
            for (Unit unit : rootedDef) defenderRooted += unit.strength;
            System.out.println("Attacker took prisoners: " + pursuit(defenderRooted));
            for (Battalion unit : attacker.battalions) unit.changeMorale(VICTORY_BONUS);
            for (Squadron unit : attacker.squadrons) unit.changeMorale(VICTORY_BONUS);
            for (Battery unit : attacker.batteries) unit.changeMorale(VICTORY_BONUS);
            for (Unit unit : rootedAtt) {
                unit.changeMorale(SMALL_VICTORY_BONUS);
                if (unit.morale > 0) {
                    attacker.attach(unit);

                } else {
                    attackerRooted += unit.strength;
                }
            }

        } else {
            for (Unit unit : rootedAtt) attackerRooted += unit.strength;
            System.out.println("Defender took prisoners: " + pursuit(attackerRooted));
            for (Battalion unit : defender.battalions) unit.changeMorale(VICTORY_BONUS);
            for (Squadron unit : defender.squadrons) unit.changeMorale(VICTORY_BONUS);
            for (Battery unit : defender.batteries) unit.changeMorale(VICTORY_BONUS);
            for (Unit unit : rootedDef) {
                unit.changeMorale(SMALL_VICTORY_BONUS);
                if (unit.morale > 0) {
                    defender.attach(unit);

                } else {
                    defenderRooted += unit.strength;
                }
            }
        }

        System.out.println("ATTACKER: Initial - " + attackerInit + " Casualities - " + (attackerInit - attacker.strength - attackerRooted) +
                " Percentage - " + (attackerInit - attacker.strength - attackerRooted) * 100 / attackerInit + " Rooted - " + attackerRooted);
        System.out.println("DEFENDER: Initial - " + defenderInit + " Casualities - " + (defenderInit - defender.strength - defenderRooted) +
                " Percentage - " + (defenderInit - defender.strength - defenderRooted) * 100 / defenderInit + " Rooted - " + defenderRooted);
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
        if (unit.strength <= MIN_SOLDIERS || unit.morale <= MIN_MORALE) {

            rooted.add(unit);
            opponent.selectRandomUnit().changeMorale(MORALE_BONUS);

            if (unit.isSub) {
                for (Force force : unit.superForce.forces) {
                    if (force.isUnit && force != unit) {
                        ((Unit) force).changeMorale(MORALE_PENALTY);
                        if (force.morale <= MIN_MORALE) rooted.add((Unit) force);
                        opponent.selectRandomUnit().changeMorale(SMALL_MORALE_BONUS);
                    }
                }
            }
        }

    }


    public String resolveStage() {

        double fireOnDefender = FIRE_ON_DEFENDER * attacker.fire / defender.strength;
        System.out.println("Fire on def " + fireOnDefender);

        double fireOnAttacker = FIRE_ON_ATTACKER * defender.fire / attacker.strength;
        System.out.println("Fire on att " + fireOnAttacker);

        double chargeOnDefender = -(CASUALITY_INTO_MORALE * fireOnDefender + CHARGE_ON_ENEMY * attacker.charge / defender.strength);
        System.out.println("Charge on def " + chargeOnDefender);

        double chargeOnAttacker = -(CASUALITY_INTO_MORALE * fireOnAttacker + CHARGE_ON_ENEMY * defender.charge / attacker.strength);
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
            if (rootedDef.size() > 0) {

                for (Unit unit : rootedDef) {
                    if (unit.isSub) unit.superForce.detach(unit);

                }
                if (defender.strength < defenderInit * 0.4) {
                    winner = 1;

                    return result.toString();
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

            if (rootedAtt.size() > 0) {

                for (Unit unit : rootedAtt) {
                    if (unit.isSub) unit.superForce.detach(unit);

                }
                if (attacker.strength < attackerInit * 0.4) {
                    winner = -1;

                }
            }

            if (winner != 0) break;

        }
        Test.list(attacker);
        Test.list(defender);


        return result.toString();
    }

}


