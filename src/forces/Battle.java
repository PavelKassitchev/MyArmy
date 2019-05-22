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
        double dropFireDef = 0.07 * attacker.fire / defender.fire;
        double dropMoraleDef = 0.07 * attacker.charge / defender.charge + dropFireDef / 6;
        double dropFireAtt = 0.07 * defender.fire / attacker.fire;
        double dropMoraleAtt = 0.07 * defender.charge / attacker.charge + dropFireAtt / 6;

        for (Battalion b: defender.battalions) {
            b.bearLoss(dropFireDef);
            b.changeMorale(-dropMoraleDef);
            if (b.morale <= 0 || b.strength <= 0) {
                result.append("Attacker");
                break;
            }

        }
        for (Squadron b: defender.squadrons) {
            b.bearLoss(dropFireDef);
            b.changeMorale(-dropMoraleDef);
            if (b.morale <= 0 || b.strength <= 0) {
                result.append("Attacker");
                break;
            }
        }
        for (Battery b: defender.batteries) {
            b.bearLoss(dropFireDef);
            b.changeMorale(-dropMoraleDef);
            if (b.morale <= 0 || b.strength <= 0) {
                result.append("Attacker");
                break;
            }
        }

        for (Battalion b: attacker.battalions) {
            b.bearLoss(dropFireAtt);
            b.changeMorale(-dropMoraleAtt);
            if (b.morale <= 0 || b.strength <= 0) {
                result.append("Defender");
                break;

            }

        }
        for (Squadron b: attacker.squadrons) {
            b.bearLoss(dropFireAtt);
            b.changeMorale(-dropMoraleAtt);
            if (b.morale <= 0 || b.strength <= 0) {
                result.append("Defender");
                break;

            }
        }
        for (Battery b: attacker.batteries) {
            b.bearLoss(dropFireAtt);
            b.changeMorale(-dropMoraleAtt);
            if (b.morale <= 0 || b.strength <= 0) {
                result.append("Defender");
                break;

            }
        }
        System.out.println(result.toString());
        return result.toString();
    }
}


