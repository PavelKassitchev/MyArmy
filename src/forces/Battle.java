package forces;

import java.util.ArrayList;
import java.util.HashSet;
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

    public void resolve() {
        String s;
        while (winner == 0) {
            s = resolveStage();
        }
        int attackerEnd = attacker.strength;
        int defenderEnd = defender.strength;
        int attackerRooted = 0;
        int defenderRooted = 0;
        for (Unit unit : rootedDef) defenderRooted += unit.strength;

        for (Unit unit : rootedAtt) attackerRooted += unit.strength;

        System.out.println("ATTACKER: Initial - " + attackerInit + " Casualities - " + (attackerInit - attackerEnd - attackerRooted) +
                " Rooted - " + attackerRooted);
        System.out.println("DEFENDER: Initial - " + defenderInit + " Casualities - " + (defenderInit - defenderEnd - defenderRooted) +
                " Rooted - " + defenderRooted);
    }

    public void hitDefenderUnit(Unit unit, double fire, double charge) {
        unit.bearLoss(fire);
        unit.changeMorale(charge);
        if (unit.strength <= 0 || unit.morale <= 0) {
            rootedDef.add(unit);
            for (Force force : unit.superForce.forces) {
                if (force.isUnit && force != unit) {
                    ((Unit) force).changeMorale(-0.03);
                    if (force.morale <= 0) rootedDef.add((Unit) force);
                }
            }
        }

    }

    public void hitAttackerUnit(Unit unit, double fire, double charge) {
        unit.bearLoss(fire);
        unit.changeMorale(charge);
        if (unit.strength <= 0 || unit.morale <= 0) {
            rootedAtt.add(unit);
            for (Force force : unit.superForce.forces) {
                if (force.isUnit && force != unit) {
                    ((Unit) force).changeMorale(-0.03);
                    if (force.morale <= 0) rootedAtt.add((Unit) force);
                }
            }
        }

    }

    public String resolveStage() {

        double fireEffectDef = 40 * attacker.fire / defender.strength;
        double fireEffectAtt = 40 * defender.fire / attacker.strength;
        double moraleEffectDef = -(3.3 * fireEffectDef + 30 * attacker.charge / defender.strength);
        double moraleEffectAtt = -(3.3 * fireEffectAtt + 30 * defender.charge / attacker.strength);

        Random random = new Random();

        StringBuilder result = new StringBuilder("Victory of ");


        for (Battalion b : defender.battalions) {
            double fluke = 0.8 + 0.4 * random.nextDouble();
            hitDefenderUnit(b, fluke * fireEffectDef, fluke * moraleEffectDef);

        }
        for (Squadron b : defender.squadrons) {
            double fluke = 0.8 + 0.4 * random.nextDouble();
            hitDefenderUnit(b, fluke * fireEffectDef, fluke * moraleEffectDef);
        }
        for (Battery b : defender.batteries) {
            double fluke = 0.8 + 0.4 * random.nextDouble();
            hitDefenderUnit(b, fluke * fireEffectDef, fluke * moraleEffectDef);

        }

        for (Battalion b : attacker.battalions) {
            double fluke = 0.8 + 0.4 * random.nextDouble();
            hitAttackerUnit(b, fluke * fireEffectAtt, fluke * moraleEffectAtt);

        }
        for (Squadron b : attacker.squadrons) {
            double fluke = 0.8 + 0.4 * random.nextDouble();
            hitAttackerUnit(b, fluke * fireEffectAtt, fluke * moraleEffectAtt);

        }
        for (Battery b : attacker.batteries) {
            double fluke = 0.8 + 0.4 * random.nextDouble();
            hitAttackerUnit(b, fluke * fireEffectAtt, fluke * moraleEffectAtt);

        }
        if (rootedDef.size() > 0) {

            for (Unit unit : rootedDef) {
                if (unit.isSub) unit.superForce.detach(unit);

            }
            if (defender.forces.size() == 0) winner = 1;
        }
        if (rootedAtt.size() > 0) {
            System.out.println("ROOTED ATTACKERS NUMBER: " + rootedAtt.size());
            for (Unit unit : rootedAtt) {
                if (unit.isSub) unit.superForce.detach(unit);

            }
           if (attacker.forces.size() == 0) winner = -1;
        }
        for (Unit unit: rootedDef) {
            Test.list(unit);
        }

        return result.toString();
    }
}


