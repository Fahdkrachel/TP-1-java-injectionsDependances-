package pres;

import dao.IDao;
import metier.IMetier;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Method;
import java.util.Scanner;

public class pres2 {

    public static void main(String[] args) throws Exception {

        // ouvrir fichier config
        Scanner sc = new Scanner(new File("config.txt"));

        // lire 1ere classe (DAO)
        String premClass = sc.nextLine();
        Class<?> daoClass = Class.forName(premClass);

        IDao objetDao = (IDao) daoClass
                .getDeclaredConstructor()
                .newInstance();

        // lire 2eme classe (Metier)
        String deuxClass = sc.nextLine();
        Class<?> metierClass = Class.forName(deuxClass);

        IMetier metier = (IMetier) metierClass
                .getDeclaredConstructor()
                .newInstance();

        // chercher methode setDao
        Method methode = metierClass.getMethod("setDao", IDao.class);

        // injecter la dependance
        methode.invoke(metier, objetDao);

        // chercher methode calcul
        Method methode2 = metierClass.getMethod("calcul");

        // executer calcul
        Object result = methode2.invoke(metier);

        System.out.println("Resultat = " + result);
    }
}