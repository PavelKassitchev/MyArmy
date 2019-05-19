package forces;
import java.awt.*;

import static forces.Nation.*;

public class Test {

    static Hex hex = new Hex();
    public static void main(String[] args) {
        Unit battalion1 = new Battalion(FRANCE, hex);
        battalion1.name = "1st Battalion";
        battalion1.strength = 750;
        battalion1.morale = 1;
        battalion1.foodStock = 0;
        Unit squadron1 = new Squadron(FRANCE, hex);
        squadron1.name = "1st Squadron";
        squadron1.morale = 0.5;
        squadron1.foodStock = 1;
        Unit battalion2 = new Battalion(FRANCE, hex);
        battalion2.name = "2nd Battalion";
        battalion2.morale = 1.1;
        Unit wagon1 = new Wagon(FRANCE, hex);
        wagon1.name = "Wagon 1";
        wagon1.foodStock = 10;

        Force force1 = new Force(battalion1, squadron1, wagon1);
        force1.name = "1st Corps";
        force1.attach(battalion2);

        Unit battalion3 = new Battalion (FRANCE, hex);
        battalion3.name ="3rd Battalion";
        battalion3.strength = 120;
        battalion3.morale = 0.4;
        battalion3.foodStock = 0;
        Unit battery1 = new Battery(FRANCE, hex);
        battery1.name = "1st Battery";
        battery1.morale = 0.3;

        Force force2 = new Force(battalion3, battery1);
        force2.name = "45.Division";

        force1.attach(force2);

        list(force1);

        System.out.println();
        System.out.println("NEW VERSION:");
        System.out.println();

        Unit battery2 = new Battery(FRANCE, hex);
        battery2.name = "Guard Battery";
        battery2.morale = 3;
        force2.attach(battery2);

        list(force1);

        force2.detach(battery2);
        System.out.println("DETACHED!");

        list(force1);

        Unit squadron2 = new Squadron(FRANCE, hex);
        squadron2.name = "Cossaks";
        squadron2.morale = 1;
        Unit wagon2 = new Wagon(FRANCE, hex);
        wagon2.name = "Wagon 2";
        wagon2.foodStock = 45;

        Force force3 = new Force(squadron2, wagon2);
        force3.name = "Special";
        force2.attach(force3);

        System.out.println("DEEP");
        System.out.println();
        list(force1);

        System.out.println("Speed up!");
        force2.detach(battery1);
        System.out.println();
        list(force1);

        System.out.println();
        /*System.out.println("REPLENISHMENT");
        Unit battalion4 = new Battalion(FRANCE, hex);
        battalion4.strength = 90;
        battalion4.foodStock = 1;

        battalion1.getReplenished(battalion4);
        list(force1);*/

        System.out.println();
        System.out.println("UNLOAD!");
        double f = force1.unloadFood();
        System.out.println();
        list(force1);
        System.out.println("Unloaded: " + f);
        System.out.println();
        System.out.println("Load!");
        force1.attach(new Battery(FRANCE,hex));
        force1.distributeFood(560);
        list(force1);



    }

    private static void list(Force force) {
        System.out.println(force.name);
        System.out.println(force.nation);
        System.out.println("Totally soldiers: " + force.strength + ", Morale level: " + force.morale + " speed: " + force.speed +
                " FOOD: " + force.foodStock +" foodNeed: "+ force.foodNeed + " foodLimit " + force.foodLimit);
        System.out.println("Including: ");
        System.out.println();
        for (Force f: force.forces) {
            if (f.isUnit) {
                System.out.println("    " + f.name + ": " + f.strength + " soldiers, Morale level: " + f.morale + " speed: " + f.speed +
                        " FOOD: " + f.foodStock + " Food Need " + f.foodNeed + " foodlimit " + f.foodLimit);
            }
            else {
                list(f);
            }
        }
    }
}
