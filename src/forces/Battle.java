package forces;

public class Battle {
    Force attacker;
    Force defender;

    public Battle (Force attacker, Force defender) {
        this.attacker = attacker;
        this.defender = defender;
    }

    public String resolve() {
        StringBuilder result = new StringBuilder("Victory of ");

        for (Battalion b: defender.battalions) {
            b.bearLoss(0.1 * attacker.fire / defender.fire);
        }
        for (Squadron b: defender.squadrons) {
            b.bearLoss(0.1 * attacker.fire / defender.fire);
        }
        for (Battery b: defender.batteries) {
            b.bearLoss(0.1 * attacker.fire / defender.fire);
        }
        return result.toString();
    }
}


