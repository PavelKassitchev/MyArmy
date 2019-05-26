package forces;
import java.awt.*;

import static forces.Nation.*;

public class Test {

    static Hex hex = new Hex();
    public static void main(String[] args) {

        Force france = createForce(FRANCE,5, 3, 0);
        Force austria = createForce(AUSTRIA, 7, 0, 0);
        Force f = new Force(new Squadron(FRANCE, hex), new Squadron(FRANCE, hex));
        //france.attach(f);
        System.out.println("Before the battle");
        System.out.println();
        list(france);
        list(austria);
        System.out.println();
        Battle battle = new Battle(france, austria);
        getStat(france, austria);
        //battle.resolve();
        //battle.resolveStage();
        //battle.resolveStage();
        //battle.resolveStage();
        //battle.resolveStage();
        //battle.resolveStage();
        System.out.println("After the battle");
        System.out.println();
        list(france);
        list(austria);
    }

    public static void getStat(Force attacker, Force defender) {
        int a = 0;
        int d = 0;
        for (int i = 0; i < 1000; i++) {
            Force att = createForce(attacker.nation, attacker.battalions.size(), attacker.squadrons.size(), attacker.batteries.size(), attacker.morale);
            Force def = createForce(defender.nation, defender.battalions.size(), defender.squadrons.size(), defender.batteries.size(), defender.morale);

            Battle battle = new Battle(att, def);
            int r = battle.resolve();

            if (r == 1) a++;
            else d++;
        }

        System.out.println("Attacker wins = " + a + " Defender wins = " + d);
    }

    static void list(Force force) {
        System.out.println(force.name);
        System.out.println("Totally soldiers: " + force.strength + ", Morale level: " + force.morale + " speed: " + force.speed +
                " FOOD: " + force.foodStock +" foodNeed: "+ force.foodNeed + " foodLimit " + force.foodLimit + " fire : " + force.fire + " charge: " +
                force.charge);
        System.out.println("Including: ");
        System.out.println();
        for (Force f: force.forces) {
            if (f.isUnit) {
                System.out.println("    " + f.name + ": " + f.strength + " soldiers, Morale level: " + f.morale + " speed: " + f.speed +
                        " FOOD: " + f.foodStock + " Food Need " + f.foodNeed + " foodlimit " + f.foodLimit + " fire: " + f.fire + " charge: " + f.charge);
            }
            else {
                list(f);
            }
        }
        System.out.println();
    }

    private static Force createForce(Nation nation, int i, int c, int a) {
        int b = 1;
        int s = 1;
        int y = 1;
        Force force = new Force(nation, hex);
        force.name = "Corps " + nation;
        for (int count = 0; count < i; count++) {
            Unit u = new Battalion(nation, hex);
            u.name = b++ +". Battalion";

            force.attach(u);
        }
        for (int count = 0; count < c; count++) {
            Unit u =  new Squadron(nation, hex);
            u.name = s++ + ". Squadron";

            force.attach(u);
        }
        for (int count = 0; count < a; count++) {
            Unit u = new Battery(nation, hex);
            u.name = y++ + ". Battery";
            force.attach(u);
        }

        return force;
    }
    private static Force createForce(Nation nation, int i, int c, int a, double m) {
        int b = 1;
        int s = 1;
        int y = 1;
        Force force = new Force(nation, hex);
        force.name = "Corps " + nation;
        for (int count = 0; count < i; count++) {
            Unit u = new Battalion(nation, hex);
            u.name = b++ +". Battalion";
            u.morale = m;
            force.attach(u);
        }
        for (int count = 0; count < c; count++) {
            Unit u =  new Squadron(nation, hex);
            u.name = s++ + ". Squadron";
            u.morale = m;
            force.attach(u);
        }
        for (int count = 0; count < a; count++) {
            Unit u = new Battery(nation, hex);
            u.name = y++ + ". Battery";
            u.morale = m;
            force.attach(u);
        }

        return force;
    }
}
