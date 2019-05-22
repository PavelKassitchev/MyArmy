package forces;

public class Battle {
    Force attacker;
    Force defender;

    public Battle (Force attacker, Force defender) {
        this.attacker = attacker;
        this.defender = defender;
    }

    public String resolveStage() {
        StringBuilder result = new StringBuilder("Victory of ");
        double dropFireDef = 0.08 * attacker.fire / defender.fire;
        double dropMoraleDef = 0.07 * attacker.charge / defender.charge + dropFireDef / 6;
        double dropFireAtt = 0.08 * defender.fire / attacker.fire;
        double dropMoraleAtt = 0.07 * defender.charge / attacker.charge + dropFireAtt / 6;

        for (Battalion b: defender.battalions) {
            b.bearLoss(dropFireDef);
            b.changeMorale(-dropMoraleDef);

        }
        for (Squadron b: defender.squadrons) {
            b.bearLoss(dropFireDef);
            b.changeMorale(-dropMoraleDef);
        }
        for (Battery b: defender.batteries) {
            b.bearLoss(dropFireDef);
            b.changeMorale(-dropMoraleDef);
        }

        for (Battalion b: attacker.battalions) {
            b.bearLoss(dropFireAtt);
            b.changeMorale(-dropMoraleAtt);

        }
        for (Squadron b: attacker.squadrons) {
            b.bearLoss(dropFireAtt);
            b.changeMorale(-dropMoraleAtt);
        }
        for (Battery b: attacker.batteries) {
            b.bearLoss(dropFireAtt);
            b.changeMorale(-dropMoraleAtt);
        }
        return result.toString();
    }
}


