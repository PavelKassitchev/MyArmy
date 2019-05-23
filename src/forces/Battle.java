package forces;

import java.util.ArrayList;
import java.util.Random;

public class Battle {
    Force attacker;
    Force defender;
    int winner = 0;
    ArrayList<Unit> rootedDef = new ArrayList<>();
    ArrayList<Unit> rootedAtt = new ArrayList<>();

    public Battle(Force attacker, Force defender) {
        this.attacker = attacker;
        this.defender = defender;

    }

    public void resolve() {
        String s;
        while (winner == 0) {
            s = resolveStage();
            System.out.println(s);
        }
        System.out.println("Defender rooted: ");
        for (Unit unit : rootedDef) Test.list(unit);
        System.out.println("Attacker rooted: ");
        for (Unit unit : rootedAtt) Test.list(unit);
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

            b.bearLoss(fluke * fireEffectDef);
            b.changeMorale(fluke * moraleEffectDef);
            if (b.strength <= 0 || b.morale <= 0) {
                rootedDef.add(b);
                if (!b.isSub || defender.forces.size() < 2) {
                    winner = 1;
                    result.append("Attacker!");
                    return result.toString();
                } else {
                    for (Force force : b.superForce.forces) {
                        if (force.isUnit && force != b) {
                            ((Unit) force).changeMorale(-0.1);
                            if (force.morale <= 0) rootedDef.add((Unit)force);
                        }
                    }
                }

            }
        }
        for (Squadron b : defender.squadrons) {
            double fluke = 0.8 + 0.4 * random.nextDouble();

            b.bearLoss(fluke * fireEffectDef);
            b.changeMorale(fluke * moraleEffectDef);
            if (b.strength <= 0 || b.morale <= 0) {
                rootedDef.add(b);
                if (!b.isSub || defender.forces.size() < 2) {
                    winner = 1;
                    result.append("Attacker!");
                    return result.toString();
                } else {
                    for (Force force : b.superForce.forces) {
                        if (force.isUnit && force != b) {
                            ((Unit) force).changeMorale(-0.1);
                            if (force.morale <= 0) rootedDef.add((Unit)force);
                        }
                    }
                }

            }
        }
        for (Battery b : defender.batteries) {
            double fluke = 0.8 + 0.4 * random.nextDouble();

            b.bearLoss(fluke * fireEffectDef);
            b.changeMorale(fluke * moraleEffectDef);
            if (b.strength <= 0 || b.morale <= 0) {
                rootedDef.add(b);
                if (!b.isSub || defender.forces.size() < 2) {
                    winner = 1;
                    result.append("Attacker!");
                    return result.toString();
                } else {
                    for (Force force : b.superForce.forces) {
                        if (force.isUnit && force != b) {
                            ((Unit) force).changeMorale(-0.1);
                            if (force.morale <= 0) rootedDef.add((Unit)force);
                        }
                    }
                }

            }
        }

        for (Battalion b : attacker.battalions) {
            double fluke = 0.8 + 0.4 * random.nextDouble();

            b.bearLoss(fluke * fireEffectAtt);
            b.changeMorale(fluke * moraleEffectAtt);
            if (b.strength <= 0 || b.morale <= 0) {
                rootedAtt.add(b);
                if (!b.isSub || attacker.forces.size() < 2) {
                    winner = -1;
                    result.append("Defender!");
                    return result.toString();
                } else {
                    for (Force force : b.superForce.forces) {
                        if (force.isUnit && force != b) {
                            ((Unit) force).changeMorale(-0.1);
                            if (force.morale <= 0) rootedAtt.add((Unit)force);
                        }
                    }
                }

            }
        }
        for (Squadron b : attacker.squadrons) {
            double fluke = 0.8 + 0.4 * random.nextDouble();

            b.bearLoss(fluke * fireEffectAtt);
            b.changeMorale(fluke * moraleEffectAtt);
            if (b.strength <= 0 || b.morale <= 0) {
                rootedAtt.add(b);
                if (!b.isSub || attacker.forces.size() < 2) {
                    winner = -1;
                    result.append("Defender!");
                    return result.toString();
                } else {
                    for (Force force : b.superForce.forces) {
                        if (force.isUnit && force != b) {
                            ((Unit) force).changeMorale(-0.1);
                            if (force.morale <= 0) rootedAtt.add((Unit)force);
                        }
                    }
                }

            }
            ;
        }
        for (Battery b : attacker.batteries) {
            double fluke = 0.8 + 0.4 * random.nextDouble();

            b.bearLoss(fluke * fireEffectAtt);
            b.changeMorale(fluke * moraleEffectAtt);
            if (b.strength <= 0 || b.morale <= 0) {
                rootedAtt.add(b);
                if (!b.isSub || attacker.forces.size() < 2) {
                    winner = -1;
                    result.append("Defender!");
                    return result.toString();
                } else {
                    for (Force force : b.superForce.forces) {
                        if (force.isUnit && force != b) {
                            ((Unit) force).changeMorale(-0.1);
                            if (force.morale <= 0) rootedAtt.add((Unit)force);
                        }
                    }
                }

            }
            ;
        }
        if (rootedDef.size() > 0) {
            for (Unit unit : rootedDef) {
                if (unit.isSub) unit.superForce.detach(unit);

            }
        }
        if (rootedAtt.size() > 0) {
            for (Unit unit : rootedAtt) {
                if (unit.isSub) unit.superForce.detach(unit);

            }
        }
        System.out.println(result.toString());
        System.out.println("WINNER = " + winner);
        return result.toString();
    }
}


