# TP-1 — Injection des Dépendances en Java

> **Objectif :** Comprendre et mettre en œuvre le principe d'**Injection des Dépendances (DI)** en Java, en partant d'un couplage fort vers un couplage faible, jusqu'à une instanciation dynamique pilotée par un fichier de configuration.

---

## Table des matières

1. [Introduction — Qu'est-ce que l'injection des dépendances ?](#1-introduction)
2. [Problème : Le Couplage Fort](#2-problème--le-couplage-fort)
3. [Solution : Le Couplage Faible](#3-solution--le-couplage-faible)
4. [Avantages du couplage faible](#4-avantages-du-couplage-faible)
5. [Vers une application de très haute qualité — Instanciation Dynamique](#5-vers-une-application-de-très-haute-qualité--instanciation-dynamique)
6. [Structure du projet](#6-structure-du-projet)
7. [Diagramme de classes](#7-diagramme-de-classes)
8. [Technologies utilisées](#8-technologies-utilisées)

---

## 1. Introduction

L'**injection des dépendances** (Dependency Injection — DI) est un **patron de conception** (design pattern) fondamental en programmation orientée objet. Il repose sur un principe simple :

> **Une classe ne doit pas créer elle-même ses dépendances. Elle doit les recevoir de l'extérieur.**

En d'autres termes, au lieu qu'un objet instancie directement les objets dont il a besoin, ces objets lui sont *injectés* — via le constructeur, un setter, ou un fichier de configuration.

Ce principe est au cœur de frameworks modernes tels que **Spring**, **Jakarta EE** ou **Quarkus**, et il est indispensable pour construire des applications :

- **Modulaires** — les composants sont interchangeables
- **Testables** — on peut substituer les vraies implémentations par des mocks
- **Maintenables** — une modification n'entraîne pas de cascade de changements
- **Extensibles** — on peut ajouter de nouvelles implémentations sans toucher au code existant

---

## 2. Problème : Le Couplage Fort

### Qu'est-ce que le couplage fort ?

On parle de **couplage fort** lorsqu'une classe dépend directement d'une autre classe concrète. La classe dépendante connaît et instancie elle-même ses dépendances. Ce type de conception rigidifie le code et rend l'application **fermée à l'extension, mais ouverte aux modifications**.

### Exemple — Couplage fort

Supposons une application qui calcule une valeur en récupérant une donnée depuis une base de données.

**Classe `DaoImpl` (couche d'accès aux données) :**

```java
// dao/DaoImpl.java
package dao;

public class daoImpl implements IDao {
    @Override
    public double getDta() {
        System.out.println("version 1");
        double temp = 15;
        return temp;

    }

    public daoImpl() {
    }
}

```

**Classe `MetierImpl` (couche métier) — couplage fort :**

```java
// metier/MetierImpl.java
package metier;

import dao.IDao;

public class MetierImpl implements IMetier {
    private IDao dao;

    public MetierImpl() {
    }

    public MetierImpl(IDao dao) {
        this.dao = dao;
    }
    @Override
    public double calcul() {
        double t = 2 * dao.getDta();
        return t;
    }

    public void setDao(IDao dao) {
        this.dao = dao;
    }
}

```

**Classe `Presentation` (point d'entrée) :**

```java
// pres/Pres1.java
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
```

### ❌ Inconvénients du couplage fort

| Problème | Description |
|---|---|
| **Rigidité** | Pour changer l'implémentation DAO (ex. : passer d'une base de données à un web service), il faut **modifier le code source** de `MetierImpl`. |
| **Violation de l'Open/Closed Principle** | L'application est **ouverte aux modifications** et **fermée aux extensions** — exactement l'inverse de ce que l'on souhaite. |
| **Testabilité faible** | Impossible de substituer `DaoImpl` par un mock sans modifier la classe `MetierImpl`. |
| **Dépendance circulaire potentielle** | Si les classes évoluent indépendamment, les modifications en cascade se multiplient. |

> ⚠️ En particulier : si demain on souhaite utiliser un **web service** au lieu d'une base de données, on est **obligé de modifier** `MetierImpl` — une classe qui n'est pas censée savoir d'où viennent les données. C'est une violation directe du **principe de responsabilité unique (SRP)**.

---

## 3. Solution : Le Couplage Faible

### Principe

La solution consiste à introduire une **interface** entre la couche métier et la couche d'accès aux données. La classe `MetierImpl` ne dépend plus d'une implémentation concrète, mais d'une **abstraction**. La dépendance concrète lui est **injectée via le constructeur**.

### Mise en œuvre

**Interface `IDao` :**

```java
// dao/IDao.java
package dao;

public interface IDao {
    double getDta();

}
```

**`DaoImpl` — version 1 :**

```java
// dao/DaoImpl.java
package dao;

public class daoImpl implements IDao {
    @Override
    public double getDta() {
        System.out.println("version 1");
        double temp = 15;
        return temp;

    }

    public daoImpl() {
    }
}

```

**`DaoImplV2` — version 2 :**

```java
// extentions/DaoImplV2.java
package extension;

import dao.IDao;

public class daoImplV2 implements IDao {
    public daoImplV2() {
    }

    @Override
    public double getDta() {
        System.out.println("version 2");
        return 32;
    }
}

```

**Interface `IMetier` :**

```java
// metier/IMetier.java
package metier;

public interface IMetier {
    double calcul();
}
```

**`MetierImpl` — avec injection (couplage faible) :**

```java
// metier/MetierImpl.java
package metier;

import dao.IDao;

public class MetierImpl implements IMetier {
    private IDao dao;

    public MetierImpl() {
    }

    public MetierImpl(IDao dao) {
        this.dao = dao;
    }
    @Override
    public double calcul() {
        double t = 2 * dao.getDta();
        return t;
    }

    public void setDao(IDao dao) {
        this.dao = dao;
    }
}
```

**`Presentation1` — Instanciation statique :**

```java
// pres/Pres1.java
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
```

Ici, pour utiliser `DaoImplV2` à la place de `DaoImpl`, **il suffit de changer une ligne dans `Pres1`** — la couche métier reste intacte.

---

## 4. Avantages du couplage faible

| Avantage | Description |
|---|---|
| ✅ **Open/Closed Principle** | L'application est **ouverte à l'extension** (nouvelles implémentations) et **fermée à la modification** (code existant inchangé). |
| ✅ **Interchangeabilité** | On peut passer de `DaoImpl` à `DaoImplV2` sans toucher à la logique métier. |
| ✅ **Testabilité** | On peut injecter un mock ou un stub lors des tests unitaires. |
| ✅ **Séparation des responsabilités** | Chaque couche (DAO, Métier, Présentation) est indépendante et focalisée sur son rôle. |
| ✅ **Maintenabilité** | Les modifications restent localisées et ne provoquent pas d'effets de bord. |

---

## 5. Vers une application de très haute qualité — Instanciation Dynamique

### Le problème qui reste

Même avec le couplage faible, la **classe `Pres1` (main)** doit encore être modifiée pour changer d'implémentation. Cela viole toujours partiellement le principe d'**ouverture/fermeture** — ici appliqué au point d'entrée de l'application.

> 💡 Pour une application de **très haute qualité**, la classe `main` ne devrait **jamais être ouverte à la modification** lors d'un changement d'implémentation.

### Solution : Instanciation dynamique via `config.txt`

On externalise le choix des implémentations dans un **fichier de configuration** texte. La classe `main` lit ce fichier et instancie dynamiquement les classes grâce à la **réflexion Java**.

**Fichier `config.txt` :**

```
dao.DaoImpl
metier.MetierImpl
```

**`Presentation2` — Instanciation dynamique :**

```java
// pres/Pres2.java
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
```

### ✅ Résultat

Pour passer à une implémentation web service, il suffit de **modifier `config.txt`** :

```
extention.DaoImplV2
metier.MetierImpl
```

**Aucune ligne de code Java n'est modifiée.** L'application est désormais :

- **Fermée à la modification** du code source
- **Ouverte à l'extension** via la configuration
- **Entièrement pilotée par des fichiers externes**

---

## 6. Structure du projet

```
TPIOC/
└── src/
    └── main/
        └── java/
            ├── dao/
            │   ├── IDao.java
            │   └── DaoImpl.java
            ├── extention/
            │   └── DaoImplV2.java
            ├── metier/
            │   ├── IMetier.java
            │   └── MetierImpl.java
            └── pres/
                ├── Pres1.java
                └── Pres2.java
config.txt
```
---

## 7. Diagramme de classes

> 📐 **Diagramme  de classes :**

![Diagramme  de classes](https://github.com/Fahdkrachel/TP-1-java-injectionsDependances-/blob/master/DiagramClass.jpeg)
```


```

**Résumé des relations :**

- `Pres1` / `Pres2` dépendent de `IDao` et `IMetier` (interfaces)
- `MetierImpl` implémente `IMetier` et dépend de `IDao` (couplage faible)
- `DaoImpl` et `DaoImplV2` implémentent `IDao`
- La couche métier ne connaît aucune implémentation concrète de DAO

---

## 8. Technologies utilisées

- **Java** (JDK 8+)
- **Réflexion Java** (`Class.forName`, `newInstance`, `getConstructor`)
- **Principes SOLID** (Open/Closed, Single Responsibility, Dependency Inversion)
- **Patron de conception** : Dependency Injection (DI)

---

> **Auteur :** *KRACHEL fahd*  
> **Module :** Architecture JEE 
> **Année :** 2025–2026
