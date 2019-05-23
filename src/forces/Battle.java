package forces;

public class Battle {
    Force attacker;
    Force defender;

    public Battle (Force attacker, Force defender) {
        this.attacker = attacker;
        this.defender = defender;
    }

    public String resolveStage() {
        double fireEffectDef = 40 * attacker.fire / defender.strength;
        double fireEffectAtt = 40 * defender.fire / attacker.strength;
        double moraleEffectDef = - (fireEffectDef + 40 * attacker.charge * attacker.morale / (defender.strength * defender.morale));
        double moraleEffectAtt = - (fireEffectAtt + 40 * defender.charge * defender.morale / (attacker.strength * attacker.morale));
        StringBuilder result = new StringBuilder("Victory of ");

        for (Battalion b: defender.battalions) {
            b.bearLoss(fireEffectDef);
            b.changeMorale(moraleEffectDef);
            if (b.strength <= 0 || b.morale <= 0) result.append("Attacker ");
        }
        for (Squadron b: defender.squadrons) {
            b.bearLoss(fireEffectDef);
            b.changeMorale(moraleEffectDef);
            if (b.strength <= 0 || b.morale <= 0) result.append("Attacker ");
        }
        for (Battery b: defender.batteries) {
            b.bearLoss(fireEffectDef);
            b.changeMorale(moraleEffectDef);
            if (b.strength <= 0 || b.morale <= 0) result.append("Attacker ");
        }

        for (Battalion b: attacker.battalions) {
            b.bearLoss(fireEffectAtt);
            b.changeMorale(moraleEffectAtt);
            if (b.strength <= 0 || b.morale <= 0) result.append("Defender ");
        }
        for (Squadron b: attacker.squadrons) {
            b.bearLoss(fireEffectAtt);
            b.changeMorale(moraleEffectAtt);
            if (b.strength <= 0 || b.morale <= 0) result.append("Defender ");
        }
        for (Battery b: attacker.batteries) {
            b.bearLoss(fireEffectAtt);
            b.changeMorale(moraleEffectAtt);
            if (b.strength <= 0 || b.morale <= 0) result.append("Defender ");
        }
        System.out.println(result.toString());
        return result.toString();
    }
}


