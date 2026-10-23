# BiblioTech : Java 21, JDBC/MySQL et multithreading

[![CI](https://github.com/blcoussama/JAVA-MAVEN-JDBC-MULTITHREADING/actions/workflows/ci.yml/badge.svg)](https://github.com/blcoussama/JAVA-MAVEN-JDBC-MULTITHREADING/actions/workflows/ci.yml)

Petite application de gestion de bibliothèque en Java (Maven) qui sert de terrain d'exercice : accès MySQL
en JDBC avec des DAO, exception métier, Collections, API Stream, lambdas, héritage et programmation concurrente
(threads, `ExecutorService`, `Semaphore`, `synchronized`, contrainte `UNIQUE`).

Il n'y a pas d'interface graphique : chaque notion est illustrée par un programme de démonstration en ligne
de commande, et vérifiée par des tests JUnit 5.

## Fonctionnalités

- **DAO JDBC** `AuteurDAO` et `LivreDAO` : insertion (identifiant généré récupéré), lecture avec jointure
  livre/auteur, recherche par identifiant (`Optional`), comptage. Requêtes paramétrées (`PreparedStatement`).
- **Service métier** `BibliothequeService` : validations (titre, auteur, catégorie), erreurs SQL traduites en
  `BibliothequeException` / `LivreDejaExistantException` (doublon 1062, clé étrangère 1452).
- **Collections et Streams** : les mêmes traitements écrits deux fois (boucles + `ArrayList`/`HashSet`/`Map`/
  `Comparator`, puis `filter`/`map`/`collect`/`sorted`), y compris un `Collectors.toMap` qui tolère les titres en double.
- **Concurrence** :
  - parallélisme simple, séquentiel contre `Thread` + `join()` contre `ExecutorService` ;
  - consultation limitée à N utilisateurs simultanés par un `Semaphore` équitable ;
  - insertion concurrente du même livre : méthode `synchronized` « vérifier puis insérer », et sans verrou Java,
    où c'est la contrainte `UNIQUE` de MySQL qui ne laisse passer qu'une insertion.

## Stack

Java 21 · Maven (wrapper 3.9.16) · MySQL 8.4 · MySQL Connector/J 26.10 · JUnit 5.14 · Testcontainers 2.0 · Docker Compose · GitHub Actions

## Architecture

```
src/main/java/com/bibliotech/
├── config/     DatabaseConfig : URL, utilisateur, mot de passe lus dans l'environnement
├── dao/        ConnectionFactory, DatabaseConnection, AuteurDAO, LivreDAO
├── exception/  BibliothequeException, LivreDejaExistantException
├── model/      Auteur, Livre, LivreNumerique (héritage), Categorie, StatutEmprunt
├── service/    ActionBibliotheque (interface), BibliothequeService
└── demo/       un programme main() par notion (voir plus bas)
sql/schema.sql  tables auteur et livre (FK, UNIQUE sur le titre, utf8mb4)
sql/seed.sql    données de démonstration (auteurs fictifs)
```

Les DAO reçoivent une `ConnectionFactory` : l'application utilise les variables d'environnement, les tests
d'intégration un MySQL jetable. Chaque opération ouvre puis ferme sa propre connexion (try-with-resources),
si bien qu'aucune connexion JDBC n'est partagée entre threads.

## Schéma de la base

| Table    | Colonnes                                                                  | Contraintes |
|----------|---------------------------------------------------------------------------|-------------|
| `auteur` | `id` (PK, auto), `nom` (obligatoire), `prenom`                            | |
| `livre`  | `id` (PK, auto), `titre`, `categorie` (ENUM des valeurs de `Categorie`), `id_auteur` | `UNIQUE (titre)` insensible à la casse et aux accents, `FOREIGN KEY (id_auteur) → auteur(id)` |

## Installation et lancement

Prérequis : JDK 21 et Docker. Maven n'a pas besoin d'être installé (`./mvnw` le télécharge et vérifie son empreinte SHA-256).

```bash
# 1. Choisir les mots de passe (ou copier un .env, voir « Configuration »)
export BIBLIOTECH_DB_PASSWORD='<mot de passe applicatif>'
export BIBLIOTECH_DB_ROOT_PASSWORD='<mot de passe root du conteneur>'

# 2. Démarrer MySQL 8.4 : sql/schema.sql puis sql/seed.sql sont chargés au premier démarrage
docker compose up -d --wait

# 3. Compiler puis lancer une démonstration
./mvnw -q compile
./mvnw -q exec:java -Dexec.mainClass=com.bibliotech.demo.DemoConnexion
```

Arrêt : `docker compose down` (ajouter `-v` pour supprimer les données).

Sans Docker, créer la base `bibliotech_db` et un utilisateur sur un MySQL 8 existant, exécuter
`sql/schema.sql` puis `sql/seed.sql`, et renseigner `BIBLIOTECH_DB_URL`.

### Démonstrations

| Classe (`com.bibliotech.demo.…`) | Notion illustrée |
|---|---|
| `DemoConnexion` | Connexion, version du serveur, contenu de la base |
| `DemoDAO` | Insertion d'un auteur et de livres, lecture avec jointure, recherche par ID |
| `DemoCollections` | `List`, `Set`, `Map`, tri avec `Comparator` |
| `DemoStreams` | `filter`, `map`, `toSet`, `toMap`, `sorted`, chaînage |
| `DemoLambda` | `removeIf` avec une lambda |
| `DemoExceptions` | Validations métier, erreurs SQL encapsulées (FK, UNIQUE) |
| `DemoHeritage` | `LivreNumerique` utilisé comme un `Livre` (polymorphisme) |
| `DemoParallelisme` | Séquentiel, `Thread` + `join()`, `ExecutorService` |
| `DemoSemaphore` | 5 utilisateurs, 2 consultations simultanées au maximum |
| `DemoInsertionConcurrente` | Même livre inséré par 5 threads : `synchronized` puis contrainte `UNIQUE` |

Chaque classe se lance avec `./mvnw -q exec:java -Dexec.mainClass=com.bibliotech.demo.<Classe>`.

### Exemple de sortie réelle

Sorties obtenues le 23/10/2026 sur MySQL 8.4.13 (conteneur Docker, données de `sql/seed.sql`). Les durées
dépendent de la machine.

`DemoParallelisme` (chaque traitement simule 2 s de travail) :

```
Durée séquentielle : 4299 ms
Durée avec deux threads : 2313 ms
Durée avec ExecutorService : 2114 ms
```

`DemoSemaphore` (extrait) :

```
Contrainte : 2 consultations simultanées au maximum.
[utilisateur-3] accès accordé (1/2 consultations en cours)
[utilisateur-1] accès accordé (2/2 consultations en cours)
[utilisateur-1] libère son accès.
[utilisateur-5] accès accordé (2/2 consultations en cours)
...
Durée totale : 6767 ms
Consultations simultanées observées au maximum : 2 (limite : 2)
Permis disponibles à la fin : 2 (aucun permis perdu ni rendu en trop)
```

`DemoInsertionConcurrente` (titres suffixés par l'horodatage de l'exécution) :

```
=== 1. Méthode synchronized (vérifier puis insérer) ===
  Utilisateur-1 : insertion réussie (ID 15)
  Utilisateur-2 : refusé -> Le livre 'L'Avare (synchronized) #1792752583408' existe déjà (détecté par la vérification Java).
  ...
  -> 1 insertion réussie sur 5 tentatives simultanées.

=== 2. Sans verrou Java : contrainte UNIQUE de la base ===
  Utilisateur-1 : refusé -> Le livre 'L'Avare (UNIQUE) #1792752583408' existe déjà (refusé par la contrainte UNIQUE de la base).
  ...
  Utilisateur-5 : insertion réussie (ID 16)
  -> 1 insertion réussie sur 5 tentatives simultanées.
```

`DemoExceptions` :

```
1. Livre sans titre : refusé -> Erreur métier : le titre ne peut pas être vide.
2. Auteur sans nom : refusé -> Erreur métier : le nom de l'auteur est obligatoire.
3. Recherche de l'auteur ID 9999 : refusé -> Aucun auteur trouvé avec l'ID : 9999
4. Livre rattaché à un auteur inexistant : refusé -> L'auteur ID 9999 n'existe pas en base.
     cause technique : Cannot add or update a child row: a foreign key constraint fails (...)
5. Titre déjà présent ('Le Guide du Java') : refusé -> Le livre 'Le Guide du Java' existe déjà (refusé par la contrainte UNIQUE de la base).
     cause technique : Duplicate entry 'Le Guide du Java' for key 'livre.uq_livre_titre'
```

## Configuration

| Variable | Rôle | Défaut |
|---|---|---|
| `BIBLIOTECH_DB_PASSWORD` | Mot de passe de l'utilisateur MySQL de l'application | **obligatoire** (aucun défaut) |
| `BIBLIOTECH_DB_USER` | Utilisateur MySQL | `bibliotech` |
| `BIBLIOTECH_DB_URL` | URL JDBC | `jdbc:mysql://localhost:3307/bibliotech_db` |
| `BIBLIOTECH_DB_ROOT_PASSWORD` | Mot de passe root du conteneur (Docker Compose uniquement) | **obligatoire** pour `docker compose` |
| `BIBLIOTECH_DB_PORT` | Port publié sur `127.0.0.1` par Docker Compose | `3307` |

Docker Compose lit aussi un fichier `.env` à la racine (ignoré par git). Pour l'application Java, exporter
les variables (par exemple `set -a; . ./.env; set +a`). Sans mot de passe, l'application s'arrête avec un message explicite.

## Tests

```bash
./mvnw verify      # tests unitaires + tests d'intégration (Docker requis)
./mvnw test        # tests unitaires seuls, sans base de données
```

- **Tests unitaires (21)** : configuration (mot de passe obligatoire et jamais affiché), traitements
  Collections/Streams/lambdas (doublons, liste vide, tris), validations métier, et sémaphore avec une doublure
  de DAO bloquante : jamais plus de N consultations simultanées, et une attente interrompue ne rend pas un
  permis qu'elle n'a pas obtenu (ce test échoue si `acquire()` est replacé dans le `try`).
- **Tests d'intégration (11)** : MySQL 8.4 jetable lancé par Testcontainers et initialisé avec `sql/schema.sql`.
  DAO (jointure, UTF-8, contrainte UNIQUE, clé étrangère, valeur « piège » stockée telle quelle), et 8 threads
  qui insèrent le même livre au même signal (`CountDownLatch`) : exactement une insertion réussit, avec ou sans `synchronized`.

Résultat local (23/10/2026) : `Tests run: 21, Failures: 0` (Surefire) puis `Tests run: 11, Failures: 0` (Failsafe), `BUILD SUCCESS`.

La CI GitHub Actions exécute `./mvnw -B verify` sous Java 21 et un audit des dépendances avec OSV-Scanner.

## Sécurité

- Aucun identifiant dans le code : configuration par variables d'environnement, mot de passe masqué dans `toString()`.
  Un ancien mot de passe MySQL a été retiré de tout l'historique git.
- Requêtes paramétrées uniquement (`PreparedStatement`).
- Pilote à jour `com.mysql:mysql-connector-j` 26.10.0, à la place de `mysql:mysql-connector-java` 8.0.33
  (relocalisé, concerné par CVE-2023-22102). OSV-Scanner ne signale aucune vulnérabilité connue (23/10/2026).
- Versions de tous les plugins Maven épinglées (vérifié par `maven-enforcer-plugin`) ; MySQL Docker publié sur `127.0.0.1` uniquement.

## Limites connues

- Pas d'interface utilisateur : uniquement des programmes de démonstration en console.
- Pas de pool de connexions : chaque opération ouvre une connexion, ce qui suffit pour ces démonstrations.
- `StatutEmprunt` n'est pas persisté et la taille d'un `LivreNumerique` n'est pas enregistrée en base.
- Les pauses de 2 s des démonstrations de concurrence sont volontaires (rendre le parallélisme visible).

## Origine du projet

Projet d'apprentissage réalisé dans le cadre d'un module de Java avancé (travaux dirigés : JDBC, Collections,
Streams, multithreading).

### Ce que j'ai ajouté / corrigé

- Retrait du mot de passe MySQL écrit en dur (et de l'historique git), configuration par variables d'environnement.
- Sémaphore : `acquire()` sorti du `try` (un permis non obtenu était rendu, ce qui augmentait la capacité),
  `InterruptedException` propagée au lieu d'être avalée, commentaires alignés sur le code (permis, durées).
- Démonstration `synchronized` réellement exécutée et contrainte `UNIQUE (titre)` ajoutée en base.
- `Collectors.toMap` qui plantait sur un titre en double, `get(0)` sur une liste vide, tris rendus cohérents.
- Passage à Java 21 (le `pom.xml` visait Java 23), nouveau pilote MySQL, plugins épinglés, Maven Wrapper.
- `sql/schema.sql`, `sql/seed.sql`, `docker-compose.yml`, tests JUnit 5 (unitaires + Testcontainers), CI GitHub Actions, ce README.
- Commentaires réécrits en commentaires techniques ; démonstrations regroupées dans le paquet `demo`.
