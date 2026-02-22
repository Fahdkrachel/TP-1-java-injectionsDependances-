package pres;


import dao.daoImpl;
import metier.MetierImpl;

public class pres1 {
    public static void main(String[] args) {
        daoImpl dao = new daoImpl();
        MetierImpl metier = new MetierImpl(dao);
        System.out.println("resultat ="+metier.calcul());

    }
}