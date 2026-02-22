package pres;


import extension.daoImplV2;
import metier.MetierImpl;

public class pres1 {
    public static void main(String[] args) {
        daoImplV2 dao = new daoImplV2();
        MetierImpl metier = new MetierImpl(dao);
        System.out.println("resultat ="+metier.calcul());

    }
}