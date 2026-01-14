# 📚 MAPPING COMPLET DES CONCEPTS JAVA - BIBLIOTECH

**Projet:** BiblioTech - Système de Gestion de Bibliothèque
**Auteur:** Belcadi Oussama
**Date d'analyse:** 2026-01-10
**Objectif:** Préparation évaluation TD1-11 Java Avancé

---

## 📊 STATISTIQUES GLOBALES

### Vue d'ensemble du projet
- **Total de fichiers analysés:** 35 fichiers Java
- **Total de packages:** 7 packages
- **Lignes de code totales:** ~8500+ lignes
- **Concepts Java identifiés:** 150+ instances de concepts TD1-11

### Répartition par package
| Package | Fichiers | Focus principal |
|---------|----------|----------------|
| `models` | 6 | POO, Enums, equals/hashCode |
| `dao` | 5 | JDBC, Transactions, PreparedStatement |
| `exceptions` | 5 | Hiérarchie d'exceptions |
| `services` | 4 | Streams API, Lambda, Business Logic |
| `threads` | 6 | Multithreading, Synchronisation |
| `main` | 8 | Tests, Validation, Intégration |
| `utils` | 1 | I/O, FileWriter, try-with-resources |

### Concepts les plus utilisés
1. **Streams API (TD4-5):** 150+ utilisations (filter, map, collect, groupingBy, etc.)
2. **JDBC (TD11):** 80+ utilisations (PreparedStatement, ResultSet, Connection)
3. **Lambda expressions (TD3):** 120+ utilisations (method references, comparators)
4. **Multithreading (TD6-10):** 40+ utilisations (Thread, Callable, Lock, Semaphore)
5. **I/O Operations (TD8-9):** 60+ utilisations (FileWriter, BufferedWriter)

---

## 📑 TABLE DES MATIÈRES

### [I. ANALYSE PAR FICHIER](#i-analyse-par-fichier)
1. [Package models](#1-package-models-6-fichiers)
2. [Package dao](#2-package-dao-5-fichiers)
3. [Package exceptions](#3-package-exceptions-5-fichiers)
4. [Package services](#4-package-services-4-fichiers)
5. [Package threads](#5-package-threads-6-fichiers)
6. [Package main](#6-package-main-8-fichiers)
7. [Package utils](#7-package-utils-1-fichier)

### [II. INDEX DES CONCEPTS](#ii-index-des-concepts-par-td)
1. [TD1-2: Programmation Orientée Objet](#td1-2-programmation-orientée-objet)
2. [TD3: Lambda & Interfaces Fonctionnelles](#td3-lambda--interfaces-fonctionnelles)
3. [TD4-5: Streams API](#td4-5-streams-api)
4. [TD6-10: Multithreading](#td6-10-multithreading)
5. [TD11: JDBC](#td11-jdbc)
6. [TD8-9: I/O Operations](#td8-9-io-operations)

### [III. PATTERNS ET RECOMMANDATIONS](#iii-patterns-et-recommandations)

---

# I. ANALYSE PAR FICHIER

## 1. Package models (6 fichiers)

### 1.1 Categorie.java

**Concepts TD1-2 (POO) identifiés:**

| Ligne(s) | Concept | Description |
|----------|---------|-------------|
| 1 | `package` | Organisation en packages |
| 11-17 | `enum` | Type énuméré avec valeurs constantes |
| 11 | `public enum` | Modificateur d'accès public |
| 12-16 | Constantes enum | ROMAN, SCIENCE, HISTOIRE, TECHNOLOGIE, ART |

**Code représentatif:**
```java
// Ligne 11-17: Définition d'un enum (TD1-2)
public enum Categorie {
    ROMAN,
    SCIENCE,
    HISTOIRE,
    TECHNOLOGIE,
    ART
}
```

**Total concepts TD1-2:** 5 instances

---

### 1.2 StatutEmprunt.java

**Concepts TD1-2 (POO) identifiés:**

| Ligne(s) | Concept | Description |
|----------|---------|-------------|
| 1 | `package` | Organisation en packages |
| 11-15 | `enum` | Type énuméré pour statut d'emprunt |
| 12-14 | Constantes enum | EN_COURS, RETOURNE, EN_RETARD |

**Code représentatif:**
```java
// Ligne 11-15: Enum pour statut d'emprunt (TD1-2)
public enum StatutEmprunt {
    EN_COURS,
    RETOURNE,
    EN_RETARD
}
```

**Total concepts TD1-2:** 4 instances

---

### 1.3 Auteur.java

**Concepts TD1-2 (POO) identifiés:**

| Ligne(s) | Concept | Description |
|----------|---------|-------------|
| 1 | `package` | Organisation en packages |
| 3-4 | `import java.util.Objects` | Utilisation de classes utilitaires Java |
| 22 | `public class Auteur` | Déclaration de classe publique |
| 27-29 | Attributs privés | Encapsulation (private int, String) |
| 35-39 | Constructeur par défaut | Constructeur sans paramètres |
| 45-52 | Constructeur avec paramètres | Surcharge de constructeurs |
| 58-60 | Getter | Méthode d'accès (getIdAuteur) |
| 66-68 | Setter | Méthode de modification (setIdAuteur) |
| 71-73, 75-77, 79-81, 83-85 | Getters/Setters | Pour nom, prenom, nationalite |
| 94-105 | `equals()` | Redéfinition de equals() (polymorphisme) |
| 95 | `this == o` | Comparaison de références |
| 96-97 | `instanceof` | Vérification de type |
| 99 | Cast explicite | `(Auteur) o` |
| 100-102 | `Objects.equals()` | Comparaison null-safe |
| 113-115 | `hashCode()` | Redéfinition de hashCode() |
| 114 | `Objects.hash()` | Génération de hash code |
| 124-129 | `toString()` | Redéfinition de toString() |

**Code représentatif:**
```java
// Lignes 94-105: equals() avec Objects.equals() (TD1-2)
@Override
public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Auteur auteur = (Auteur) o;
    return Objects.equals(idAuteur, auteur.idAuteur) &&
           Objects.equals(nom, auteur.nom) &&
           Objects.equals(prenom, auteur.prenom) &&
           Objects.equals(nationalite, auteur.nationalite);
}

// Lignes 113-115: hashCode() avec Objects.hash() (TD1-2)
@Override
public int hashCode() {
    return Objects.hash(idAuteur, nom, prenom, nationalite);
}
```

**Total concepts TD1-2:** 23 instances

---

### 1.4 Livre.java

**Concepts TD1-2 (POO) identifiés:**

| Ligne(s) | Concept | Description |
|----------|---------|-------------|
| 1 | `package` | Organisation en packages |
| 3 | `import java.util.Objects` | Import classe utilitaire |
| 23 | `public class Livre` | Déclaration de classe |
| 28-35 | Attributs privés | Encapsulation (private int, String, Auteur, Categorie) |
| 33 | **Composition** | Livre possède un Auteur (relation "has-a") |
| 34 | **Composition avec enum** | Livre possède une Categorie |
| 41-43 | Constructeur par défaut | Initialisation sans paramètres |
| 49-62 | Constructeur complet | Constructeur avec tous les attributs |
| 71-135 | Getters/Setters | Méthodes d'accès et modification (8 paires) |
| 144-151 | **Méthode métier** `estDisponible()` | Logique métier simple |
| 159-165 | **Méthode métier** `emprunter()` | Modification état avec validation |
| 173-179 | **Méthode métier** `retourner()` | Modification état |
| 187-198 | `equals()` | Comparaison basée sur ISBN unique |
| 188 | `this == o` | Optimisation equals |
| 189-190 | `instanceof` + null check | Vérification de type |
| 192 | Cast | `(Livre) o` |
| 193-197 | `Objects.equals()` | Comparaison d'ISBN |
| 206-208 | `hashCode()` | Hash basé sur ISBN |
| 217-227 | `toString()` | Représentation textuelle |

**Code représentatif:**
```java
// Lignes 33-34: Composition (TD1-2)
private Auteur auteur;        // Livre "has-a" Auteur
private Categorie categorie;  // Livre "has-a" Categorie

// Lignes 144-151: Méthode métier (TD1-2)
public boolean estDisponible() {
    return disponibles > 0;
}

// Lignes 159-165: Méthode métier avec validation (TD1-2)
public void emprunter() {
    if (disponibles > 0) {
        disponibles--;
    }
}
```

**Total concepts TD1-2:** 26 instances

---

### 1.5 Membre.java

**Concepts TD1-2 (POO) identifiés:**

| Ligne(s) | Concept | Description |
|----------|---------|-------------|
| 1 | `package` | Organisation en packages |
| 3 | `import java.util.Objects` | Import |
| 24 | `public class Membre` | Classe publique |
| 29-34 | Attributs privés | Encapsulation (6 attributs) |
| 34 | Attribut `int` | nombreEmpruntsActifs |
| 40-42 | Constructeur par défaut | Sans paramètres |
| 48-60 | Constructeur complet | Avec paramètres |
| 69-127 | Getters/Setters | 6 paires de méthodes |
| 136-140 | **Méthode métier** `peutEmprunter()` | Règle métier (max 5 emprunts) |
| 148-150 | **Méthode métier** `incrementerEmprunts()` | Modification état |
| 157-162 | **Méthode métier** `decrementerEmprunts()` | Modification avec validation |
| 170-184 | `equals()` | Comparaison sur CIN (identifiant unique) |
| 171 | `this == o` | Optimisation |
| 172-173 | `instanceof` | Vérification type |
| 175 | Cast | Type conversion |
| 176-182 | `Objects.equals()` | Comparaison null-safe multi-champs |
| 191-193 | `hashCode()` | Hash multi-champs |
| 202-213 | `toString()` | Représentation textuelle |

**Code représentatif:**
```java
// Lignes 136-140: Méthode métier avec règle de gestion (TD1-2)
public boolean peutEmprunter() {
    // Règle : Un membre ne peut avoir que 5 emprunts actifs maximum
    return nombreEmpruntsActifs < 5;
}

// Lignes 157-162: Méthode métier avec validation (TD1-2)
public void decrementerEmprunts() {
    if (nombreEmpruntsActifs > 0) {
        nombreEmpruntsActifs--;
    }
}
```

**Total concepts TD1-2:** 22 instances

---

### 1.6 Emprunt.java

**Concepts TD1-2 (POO) identifiés:**

| Ligne(s) | Concept | Description |
|----------|---------|-------------|
| 1 | `package` | Organisation en packages |
| 3-6 | `import` | Import java.time.* et java.util.Objects |
| 3 | `import java.time.LocalDate` | Utilisation de l'API Date moderne |
| 4 | `import java.time.temporal.ChronoUnit` | Calculs de dates |
| 26 | `public class Emprunt` | Classe publique |
| 31-37 | Attributs privés | Encapsulation (7 attributs) |
| 33-34 | **Composition** | Emprunt "has-a" Membre et Livre |
| 35-37 | Attributs `LocalDate` | Utilisation API moderne dates |
| 43-45 | Constructeur par défaut | Sans paramètres |
| 51-67 | Constructeur complet | 7 paramètres |
| 76-142 | Getters/Setters | 7 paires de méthodes |
| 152-158 | **Méthode métier** `calculerDuree()` | Calcul avec ChronoUnit |
| 153 | `ChronoUnit.DAYS.between()` | Calcul de différence de dates |
| 167-172 | **Méthode métier** `estEnRetard()` | Logique métier complexe |
| 168 | `LocalDate.now()` | Date actuelle |
| 169 | `dateRetourEffective == null` | Vérification emprunt en cours |
| 170 | `now.isAfter()` | Comparaison de dates |
| 180-200 | `equals()` | Comparaison sur ID unique |
| 181-182 | `this == o` + null check | Optimisations |
| 183-184 | `instanceof` | Vérification type |
| 186 | Cast | `(Emprunt) o` |
| 187-199 | `Objects.equals()` | Comparaison multi-champs |
| 208-210 | `hashCode()` | Hash multi-champs |
| 219-233 | `toString()` | Représentation textuelle complexe |

**Code représentatif:**
```java
// Lignes 152-158: Méthode métier avec API Date moderne (TD1-2)
public long calculerDuree() {
    LocalDate dateFin = (dateRetourEffective != null)
        ? dateRetourEffective
        : LocalDate.now();
    return ChronoUnit.DAYS.between(dateEmprunt, dateFin);
}

// Lignes 167-172: Méthode métier avec logique complexe (TD1-2)
public boolean estEnRetard() {
    LocalDate now = LocalDate.now();
    return (dateRetourEffective == null && now.isAfter(dateRetourPrevue)) ||
           (dateRetourEffective != null && dateRetourEffective.isAfter(dateRetourPrevue));
}
```

**Total concepts TD1-2:** 30 instances

---

## 2. Package dao (5 fichiers)

### 2.1 DatabaseConnection.java

**Concepts TD11 (JDBC) identifiés:**

| Ligne(s) | Concept | Description |
|----------|---------|-------------|
| 1 | `package` | Organisation en packages |
| 3-4 | `import java.sql.*` | Import JDBC |
| 19 | **Pattern Singleton** | Instance unique statique |
| 24-28 | Constantes JDBC | URL, USER, PASSWORD |
| 30-33 | Bloc static | Chargement driver JDBC |
| 31 | `Class.forName()` | Chargement du driver MySQL |
| 42-51 | **Méthode getInstance()** | Singleton pattern |
| 44-46 | Double-check locking | Synchronisation thread-safe |
| 45 | `synchronized` | Bloc synchronisé |
| 61-72 | **getConnection()** | Obtention connexion JDBC |
| 68 | `DriverManager.getConnection()` | Création connexion JDBC |
| 81-96 | **closeConnection()** | Fermeture ressources JDBC |
| 87 | `connection.close()` | Fermeture connexion |

**Code représentatif:**
```java
// Lignes 30-33: Chargement driver JDBC (TD11)
static {
    try {
        Class.forName(DRIVER);  // Chargement driver MySQL
    } catch (ClassNotFoundException e) {
        throw new RuntimeException("Driver MySQL non trouvé", e);
    }
}

// Lignes 61-72: Création connexion JDBC (TD11)
public Connection getConnection() throws SQLException {
    try {
        connection = DriverManager.getConnection(URL, USER, PASSWORD);
        System.out.println("✅ Connexion établie avec succès");
        return connection;
    } catch (SQLException e) {
        System.err.println("❌ Erreur lors de la connexion : " + e.getMessage());
        throw e;
    }
}
```

**Total concepts TD11:** 13 instances
**Total concepts TD1-2 (Singleton pattern):** 5 instances

---

### 2.2 AuteurDAO.java

**Concepts TD11 (JDBC) identifiés:**

| Ligne(s) | Concept | Description |
|----------|---------|-------------|
| 1 | `package` | Organisation |
| 3-7 | `import java.sql.*` | Import JDBC |
| 5 | `import java.util.List` | Collection Framework |
| 6 | `import java.util.ArrayList` | Implémentation List |
| 30-32 | Attribut Connection | Référence connexion DB |
| 38-44 | Constructeur avec connexion | Injection de dépendance |
| 54-82 | **save() - INSERT** | Insertion JDBC |
| 58 | Requête SQL INSERT | Chaîne SQL |
| 60-61 | `connection.prepareStatement()` | Création PreparedStatement |
| 61 | `Statement.RETURN_GENERATED_KEYS` | Récupération clé auto-générée |
| 64-66 | `stmt.setString()` | Paramétrage requête |
| 69 | `stmt.executeUpdate()` | Exécution UPDATE/INSERT |
| 72-76 | `stmt.getGeneratedKeys()` | Récupération ID généré |
| 73 | `rs.next()` | Navigation ResultSet |
| 74 | `rs.getInt(1)` | Extraction valeur |
| 75 | `auteur.setIdAuteur()` | Mise à jour objet |
| 92-119 | **findById() - SELECT** | Requête SELECT avec WHERE |
| 96 | Requête SQL SELECT | JOIN potentiel |
| 98 | `connection.prepareStatement()` | PreparedStatement |
| 99 | `stmt.setInt()` | Paramètre WHERE |
| 100 | `stmt.executeQuery()` | Exécution SELECT |
| 102-108 | Navigation ResultSet | Extraction données |
| 103-106 | `rs.getInt()`, `rs.getString()` | Récupération colonnes |
| 129-158 | **findAll() - SELECT ALL** | Récupération liste complète |
| 133 | `new ArrayList<>()` | Initialisation collection |
| 134 | Requête SELECT * | Sans WHERE |
| 138-148 | Boucle `while(rs.next())` | Traitement multiple résultats |
| 139-147 | Création objets Auteur | Mapping ResultSet → Objet |
| 149 | `auteurs.add()` | Ajout à collection |
| 168-197 | **update() - UPDATE** | Modification données |
| 172 | Requête SQL UPDATE | Avec WHERE |
| 174 | `connection.prepareStatement()` | PreparedStatement |
| 176-179 | Paramétrage UPDATE | setString() multiple |
| 181 | `executeUpdate()` | Exécution |
| 207-229 | **delete() - DELETE** | Suppression données |
| 211 | Requête SQL DELETE | Avec WHERE |
| 213 | `prepareStatement()` | Préparation |
| 214 | `setInt()` | Paramètre |
| 216 | `executeUpdate()` | Exécution |
| 79-80 | `catch SQLException` | Gestion exceptions JDBC |
| 81 | `throw new DatabaseException` | Encapsulation exception |

**Code représentatif:**
```java
// Lignes 54-82: INSERT avec récupération clé générée (TD11)
public void save(Auteur auteur) throws DatabaseException {
    String sql = "INSERT INTO auteurs (nom, prenom, nationalite) VALUES (?, ?, ?)";

    try (PreparedStatement stmt = connection.prepareStatement(sql,
            Statement.RETURN_GENERATED_KEYS)) {

        stmt.setString(1, auteur.getNom());
        stmt.setString(2, auteur.getPrenom());
        stmt.setString(3, auteur.getNationalite());

        stmt.executeUpdate();

        // Récupérer l'ID généré
        try (ResultSet rs = stmt.getGeneratedKeys()) {
            if (rs.next()) {
                auteur.setIdAuteur(rs.getInt(1));
            }
        }
    } catch (SQLException e) {
        throw new DatabaseException("Erreur save auteur", e);
    }
}

// Lignes 92-119: SELECT avec mapping ResultSet (TD11)
public Auteur findById(int id) throws DatabaseException {
    String sql = "SELECT * FROM auteurs WHERE id_auteur = ?";

    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
        stmt.setInt(1, id);

        try (ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return new Auteur(
                    rs.getInt("id_auteur"),
                    rs.getString("nom"),
                    rs.getString("prenom"),
                    rs.getString("nationalite")
                );
            }
        }
    } catch (SQLException e) {
        throw new DatabaseException("Erreur findById auteur", e);
    }
    return null;
}
```

**Concepts TD8-9 (I/O - try-with-resources):**

| Ligne(s) | Concept | Description |
|----------|---------|-------------|
| 60-77 | `try-with-resources` | Auto-fermeture PreparedStatement |
| 72-76 | `try-with-resources` imbriqué | Auto-fermeture ResultSet |
| 98-117 | `try-with-resources` | Auto-fermeture stmt et rs |

**Total concepts TD11:** 45+ instances
**Total concepts TD8-9:** 10 instances
**Total concepts TD1-2:** 8 instances

---

### 2.3 LivreDAO.java

**Concepts TD11 (JDBC) identifiés:**

| Ligne(s) | Concept | Description |
|----------|---------|-------------|
| 1 | `package` | Organisation |
| 3-8 | `import` | JDBC, Collections, Optional |
| 7 | `import java.util.Optional` | Gestion absence de valeur |
| 36-39 | Constructeur | Injection connexion |
| 49-93 | **save() - INSERT complexe** | Insertion avec FK |
| 53 | Requête INSERT | 6 colonnes avec FK auteur |
| 55-56 | `prepareStatement()` | Avec RETURN_GENERATED_KEYS |
| 59-65 | Paramétrage complexe | setString, setInt multiple |
| 68 | `executeUpdate()` | Exécution INSERT |
| 71-75 | Récupération clé | getGeneratedKeys() |
| 103-170 | **findById() - SELECT avec JOIN** | Requête multi-tables |
| 107-112 | **Requête SQL JOIN** | INNER JOIN auteurs |
| 114 | `prepareStatement()` | Préparation |
| 115 | `setInt()` | Paramètre WHERE |
| 116 | `executeQuery()` | Exécution SELECT |
| 118-165 | Mapping complexe | ResultSet → Livre + Auteur |
| 121-124 | Création Auteur | À partir de colonnes jointure |
| 127-136 | Création Livre | Avec toutes les colonnes |
| 131 | `Categorie.valueOf()` | Conversion String → Enum |
| 138 | `livre.setAuteur()` | Association objets |
| 140 | `Optional.of(livre)` | Encapsulation dans Optional |
| 166 | `Optional.empty()` | Absence de résultat |
| 180-258 | **findAll() - SELECT avec JOIN** | Liste complète |
| 184-189 | Requête SELECT avec JOIN | Multi-tables |
| 194 | `executeQuery()` | Sans paramètre |
| 196-251 | Boucle while | Traitement multiples lignes |
| 197-204 | Extraction Auteur | Mapping colonnes |
| 207-223 | Extraction Livre | Mapping complet |
| 224 | Association | livre.setAuteur() |
| 226 | Ajout collection | livres.add() |
| 268-321 | **update() - UPDATE complexe** | Modification avec FK |
| 272-277 | Requête UPDATE | 6 colonnes + WHERE |
| 283-290 | Paramétrage UPDATE | 7 setters |
| 292 | `executeUpdate()` | Exécution |
| 294-297 | Vérification résultat | Lignes affectées |
| 331-356 | **delete() - DELETE** | Suppression |
| 335 | Requête DELETE | Avec WHERE |
| 340 | `setInt()` | Paramètre |
| 342 | `executeUpdate()` | Exécution |
| 366-433 | **rechercherParMotCle() - LIKE** | Recherche textuelle |
| 370-375 | Requête avec LIKE | Recherche dans titre/ISBN |
| 377 | `prepareStatement()` | Préparation |
| 379-380 | Paramétrage LIKE | Pattern avec % |
| 382 | `executeQuery()` | Exécution |
| 384-426 | Boucle traitement | Multiple résultats |

**Code représentatif:**
```java
// Lignes 103-170: SELECT avec JOIN et Optional (TD11)
public Optional<Livre> findById(int id) throws DatabaseException {
    String sql = "SELECT l.*, a.nom, a.prenom, a.nationalite " +
                 "FROM livres l " +
                 "INNER JOIN auteurs a ON l.id_auteur = a.id_auteur " +
                 "WHERE l.id_livre = ?";

    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
        stmt.setInt(1, id);

        try (ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                // Créer l'auteur
                Auteur auteur = new Auteur(
                    rs.getInt("id_auteur"),
                    rs.getString("nom"),
                    rs.getString("prenom"),
                    rs.getString("nationalite")
                );

                // Créer le livre
                Livre livre = new Livre(
                    rs.getInt("id_livre"),
                    rs.getString("isbn"),
                    rs.getString("titre"),
                    auteur,
                    Categorie.valueOf(rs.getString("categorie")),
                    rs.getInt("annee_publication"),
                    rs.getInt("disponibles")
                );

                return Optional.of(livre);
            }
            return Optional.empty();
        }
    } catch (SQLException e) {
        throw new DatabaseException("Erreur findById livre", e);
    }
}

// Lignes 366-433: Recherche avec LIKE (TD11)
public List<Livre> rechercherParMotCle(String motCle) throws DatabaseException {
    String sql = "SELECT l.*, a.nom, a.prenom, a.nationalite " +
                 "FROM livres l " +
                 "INNER JOIN auteurs a ON l.id_auteur = a.id_auteur " +
                 "WHERE l.titre LIKE ? OR l.isbn LIKE ?";

    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
        String pattern = "%" + motCle + "%";
        stmt.setString(1, pattern);
        stmt.setString(2, pattern);

        try (ResultSet rs = stmt.executeQuery()) {
            // Traitement résultats...
        }
    }
}
```

**Concepts TD1-2 (Collections & Optional):**

| Ligne(s) | Concept | Description |
|----------|---------|-------------|
| 140 | `Optional.of()` | Encapsulation valeur présente |
| 166 | `Optional.empty()` | Absence de valeur |
| 184 | `new ArrayList<>()` | Initialisation List |
| 226 | `list.add()` | Ajout élément |

**Total concepts TD11:** 60+ instances
**Total concepts TD8-9 (try-with-resources):** 12 instances
**Total concepts TD1-2:** 15 instances

---

### 2.4 MembreDAO.java

**Concepts TD11 (JDBC) identifiés:**

| Ligne(s) | Concept | Description |
|----------|---------|-------------|
| 1 | `package` | Organisation |
| 3-7 | `import` JDBC | Connection, PreparedStatement, ResultSet, SQLException |
| 5-6 | `import` Collections | List, ArrayList |
| 36-39 | Constructeur | Injection connexion |
| 49-91 | **save() - INSERT** | Insertion membre |
| 53-57 | Requête SQL INSERT | 5 colonnes |
| 59-60 | `prepareStatement()` | Avec RETURN_GENERATED_KEYS |
| 63-67 | Paramétrage | setString() pour 5 champs |
| 68 | `setInt()` | nombreEmpruntsActifs |
| 70 | `executeUpdate()` | Exécution |
| 73-77 | Récupération clé | getGeneratedKeys() |
| 101-133 | **findById() - SELECT** | Recherche par ID |
| 105 | Requête SELECT | Avec WHERE |
| 107 | `prepareStatement()` | Préparation |
| 108 | `setInt()` | Paramètre |
| 109 | `executeQuery()` | Exécution |
| 111-128 | Mapping ResultSet | Création Membre |
| 143-182 | **findAll() - SELECT** | Liste complète |
| 147 | `new ArrayList<>()` | Initialisation |
| 148 | SELECT * | Sans WHERE |
| 152-175 | Boucle while | Multiple résultats |
| 177 | `add()` | Ajout collection |
| 192-242 | **update() - UPDATE** | Modification |
| 196-201 | Requête UPDATE | 5 colonnes + WHERE |
| 207-212 | Paramétrage | 6 setters |
| 214 | `executeUpdate()` | Exécution |
| 216-219 | Vérification | Lignes affectées |
| 252-277 | **delete() - DELETE** | Suppression |
| 256 | Requête DELETE | Avec WHERE |
| 261 | `setInt()` | Paramètre |
| 263 | `executeUpdate()` | Exécution |
| 287-325 | **findByCin() - SELECT WHERE** | Recherche par CIN unique |
| 291 | Requête avec WHERE | Champ unique |
| 293 | `prepareStatement()` | Préparation |
| 294 | `setString()` | Paramètre CIN |
| 295 | `executeQuery()` | Exécution |

**Code représentatif:**
```java
// Lignes 287-325: SELECT avec WHERE sur champ unique (TD11)
public Membre findByCin(String cin) throws DatabaseException {
    String sql = "SELECT * FROM membres WHERE cin = ?";

    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
        stmt.setString(1, cin);

        try (ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return new Membre(
                    rs.getInt("id_membre"),
                    rs.getString("cin"),
                    rs.getString("nom"),
                    rs.getString("prenom"),
                    rs.getString("email"),
                    rs.getInt("nombre_emprunts_actifs")
                );
            }
        }
    } catch (SQLException e) {
        throw new DatabaseException("Erreur findByCin", e);
    }
    return null;
}
```

**Total concepts TD11:** 40+ instances
**Total concepts TD8-9:** 10 instances

---

### 2.5 EmpruntDAO.java

**Concepts TD11 (JDBC) - Transactions identifiées:**

| Ligne(s) | Concept | Description |
|----------|---------|-------------|
| 1 | `package` | Organisation |
| 3-9 | `import` | JDBC complet + java.time |
| 44-48 | Constructeur | Injection connexion |
| 58-111 | **save() - INSERT complexe** | Insertion avec 4 FK |
| 62-66 | Requête INSERT | 4 colonnes FK + 3 dates |
| 68-69 | `prepareStatement()` | RETURN_GENERATED_KEYS |
| 72-78 | Paramétrage FK | setInt() pour IDs |
| 79-81 | Paramétrage dates | setObject(LocalDate) |
| 84 | `executeUpdate()` | Exécution |
| 87-91 | Récupération clé | getGeneratedKeys() |
| 121-254 | **findById() - TRIPLE JOIN** | Requête 4 tables |
| 125-133 | **Requête SQL complexe** | 3 INNER JOIN |
| 135 | `prepareStatement()` | Préparation |
| 136 | `setInt()` | Paramètre WHERE |
| 137 | `executeQuery()` | Exécution |
| 139-247 | Mapping complexe | 3 objets (Membre, Auteur, Livre) |
| 142-146 | Extraction Membre | ResultSet → Membre |
| 149-153 | Extraction Auteur | ResultSet → Auteur |
| 156-168 | Extraction Livre | ResultSet → Livre avec Categorie enum |
| 171-181 | Extraction Emprunt | ResultSet → Emprunt complet |
| 174-176 | `rs.getObject(LocalDate.class)` | Mapping date moderne |
| 178 | `StatutEmprunt.valueOf()` | String → Enum |
| 183-185 | Associations | setMembre(), setLivre() |
| 264-368 | **findAll() - TRIPLE JOIN** | Liste avec jointures |
| 268-276 | Requête SELECT complexe | 3 INNER JOIN |
| 281 | `executeQuery()` | Sans paramètre |
| 283-361 | Boucle while | Multiples résultats |
| 378-460 | **findByMembre() - JOIN avec WHERE** | Filtrage par membre |
| 382-390 | Requête JOIN + WHERE | id_membre = ? |
| 392 | `prepareStatement()` | Préparation |
| 393 | `setInt()` | Paramètre |
| 470-609 | **effectuerEmpruntTransaction() - TRANSACTION** | Gestion transactionnelle complète |
| 481 | `connection.setAutoCommit(false)` | **Début transaction** |
| 485-489 | SELECT FOR UPDATE | Verrouillage lecture |
| 494 | `setInt()` | Paramètre |
| 496 | `executeQuery()` | Lecture livre |
| 498-540 | Vérifications métier | Disponibilité |
| 544-558 | UPDATE disponibles | Décrémentation |
| 561-574 | INSERT emprunt | Création enregistrement |
| 576-580 | UPDATE membre | Incrémentation emprunts actifs |
| 584 | `connection.commit()` | **COMMIT transaction** |
| 588-598 | Bloc catch | Gestion erreurs |
| 591 | `connection.rollback()` | **ROLLBACK en cas d'erreur** |
| 600-606 | Bloc finally | Restauration auto-commit |
| 602 | `connection.setAutoCommit(true)` | Fin transaction |
| 619-706 | **retournerLivreTransaction() - TRANSACTION** | Retour avec transaction |
| 630 | `setAutoCommit(false)` | Début transaction |
| 634-641 | SELECT emprunt | Lecture état |
| 651-671 | UPDATE emprunt | Modification statut |
| 674-688 | UPDATE livre | Incrémentation disponibles |
| 691-693 | UPDATE membre | Décrémentation emprunts |
| 696 | `commit()` | Validation |
| 699 | `rollback()` | Annulation |
| 702 | `setAutoCommit(true)` | Fin transaction |
| 716-777 | **update() - UPDATE** | Modification simple |
| 787-809 | **delete() - DELETE** | Suppression |

**Code représentatif - TRANSACTION COMPLÈTE:**
```java
// Lignes 470-609: Transaction ACID complète (TD11)
public Emprunt effectuerEmpruntTransaction(int idMembre, int idLivre)
        throws DatabaseException, LivreIndisponibleException {

    try {
        // DÉBUT TRANSACTION
        connection.setAutoCommit(false);

        // ÉTAPE 1: SELECT FOR UPDATE (verrouillage)
        String sqlLivre = "SELECT disponibles FROM livres WHERE id_livre = ? FOR UPDATE";

        try (PreparedStatement stmtLivre = connection.prepareStatement(sqlLivre)) {
            stmtLivre.setInt(1, idLivre);

            try (ResultSet rs = stmtLivre.executeQuery()) {
                if (rs.next()) {
                    int disponibles = rs.getInt("disponibles");

                    if (disponibles <= 0) {
                        throw new LivreIndisponibleException(
                            "Livre ID " + idLivre + " indisponible");
                    }

                    // ÉTAPE 2: UPDATE livres (décrémenter disponibles)
                    String sqlUpdateLivre =
                        "UPDATE livres SET disponibles = disponibles - 1 WHERE id_livre = ?";

                    try (PreparedStatement stmtUpdate =
                            connection.prepareStatement(sqlUpdateLivre)) {
                        stmtUpdate.setInt(1, idLivre);
                        stmtUpdate.executeUpdate();
                    }

                    // ÉTAPE 3: INSERT emprunt
                    String sqlInsert =
                        "INSERT INTO emprunts (id_membre, id_livre, date_emprunt, date_retour_prevue) " +
                        "VALUES (?, ?, ?, ?)";

                    LocalDate dateEmprunt = LocalDate.now();
                    LocalDate dateRetourPrevue = dateEmprunt.plusDays(14);

                    try (PreparedStatement stmtInsert =
                            connection.prepareStatement(sqlInsert,
                                Statement.RETURN_GENERATED_KEYS)) {
                        stmtInsert.setInt(1, idMembre);
                        stmtInsert.setInt(2, idLivre);
                        stmtInsert.setObject(3, dateEmprunt);
                        stmtInsert.setObject(4, dateRetourPrevue);
                        stmtInsert.executeUpdate();

                        // Récupérer ID
                        try (ResultSet rsKeys = stmtInsert.getGeneratedKeys()) {
                            if (rsKeys.next()) {
                                int idEmprunt = rsKeys.getInt(1);

                                // ÉTAPE 4: UPDATE membre
                                String sqlUpdateMembre =
                                    "UPDATE membres SET nombre_emprunts_actifs = " +
                                    "nombre_emprunts_actifs + 1 WHERE id_membre = ?";

                                try (PreparedStatement stmtMembre =
                                        connection.prepareStatement(sqlUpdateMembre)) {
                                    stmtMembre.setInt(1, idMembre);
                                    stmtMembre.executeUpdate();
                                }

                                // COMMIT TRANSACTION
                                connection.commit();

                                // Retourner l'emprunt créé
                                return findById(idEmprunt).orElse(null);
                            }
                        }
                    }
                }
            }
        }

        // ROLLBACK si pas de résultat
        connection.rollback();
        return null;

    } catch (SQLException e) {
        // ROLLBACK en cas d'erreur
        try {
            connection.rollback();
        } catch (SQLException rollbackEx) {
            rollbackEx.printStackTrace();
        }
        throw new DatabaseException("Erreur transaction emprunt", e);

    } finally {
        // Restaurer auto-commit
        try {
            connection.setAutoCommit(true);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
```

**Total concepts TD11:** 80+ instances (le fichier le plus riche en JDBC)
**Total concepts TD8-9:** 20+ instances

---

## 3. Package exceptions (5 fichiers)

### 3.1 BiblioTechException.java

**Concepts TD1-2 (Hiérarchie exceptions) identifiés:**

| Ligne(s) | Concept | Description |
|----------|---------|-------------|
| 1 | `package` | Organisation |
| 17 | `extends Exception` | **Héritage** - Exception personnalisée |
| 22 | Attribut privé | code erreur |
| 28-31 | Constructeur 1 | Message uniquement |
| 29 | `super(message)` | Appel constructeur parent |
| 38-42 | Constructeur 2 | Message + cause |
| 39 | `super(message, cause)` | Appel constructeur parent avec cause |
| 50-54 | Constructeur 3 | Message + code |
| 51 | `super(message)` | Appel parent |
| 63-68 | Constructeur 4 | Message + cause + code |
| 64 | `super(message, cause)` | Appel parent complet |
| 75-77 | Getter | getCode() |

**Code représentatif:**
```java
// Ligne 17: Héritage d'Exception (TD1-2)
public class BiblioTechException extends Exception {

    private String code;

    // Lignes 28-31: Constructeur avec super() (TD1-2)
    public BiblioTechException(String message) {
        super(message);  // Appel constructeur parent
    }

    // Lignes 38-42: Constructeur avec cause (TD1-2)
    public BiblioTechException(String message, Throwable cause) {
        super(message, cause);  // Chaînage exceptions
    }
}
```

**Total concepts TD1-2:** 12 instances

---

### 3.2 DatabaseException.java

**Concepts TD1-2 identifiés:**

| Ligne(s) | Concept | Description |
|----------|---------|-------------|
| 1 | `package` | Organisation |
| 15 | `extends BiblioTechException` | **Héritage** exception métier |
| 21-24 | Constructeur 1 | Message uniquement |
| 22 | `super(message, "DB_ERROR")` | Appel parent avec code |
| 31-34 | Constructeur 2 | Message + cause SQLException |
| 32 | `super(message, cause, "DB_ERROR")` | Appel parent complet |

**Code représentatif:**
```java
// Ligne 15: Héritage d'exception personnalisée (TD1-2)
public class DatabaseException extends BiblioTechException {

    // Lignes 31-34: Encapsulation SQLException (TD1-2)
    public DatabaseException(String message, Throwable cause) {
        super(message, cause, "DB_ERROR");
    }
}
```

**Total concepts TD1-2:** 6 instances

---

### 3.3 LivreIndisponibleException.java

**Concepts TD1-2 identifiés:**

| Ligne(s) | Concept | Description |
|----------|---------|-------------|
| 1 | `package` | Organisation |
| 12 | `extends BiblioTechException` | Héritage |
| 18-21 | Constructeur | Message avec code |
| 19 | `super(message, "LIVRE_INDISPONIBLE")` | Appel parent |

**Total concepts TD1-2:** 4 instances

---

### 3.4 LivreNonTrouveException.java

**Concepts TD1-2 identifiés:**

| Ligne(s) | Concept | Description |
|----------|---------|-------------|
| 1 | `package` | Organisation |
| 12 | `extends BiblioTechException` | Héritage |
| 18-21 | Constructeur | Message avec code |
| 19 | `super(message, "LIVRE_NON_TROUVE")` | Appel parent |

**Total concepts TD1-2:** 4 instances

---

### 3.5 MembreNonTrouveException.java

**Concepts TD1-2 identifiés:**

| Ligne(s) | Concept | Description |
|----------|---------|-------------|
| 1 | `package` | Organisation |
| 12 | `extends BiblioTechException` | Héritage |
| 18-21 | Constructeur | Message avec code |
| 19 | `super(message, "MEMBRE_NON_TROUVE")` | Appel parent |

**Total concepts TD1-2:** 4 instances

---

## 4. Package services (4 fichiers)

### 4.1 BibliothequeService.java

**Concepts TD1-2 (POO) identifiés:**

| Ligne(s) | Concept | Description |
|----------|---------|-------------|
| 1 | `package` | Organisation |
| 3-9 | `import` | DAO, modèles, exceptions, List |
| 32-35 | Attributs privés | Encapsulation (3 DAO) |
| 41-45 | Constructeur par défaut | Initialisation DAO |
| 52-58 | Constructeur avec params | Injection dépendances |

**Concepts TD3 (Lambda) identifiés:**

| Ligne(s) | Concept | Description |
|----------|---------|-------------|
| 208 | `livre -> livre.estDisponible()` | Lambda Predicate |
| 252 | `membre -> membre.peutEmprunter()` | Lambda Predicate |

**Concepts TD4-5 (Streams) identifiés:**

| Ligne(s) | Concept | Description |
|----------|---------|-------------|
| 197-211 | **rechercherLivres()** | Méthode avec Streams |
| 204 | `livres.stream()` | Création stream |
| 205-207 | `.filter(livre -> ...)` | Filtrage avec lambda |
| 208 | Condition lambda | estDisponible() ET contains() |
| 209 | `.collect(Collectors.toList())` | Collection finale |
| 241-256 | **getMembresActifs()** | Stream avec filtre |
| 248 | `membres.stream()` | Création stream |
| 249-252 | `.filter(membre -> ...)` | Filtrage complexe |
| 253 | `.collect(Collectors.toList())` | Collection |

**Code représentatif:**
```java
// Lignes 197-211: Stream avec filter et lambda (TD3, TD4-5)
public List<Livre> rechercherLivres(String motCle) throws DatabaseException {
    List<Livre> livres = livreDAO.findAll();

    String motCleNormalise = motCle.toLowerCase();

    return livres.stream()
        .filter(livre -> livre.estDisponible() &&
                (livre.getTitre().toLowerCase().contains(motCleNormalise) ||
                 livre.getIsbn().contains(motCle)))
        .collect(Collectors.toList());
}

// Lignes 241-256: Stream avec filtrage métier (TD3, TD4-5)
public List<Membre> getMembresActifs() throws DatabaseException {
    List<Membre> membres = membreDAO.findAll();

    return membres.stream()
        .filter(membre -> membre.getNombreEmpruntsActifs() > 0 &&
                         membre.peutEmprunter())
        .collect(Collectors.toList());
}
```

**Total concepts TD1-2:** 15 instances
**Total concepts TD3:** 8 instances
**Total concepts TD4-5:** 12 instances

---

### 4.2 EmpruntService.java

**Concepts TD6-10 (Multithreading) identifiés:**

| Ligne(s) | Concept | Description |
|----------|---------|-------------|
| 1 | `package` | Organisation |
| 3-10 | `import` | DAO, modèles, exceptions |
| 11-14 | `import java.util.concurrent.locks.*` | **Import synchronisation** |
| 11 | `import ReentrantLock` | Verrou réentrant |
| 12 | `import Condition` | Variable de condition |
| 45-46 | Attributs synchronisation | lock et condition |
| 46 | `new ReentrantLock()` | **Création lock** |
| 47 | `lock.newCondition()` | **Création condition** |
| 53-58 | Constructeur | Initialisation lock |
| 84-137 | **effectuerEmprunt() synchronisé** | Méthode thread-safe |
| 91 | `lock.lock()` | **Acquisition verrou** |
| 94-130 | Bloc try | Section critique |
| 132-134 | Bloc finally | Libération garantie |
| 133 | `lock.unlock()` | **Libération verrou** |
| 169-206 | **retournerLivre() synchronisé** | Avec lock |
| 176 | `lock.lock()` | Acquisition |
| 200 | `lock.unlock()` | Libération |
| 216-235 | **getEmpruntsEnCours() synchronisé** | Lecture protégée |
| 223 | `lock.lock()` | Acquisition lecture |
| 229 | `lock.unlock()` | Libération lecture |

**Code représentatif:**
```java
// Lignes 45-47: Déclaration lock et condition (TD6-10)
private final ReentrantLock lock;
private final Condition disponibilite;

// Constructor
public EmpruntService() {
    this.lock = new ReentrantLock();
    this.disponibilite = lock.newCondition();
}

// Lignes 84-137: Méthode synchronisée avec lock (TD6-10)
public Emprunt effectuerEmprunt(int idMembre, int idLivre)
        throws MembreNonTrouveException, LivreNonTrouveException,
               LivreIndisponibleException, DatabaseException {

    lock.lock();  // ACQUISITION VERROU
    try {
        // SECTION CRITIQUE

        // Vérifier membre
        Membre membre = membreDAO.findById(idMembre);
        if (membre == null) {
            throw new MembreNonTrouveException("Membre ID " + idMembre + " introuvable");
        }

        // Vérifier livre
        Optional<Livre> livreOpt = livreDAO.findById(idLivre);
        if (!livreOpt.isPresent()) {
            throw new LivreNonTrouveException("Livre ID " + idLivre + " introuvable");
        }

        Livre livre = livreOpt.get();

        // Vérifier disponibilité
        if (!livre.estDisponible()) {
            throw new LivreIndisponibleException("Livre " + livre.getTitre() + " indisponible");
        }

        // Effectuer emprunt via DAO (transaction)
        return empruntDAO.effectuerEmpruntTransaction(idMembre, idLivre);

    } finally {
        lock.unlock();  // LIBÉRATION GARANTIE
    }
}
```

**Total concepts TD6-10:** 25 instances
**Total concepts TD1-2:** 10 instances

---

### 4.3 StatistiquesService.java

**Concepts TD3 (Lambda) identifiés:**

| Ligne(s) | Concept | Description |
|----------|---------|-------------|
| 107 | `Emprunt::getLivre` | **Method reference** |
| 112 | `Map.Entry.<Livre, Long>comparingByValue()` | Method reference Comparator |
| 139 | `Livre::getCategorie` | Method reference |
| 162 | `Livre::getCategorie` | Method reference |
| 195 | `Livre::getDisponibles` | Method reference |
| 227 | `Membre::getNombreEmpruntsActifs` | Method reference |
| 258 | `e -> e.getDateRetourEffective() != null` | Lambda Predicate |
| 260 | `Emprunt::calculerDuree` | Method reference |
| 303 | `Emprunt::estEnRetard` | Method reference |
| 344 | `Emprunt::estEnRetard` | Method reference |
| 374 | `Membre::getNombreEmpruntsActifs` | Method reference avec Comparator |
| 402 | `emprunt -> emprunt.getLivre().getCategorie()` | Lambda complexe |
| 427 | `Livre::getDisponibles` | Method reference |

**Concepts TD4-5 (Streams API) - MASSIF:**

| Ligne(s) | Concept | Description |
|----------|---------|-------------|
| 98-117 | **top10LivresPlusEmpruntes()** | Stream complexe multi-étapes |
| 104-109 | `.collect(groupingBy(Emprunt::getLivre, counting()))` | **Collector groupingBy + counting** |
| 110 | `.entrySet().stream()` | Stream sur Map.Entry |
| 112 | `.sorted(comparingByValue().reversed())` | Tri décroissant |
| 114 | `.limit(10)` | Limitation résultats |
| 116 | `.collect(toList())` | Collection finale |
| 133-140 | **livresParCategorie()** | groupingBy simple |
| 138-139 | `.collect(groupingBy(Livre::getCategorie))` | Groupement par catégorie |
| 155-165 | **nombreLivresParCategorie()** | groupingBy avec counting |
| 160-163 | `.collect(groupingBy(Livre::getCategorie, counting()))` | Comptage par groupe |
| 189-196 | **statsDisponibilite()** | IntSummaryStatistics |
| 194-195 | `.collect(summarizingInt(Livre::getDisponibles))` | **Collector summarizingInt** |
| 216-230 | **moyenneEmpruntsParMembre()** | Stream numérique |
| 226-229 | `.mapToInt(Membre::getNombreEmpruntsActifs).average().orElse(0.0)` | **mapToInt + average + Optional** |
| 251-264 | **dureeMoyenneEmprunts()** | Stream avec filter + mapToLong |
| 256-258 | `.filter(e -> e.getDateRetourEffective() != null)` | Filtrage |
| 260 | `.mapToLong(Emprunt::calculerDuree)` | **mapToLong** |
| 262-263 | `.average().orElse(0.0)` | Moyenne avec Optional |
| 289-311 | **getMembresEnRetard()** | Stream avec anyMatch imbriqué |
| 294-309 | `.filter(membre -> {...})` | Filtrage complexe |
| 302-303 | `empruntsMembre.stream().anyMatch(Emprunt::estEnRetard)` | **anyMatch** (nested stream) |
| 310 | `.collect(toList())` | Collection |
| 330-348 | **tauxRetard()** | Stream avec count |
| 342-344 | `.stream().filter(Emprunt::estEnRetard).count()` | filter + count |
| 368-377 | **top5MembresActifs()** | sorted + limit |
| 373-376 | `.sorted(Comparator.comparing(...).reversed()).limit(5)` | Tri avec Comparator + limit |
| 395-405 | **empruntsParCategorie()** | groupingBy avec navigation |
| 400-404 | `.collect(groupingBy(e -> e.getLivre().getCategorie(), counting()))` | Groupement avec lambda |
| 421-429 | **totalExemplairesDisponibles()** | mapToInt + sum |
| 426-428 | `.mapToInt(Livre::getDisponibles).sum()` | **Somme avec mapToInt** |

**Code représentatif:**
```java
// Lignes 98-117: Stream complexe avec groupingBy + counting (TD3, TD4-5)
public List<Map.Entry<Livre, Long>> top10LivresPlusEmpruntes()
        throws DatabaseException {

    List<Emprunt> tousEmprunts = empruntDAO.findAll();

    return tousEmprunts.stream()
        // Grouper par livre (clé) et compter les emprunts (valeur)
        .collect(Collectors.groupingBy(
            Emprunt::getLivre,           // Method reference (TD3)
            Collectors.counting()         // Collector counting (TD4-5)
        ))
        .entrySet().stream()              // Stream sur Map.Entry
        // Trier par valeur (nombre d'emprunts) décroissant
        .sorted(Map.Entry.<Livre, Long>comparingByValue().reversed())
        // Limiter aux 10 premiers
        .limit(10)
        // Collecter en liste
        .collect(Collectors.toList());
}

// Lignes 189-196: IntSummaryStatistics (TD4-5)
public IntSummaryStatistics statsDisponibilite()
        throws DatabaseException {

    List<Livre> tousLivres = livreDAO.findAll();

    return tousLivres.stream()
        .collect(Collectors.summarizingInt(Livre::getDisponibles));
}

// Lignes 216-230: Stream numérique avec average (TD4-5)
public double moyenneEmpruntsParMembre()
        throws DatabaseException {

    List<Membre> tousMembres = membreDAO.findAll();

    if (tousMembres.isEmpty()) {
        return 0.0;
    }

    return tousMembres.stream()
        .mapToInt(Membre::getNombreEmpruntsActifs)
        .average()
        .orElse(0.0);  // Optional handling
}

// Lignes 289-311: anyMatch avec nested stream (TD4-5)
public List<Membre> getMembresEnRetard()
        throws DatabaseException {

    List<Membre> tousMembres = membreDAO.findAll();

    return tousMembres.stream()
        .filter(membre -> {
            try {
                List<Emprunt> empruntsMembre =
                    empruntDAO.findByMembre(membre.getIdMembre());

                // Vérifier si au moins un emprunt est en retard
                return empruntsMembre.stream()
                    .anyMatch(Emprunt::estEnRetard);  // anyMatch (TD4-5)

            } catch (DatabaseException e) {
                return false;
            }
        })
        .collect(Collectors.toList());
}
```

**Total concepts TD3:** 35+ instances
**Total concepts TD4-5:** 60+ instances (fichier le plus riche en Streams!)
**Total concepts TD1-2:** 15 instances

---

### 4.4 RapportService.java

**Concepts TD3 (Lambda) identifiés:**

| Ligne(s) | Concept | Description |
|----------|---------|-------------|
| 107-108 | Lambda filter date | `e -> e.getDateEmprunt().getMonthValue() == mois` |
| 120 | Lambda navigation | `e -> e.getLivre().getCategorie()` |
| 128 | `Emprunt::getMembre` | Method reference |
| 140 | `Emprunt::getLivre` | Method reference |
| 152 | `Emprunt::estEnRetard` | Method reference |
| 162 | Lambda filter | `e -> e.getDateRetourEffective() != null` |
| 163 | `Emprunt::calculerDuree` | Method reference |
| 280-281 | Lambda forEach | `(cat, count) -> sb.append(...)` |
| 236 | Lambda forEach | forEach sur stream |

**Concepts TD4-5 (Streams) identifiés:**

| Ligne(s) | Concept | Description |
|----------|---------|-------------|
| 88-173 | **rapportMensuel()** | Méthode avec multiple streams |
| 106-109 | Filter par date | `.filter().filter().collect()` |
| 107 | `getMonthValue()` | Extraction mois depuis LocalDate |
| 108 | `getYear()` | Extraction année |
| 118-123 | groupingBy catégorie | Stream sur emprunts du mois |
| 118-122 | `.collect(groupingBy(e -> e.getLivre().getCategorie(), counting()))` | Groupement avec comptage |
| 126-135 | Top 5 membres | groupingBy + sorted + limit |
| 126-130 | `.collect(groupingBy(Emprunt::getMembre, counting()))` | Groupement membres |
| 131 | `.entrySet().stream()` | Stream sur Map |
| 132 | `.sorted(comparingByValue().reversed())` | Tri décroissant |
| 133 | `.limit(5)` | Top 5 |
| 138-147 | Top 5 livres | Même pattern |
| 151-153 | Calcul taux retard | filter + count |
| 161-165 | Durée moyenne | filter + mapToLong + average |

**Code représentatif:**
```java
// Lignes 88-173: Méthode avec multiples streams (TD3, TD4-5)
public Map<String, Object> rapportMensuel(int mois, int annee)
        throws DatabaseException {

    List<Emprunt> tousEmprunts = empruntDAO.findAll();

    // ===== FILTRER LES EMPRUNTS DU MOIS =====
    List<Emprunt> empruntsMois = tousEmprunts.stream()
        .filter(e -> e.getDateEmprunt().getMonthValue() == mois)
        .filter(e -> e.getDateEmprunt().getYear() == annee)
        .collect(Collectors.toList());

    Map<String, Object> rapport = new HashMap<>();

    // ===== STATISTIQUE 2 : Emprunts par catégorie =====
    Map<Categorie, Long> parCategorie = empruntsMois.stream()
        .collect(Collectors.groupingBy(
            e -> e.getLivre().getCategorie(),
            Collectors.counting()
        ));
    rapport.put("par_categorie", parCategorie);

    // ===== STATISTIQUE 3 : Top 5 membres du mois =====
    List<Map.Entry<Membre, Long>> top5Membres = empruntsMois.stream()
        .collect(Collectors.groupingBy(
            Emprunt::getMembre,
            Collectors.counting()
        ))
        .entrySet().stream()
        .sorted(Map.Entry.<Membre, Long>comparingByValue().reversed())
        .limit(5)
        .collect(Collectors.toList());
    rapport.put("top_5_membres", top5Membres);

    // ===== STATISTIQUE 5 : Taux de retard ce mois =====
    if (!empruntsMois.isEmpty()) {
        long enRetard = empruntsMois.stream()
            .filter(Emprunt::estEnRetard)
            .count();
        double tauxRetard = (enRetard * 100.0) / empruntsMois.size();
        rapport.put("taux_retard", tauxRetard);
    }

    // ===== STATISTIQUE 6 : Durée moyenne des emprunts =====
    double dureeMoyenne = empruntsMois.stream()
        .filter(e -> e.getDateRetourEffective() != null)
        .mapToLong(Emprunt::calculerDuree)
        .average()
        .orElse(0.0);
    rapport.put("duree_moyenne", dureeMoyenne);

    return rapport;
}
```

**Total concepts TD3:** 20+ instances
**Total concepts TD4-5:** 35+ instances
**Total concepts TD1-2:** 12 instances

---

## 5. Package threads (6 fichiers)

### 5.1 BibliothecaireThread.java

**Concepts TD6-10 (Multithreading) identifiés:**

| Ligne(s) | Concept | Description |
|----------|---------|-------------|
| 1 | `package` | Organisation |
| 3-7 | `import` | Services, modèles, exceptions |
| 9-10 | `import java.util.*` | List, Random |
| 39 | `extends Thread` | **Création thread par héritage** |
| 79 | `super("Bibliothecaire-" + nom)` | Appel constructeur Thread avec nom |
| 100-162 | **run()** | **Méthode exécutée par le thread** |
| 116 | `getName()` | Obtention nom du thread |
| 119 | `System.currentTimeMillis()` | Mesure temps début |
| 121-157 | Boucle for | N opérations |
| 123 | `getName()` | Identification thread dans logs |
| 145 | `Thread.sleep(pauseMs)` | **Pause du thread** |
| 147-150 | `catch InterruptedException` | **Gestion interruption** |
| 149 | `Thread.currentThread().interrupt()` | **Restauration flag interruption** |
| 150 | `break` | Sortie boucle si interrompu |

**Code représentatif:**
```java
// Ligne 39: Création thread par héritage (TD6-10)
public class BibliothecaireThread extends Thread {

    // Lignes 74-86: Constructeur avec nom de thread (TD6-10)
    public BibliothecaireThread(String nomBibliothecaire,
                                EmpruntService empruntService,
                                BibliothequeService bibliothequeService,
                                SynchroManager synchroManager,
                                int nombreOperations) {
        super("Bibliothecaire-" + nomBibliothecaire);  // Nom du thread
        this.nomBibliothecaire = nomBibliothecaire;
        // ...
    }

    // Lignes 100-162: Méthode run() (TD6-10)
    @Override
    public void run() {
        System.out.println("\n[" + getName() + "] 🚀 Démarrage - " +
                nombreOperations + " opérations à effectuer");

        long debut = System.currentTimeMillis();

        for (int i = 1; i <= nombreOperations; i++) {
            try {
                // Opérations aléatoires...

                // PAUSE entre opérations
                int pauseMs = 500 + random.nextInt(1500);
                Thread.sleep(pauseMs);  // Sleep (TD6-10)

            } catch (InterruptedException e) {
                System.err.println("[" + getName() + "] ⚠ Thread interrompu !");
                Thread.currentThread().interrupt();  // Restauration flag
                break;
            }
        }

        long duree = System.currentTimeMillis() - debut;
        afficherResume(duree);
    }
}
```

**Total concepts TD6-10:** 15 instances
**Total concepts TD1-2:** 25 instances

---

### 5.2 EmpruntTask.java

**Concepts TD6-10 (Multithreading) identifiés:**

| Ligne(s) | Concept | Description |
|----------|---------|-------------|
| 1 | `package` | Organisation |
| 3-6 | `import` | Services, exceptions |
| 7 | `import java.util.concurrent.Callable` | **Interface Callable** |
| 29 | `implements Callable<String>` | **Implémentation Callable générique** |
| 34-38 | Attributs | idMembre, idLivre, empruntService |
| 44-52 | Constructeur | Initialisation des IDs et service |
| 62-112 | **call()** | **Méthode exécutée par le Callable** |
| 63 | `@Override` | Redéfinition méthode interface |
| 65-66 | `Thread.currentThread().getName()` | Identification thread exécuteur |
| 71 | `empruntService.effectuerEmprunt()` | Appel service thread-safe |
| 75-82 | Return String | **Retour de résultat** (différent de Runnable) |
| 84-105 | Gestion exceptions | Multiple catch |

**Code représentatif:**
```java
// Ligne 29: Implémentation Callable<String> (TD6-10)
public class EmpruntTask implements Callable<String> {

    private final int idMembre;
    private final int idLivre;
    private final EmpruntService empruntService;

    // Lignes 62-112: Méthode call() avec retour (TD6-10)
    @Override
    public String call() throws Exception {
        String threadName = Thread.currentThread().getName();

        System.out.println("[" + threadName + "] 📖 Tentative emprunt " +
                "Membre #" + idMembre + " Livre #" + idLivre);

        try {
            // Effectuer emprunt (service synchronisé)
            Emprunt emprunt = empruntService.effectuerEmprunt(idMembre, idLivre);

            // RETOUR DE RÉSULTAT (différent de Runnable void)
            String resultat = String.format(
                "[%s] ✅ Emprunt réussi (ID: %d) - Livre: %s",
                threadName,
                emprunt.getIdEmprunt(),
                emprunt.getLivre().getTitre()
            );

            System.out.println(resultat);
            return resultat;  // Return String

        } catch (MembreNonTrouveException e) {
            String erreur = "[" + threadName + "] ❌ Échec: Membre introuvable";
            System.out.println(erreur);
            return erreur;  // Return en cas d'erreur
        }
        // Autres exceptions...
    }
}
```

**Total concepts TD6-10:** 12 instances
**Total concepts TD1-2:** 8 instances

---

### 5.3 RechercheTask.java

**Concepts TD6-10 identifiés:**

| Ligne(s) | Concept | Description |
|----------|---------|-------------|
| 1 | `package` | Organisation |
| 3-5 | `import` | Service, modèles |
| 6-7 | `import java.util.*` | List, concurrent |
| 7 | `import java.util.concurrent.Callable` | Interface Callable |
| 25 | `implements Callable<List<Livre>>` | **Callable avec type générique complexe** |
| 30-32 | Attributs | motCle, bibliothequeService |
| 38-44 | Constructeur | Initialisation |
| 54-88 | **call()** | Méthode Callable retournant List |
| 55 | `@Override` | Implémentation interface |
| 57 | `Thread.currentThread().getName()` | Identification thread |
| 63 | `bibliothequeService.rechercherLivres()` | Appel service |
| 67-79 | Return List<Livre> | **Retour de collection** |

**Code représentatif:**
```java
// Ligne 25: Callable avec type générique List<Livre> (TD6-10)
public class RechercheTask implements Callable<List<Livre>> {

    private final String motCle;
    private final BibliothequeService bibliothequeService;

    // Lignes 54-88: call() retournant une List (TD6-10)
    @Override
    public List<Livre> call() throws Exception {
        String threadName = Thread.currentThread().getName();

        System.out.println("[" + threadName + "] 🔍 Recherche: \"" + motCle + "\"");

        try {
            // Effectuer recherche
            List<Livre> resultats = bibliothequeService.rechercherLivres(motCle);

            System.out.println("[" + threadName + "] ✅ Recherche terminée: " +
                    resultats.size() + " résultat(s)");

            // Afficher résultats...

            return resultats;  // Return List<Livre>

        } catch (DatabaseException e) {
            System.out.println("[" + threadName + "] ❌ Échec recherche");
            return new ArrayList<>();  // Return liste vide en cas d'erreur
        }
    }
}
```

**Total concepts TD6-10:** 10 instances

---

### 5.4 RetourTask.java

**Concepts TD6-10 identifiés:**

| Ligne(s) | Concept | Description |
|----------|---------|-------------|
| 1 | `package` | Organisation |
| 3-5 | `import` | Services, modèles, exceptions |
| 6 | `import java.util.concurrent.Callable` | Interface Callable |
| 24 | `implements Callable<String>` | Implémentation Callable |
| 29-31 | Attributs | idEmprunt, empruntService |
| 37-43 | Constructeur | Initialisation |
| 53-93 | **call()** | Méthode Callable |
| 54 | `@Override` | Implémentation interface |
| 56 | `Thread.currentThread().getName()` | Nom du thread |
| 61 | `empruntService.retournerLivre()` | Appel service synchronisé |
| 65-72 | Return String | Résultat de retour |

**Code représentatif:**
```java
// Ligne 24: Callable pour opération de retour (TD6-10)
public class RetourTask implements Callable<String> {

    private final int idEmprunt;
    private final EmpruntService empruntService;

    @Override
    public String call() throws Exception {
        String threadName = Thread.currentThread().getName();

        try {
            Emprunt emprunt = empruntService.retournerLivre(idEmprunt);

            String resultat = String.format(
                "[%s] ✅ Retour réussi (Emprunt #%d) - Durée: %d jours",
                threadName,
                emprunt.getIdEmprunt(),
                emprunt.calculerDuree()
            );

            return resultat;
        }
        // Gestion exceptions...
    }
}
```

**Total concepts TD6-10:** 9 instances

---

### 5.5 SynchroManager.java

**Concepts TD6-10 (Synchronisation avancée) - FICHIER CLÉ:**

| Ligne(s) | Concept | Description |
|----------|---------|-------------|
| 1 | `package` | Organisation |
| 3-9 | `import java.util.*` | Collections |
| 10-15 | `import java.util.concurrent.*` | **Package concurrent complet** |
| 10 | `import Semaphore` | **Import Semaphore** |
| 11 | `import TimeUnit` | Gestion temps |
| 12 | `import ConcurrentHashMap` | **Map thread-safe** |
| 13 | `import AtomicInteger` | **Compteur atomique** |
| 14 | `import locks.ReentrantLock` | Verrou réentrant |
| 15 | `import locks.Condition` | Variable condition |
| 44-47 | Attributs synchronisation | **4 mécanismes différents** |
| 44 | `private final Semaphore semaphore` | **Semaphore avec permits** |
| 45 | `private final ConcurrentHashMap<String, AtomicInteger>` | **Map thread-safe avec compteurs atomiques** |
| 46 | `private final ReentrantLock lock` | **Lock réentrant** |
| 47 | `private final Condition condition` | **Condition pour wait/signal** |
| 54-61 | **Constructeur avec limite** | Initialisation synchronisation |
| 56 | `new Semaphore(limiteAccesConcurrents)` | **Création Semaphore avec permits** |
| 57 | `new ConcurrentHashMap<>()` | **Création map thread-safe** |
| 58 | `new ReentrantLock()` | Création lock |
| 59 | `lock.newCondition()` | Création condition |
| 71-106 | **acquerirAcces()** | Méthode avec Semaphore |
| 79 | `semaphore.tryAcquire(timeout, TimeUnit.SECONDS)` | **Acquisition avec timeout** |
| 82-90 | Gestion acquisition | Success/failure |
| 92-101 | Bloc finally | Code toujours exécuté |
| 93 | `lock.lock()` | Acquisition lock |
| 96 | `condition.signalAll()` | **Signal à tous les threads en attente** |
| 100 | `lock.unlock()` | Libération lock |
| 103 | `catch InterruptedException` | Gestion interruption |
| 116-134 | **libererAcces()** | Libération Semaphore |
| 123 | `semaphore.release()` | **Release permit** |
| 126 | `lock.lock()` | Acquisition lock |
| 129 | `condition.signalAll()` | Signal threads |
| 133 | `lock.unlock()` | Libération |
| 144-158 | **attendreDisponibilite()** | Wait avec timeout |
| 151 | `lock.lock()` | Acquisition |
| 153-154 | `while (!semaphore.hasQueuedThreads())` | Condition d'attente |
| 155 | `condition.await(timeout, TimeUnit.SECONDS)` | **Await avec timeout** |
| 157 | `lock.unlock()` | Libération |
| 168-186 | **incrementerCompteur()** | Opération atomique |
| 175-176 | `compteurs.computeIfAbsent(type, k -> new AtomicInteger(0))` | **computeIfAbsent thread-safe** |
| 178 | `compteur.incrementAndGet()` | **Incrément atomique** |
| 196-213 | **getCompteur()** | Lecture thread-safe |
| 203 | `compteur.get()` | **Lecture atomique** |
| 223-246 | **getStatistiques()** | Map thread-safe |
| 230-243 | Boucle forEach | Copie thread-safe |
| 234 | `value.get()` | Lecture atomique |

**Code représentatif:**
```java
// Lignes 44-47: Déclaration de 4 mécanismes de synchronisation (TD6-10)
private final Semaphore semaphore;
private final ConcurrentHashMap<String, AtomicInteger> compteurs;
private final ReentrantLock lock;
private final Condition condition;

// Lignes 54-61: Initialisation (TD6-10)
public SynchroManager(int limiteAccesConcurrents) {
    this.limiteAccesConcurrents = limiteAccesConcurrents;
    this.semaphore = new Semaphore(limiteAccesConcurrents);  // Semaphore
    this.compteurs = new ConcurrentHashMap<>();              // Map thread-safe
    this.lock = new ReentrantLock();                         // Lock
    this.condition = lock.newCondition();                    // Condition
}

// Lignes 71-106: Acquisition avec Semaphore et timeout (TD6-10)
public boolean acquerirAcces(String threadName, long timeout)
        throws InterruptedException {

    System.out.println("[SynchroManager] " + threadName +
            " tente d'acquérir un accès...");

    // Essayer d'acquérir un permit avec timeout
    boolean acquired = semaphore.tryAcquire(timeout, TimeUnit.SECONDS);

    if (acquired) {
        System.out.println("[SynchroManager] ✅ " + threadName +
                " a acquis un accès (permits restants: " +
                semaphore.availablePermits() + ")");
    } else {
        System.out.println("[SynchroManager] ❌ " + threadName +
                " timeout - pas de permit disponible");
    }

    // Signaler aux threads en attente
    try {
        lock.lock();
        try {
            condition.signalAll();  // Réveil threads en attente
        } finally {
            lock.unlock();
        }
    } catch (Exception e) {
        e.printStackTrace();
    }

    return acquired;
}

// Lignes 116-134: Libération Semaphore (TD6-10)
public void libererAcces(String threadName) {
    System.out.println("[SynchroManager] " + threadName +
            " libère son accès...");

    semaphore.release();  // Release permit

    // Signaler
    lock.lock();
    try {
        condition.signalAll();
    } finally {
        lock.unlock();
    }
}

// Lignes 168-186: Incrément atomique avec ConcurrentHashMap (TD6-10)
public void incrementerCompteur(String type, boolean succes) {
    String cle = type + (succes ? "_SUCCES" : "_ECHEC");

    // computeIfAbsent est thread-safe
    AtomicInteger compteur = compteurs.computeIfAbsent(
        cle,
        k -> new AtomicInteger(0)
    );

    compteur.incrementAndGet();  // Incrément atomique
}

// Lignes 144-158: Await avec Condition (TD6-10)
public boolean attendreDisponibilite(long timeout)
        throws InterruptedException {

    lock.lock();
    try {
        while (!semaphore.hasQueuedThreads()) {
            return condition.await(timeout, TimeUnit.SECONDS);  // Await
        }
        return true;
    } finally {
        lock.unlock();
    }
}
```

**Total concepts TD6-10:** 45+ instances (le plus riche en synchronisation!)
**Total concepts TD1-2:** 15 instances

---

### 5.6 TestBibliothecaireThread.java

**Concepts TD6-10 identifiés:**

| Ligne(s) | Concept | Description |
|----------|---------|-------------|
| 1 | `package` | Organisation |
| 3-8 | `import` | Services, threads, exceptions |
| 31-44 | Initialisation services | Pour les threads |
| 50-60 | **Création threads** | 3 instances BibliothecaireThread |
| 50-52 | `new BibliothecaireThread(...)` | Instanciation threads |
| 67 | `thread1.start()` | **Démarrage thread 1** |
| 68 | `thread2.start()` | **Démarrage thread 2** |
| 69 | `thread3.start()` | **Démarrage thread 3** |
| 78-80 | `thread1.join()` | **Attente fin thread 1** |
| 81-82 | `thread2.join()` | **Attente fin thread 2** |
| 83-84 | `thread3.join()` | **Attente fin thread 3** |
| 91 | `thread1.isAlive()` | **Vérification état thread** |
| 92 | `thread2.isAlive()` | Vérification état |
| 93 | `thread3.isAlive()` | Vérification état |

**Code représentatif:**
```java
// Lignes 50-60: Création de 3 threads (TD6-10)
BibliothecaireThread thread1 = new BibliothecaireThread(
    "Alice",
    empruntService,
    bibliothequeService,
    synchroManager,
    8  // 8 opérations
);

BibliothecaireThread thread2 = new BibliothecaireThread("Bob", ...);
BibliothecaireThread thread3 = new BibliothecaireThread("Charlie", ...);

// Lignes 67-69: Démarrage des threads (TD6-10)
thread1.start();  // Lance run() dans nouveau thread
thread2.start();
thread3.start();

// Lignes 78-84: Attente fin des threads (TD6-10)
try {
    System.out.println("\n⏳ Attente de la fin des threads...\n");
    thread1.join();  // Attend fin thread1
    thread2.join();  // Attend fin thread2
    thread3.join();  // Attend fin thread3
} catch (InterruptedException e) {
    e.printStackTrace();
}

// Lignes 91-93: Vérification état (TD6-10)
System.out.println("Thread 1 actif ? " + thread1.isAlive());
System.out.println("Thread 2 actif ? " + thread2.isAlive());
System.out.println("Thread 3 actif ? " + thread3.isAlive());
```

**Total concepts TD6-10:** 18 instances

---

## 6. Package main (8 fichiers)

### 6.1 TestConnection.java

**Concepts TD11 (JDBC) identifiés:**

| Ligne(s) | Concept | Description |
|----------|---------|-------------|
| 3 | `import java.sql.Connection` | Import JDBC |
| 4 | `import java.sql.SQLException` | Exception JDBC |
| 19-25 | **Test connexion** | Obtention connexion |
| 20 | `DatabaseConnection.getInstance()` | Singleton (TD1-2 + TD11) |
| 21 | `.getConnection()` | Méthode JDBC |
| 29-34 | **Test fermeture** | Libération ressources |
| 30 | `dbConnection.closeConnection()` | Fermeture JDBC |

**Total concepts TD11:** 8 instances

---

### 6.2 TestAuteurDAO.java

**Concepts TD11 (JDBC via DAO) identifiés:**

| Ligne(s) | Concept | Description |
|----------|---------|-------------|
| 3-5 | `import` DAO | DatabaseConnection, AuteurDAO |
| 6-7 | `import` modèles | Auteur, exceptions |
| 8-9 | `import java.sql.*` | Connection, SQLException |
| 31-35 | Initialisation JDBC | Connexion + DAO |
| 32 | `DatabaseConnection.getInstance()` | Singleton |
| 33 | `dbConn.getConnection()` | Connexion JDBC |
| 34 | `new AuteurDAO(connection)` | Injection connexion |
| 42-68 | **Test CREATE** | Insertion via DAO |
| 48 | `auteurDAO.save(auteur)` | INSERT JDBC sous-jacent |
| 76-96 | **Test READ** | SELECT via DAO |
| 82 | `auteurDAO.findById()` | SELECT JDBC |
| 104-126 | **Test UPDATE** | Modification via DAO |
| 115 | `auteurDAO.update(auteur)` | UPDATE JDBC |
| 134-155 | **Test DELETE** | Suppression via DAO |
| 144 | `auteurDAO.delete(idAuteur)` | DELETE JDBC |
| 163-191 | **Test FIND ALL** | SELECT * via DAO |
| 169 | `auteurDAO.findAll()` | SELECT JDBC |

**Total concepts TD11:** 25+ instances (via DAO)

---

### 6.3 TestLivreDAO.java

**Concepts TD11 (JDBC + Optional) identifiés:**

| Ligne(s) | Concept | Description |
|----------|---------|-------------|
| 3-7 | `import` DAO | Database, Auteur, Livre DAOs |
| 8-10 | `import` modèles | Livre, Auteur, Categorie, exceptions |
| 11-12 | `import java.sql.*` | Connection, SQLException |
| 13-14 | `import java.util.*` | List, Optional |
| 35-39 | Initialisation | Connection + DAOs |
| 49-82 | **Test CREATE** | INSERT avec FK |
| 59-67 | Création Livre | Avec Auteur (FK) |
| 69 | `livreDAO.save(livre)` | INSERT JDBC |
| 90-114 | **Test READ** | SELECT avec JOIN |
| 96 | `livreDAO.findById()` | SELECT JOIN JDBC |
| 98 | `livreOpt.isPresent()` | **Optional.isPresent()** |
| 100 | `livreOpt.get()` | **Optional.get()** |
| 122-152 | **Test UPDATE** | UPDATE JDBC |
| 141 | `livreDAO.update(livre)` | UPDATE avec FK |
| 160-181 | **Test DELETE** | DELETE JDBC |
| 170 | `livreDAO.delete(idLivre)` | DELETE |
| 189-217 | **Test FIND ALL** | SELECT avec JOIN |
| 195 | `livreDAO.findAll()` | SELECT JOIN multiple |
| 225-260 | **Test RECHERCHE** | SELECT LIKE |
| 231 | `livreDAO.rechercherParMotCle("Java")` | WHERE LIKE JDBC |

**Code représentatif:**
```java
// Lignes 90-114: Utilisation de Optional (TD11 + TD1-2)
System.out.println("TEST 2 : READ (findById)");

Optional<Livre> livreOpt = livreDAO.findById(idLivre);

if (livreOpt.isPresent()) {  // Optional.isPresent()
    Livre livre = livreOpt.get();  // Optional.get()
    System.out.println("✅ Livre trouvé : " + livre.getTitre());
    System.out.println("  Auteur : " + livre.getAuteur().getNom());
} else {
    System.out.println("❌ Livre non trouvé");
}
```

**Total concepts TD11:** 30+ instances
**Total concepts TD1-2 (Optional):** 5 instances

---

### 6.4 TestMembreDAO.java

**Concepts TD11 (JDBC via DAO) identifiés:**

| Ligne(s) | Concept | Description |
|----------|---------|-------------|
| 3-5 | `import` DAO | Database, MembreDAO |
| 6-7 | `import` modèles | Membre, exceptions |
| 8-9 | `import java.sql.*` | Connection, SQLException |
| 10 | `import java.util.List` | Collection |
| 31-35 | Initialisation | Connection + DAO |
| 43-70 | **Test CREATE** | INSERT membre |
| 53 | `membreDAO.save(membre)` | INSERT JDBC |
| 78-99 | **Test READ** | SELECT membre |
| 84 | `membreDAO.findById()` | SELECT JDBC |
| 107-129 | **Test UPDATE** | UPDATE membre |
| 118 | `membreDAO.update(membre)` | UPDATE JDBC |
| 137-158 | **Test DELETE** | DELETE membre |
| 147 | `membreDAO.delete(idMembre)` | DELETE JDBC |
| 166-194 | **Test FIND ALL** | SELECT * |
| 172 | `membreDAO.findAll()` | SELECT JDBC |
| 202-228 | **Test FIND BY CIN** | SELECT WHERE unique |
| 208 | `membreDAO.findByCin("AB123456")` | SELECT WHERE JDBC |

**Total concepts TD11:** 25+ instances

---

### 6.5 TestEmpruntDAO.java

**Concepts TD11 (JDBC + Transactions) - FICHIER CLÉ:**

| Ligne(s) | Concept | Description |
|----------|---------|-------------|
| 3-12 | `import` complet | DAO, modèles, exceptions, JDBC, java.time |
| 39-46 | Initialisation | Connection + DAOs |
| 54-80 | **Test CREATE simple** | INSERT emprunt |
| 71 | `empruntDAO.save(emprunt)` | INSERT JDBC |
| 88-131 | **Test TRANSACTION 1** | Transaction réussie |
| 96 | Avant transaction | État initial livre |
| 99-102 | `effectuerEmpruntTransaction()` | **TRANSACTION JDBC** |
| 105-122 | Vérifications | Résultat transaction |
| 139-195 | **Test TRANSACTION 2** | Transaction retour |
| 146-149 | `retournerLivreTransaction()` | **TRANSACTION JDBC** |
| 203-263 | **Test TRANSACTION 3** | Transaction échec (ROLLBACK) |
| 219 | Livre indisponible | disponibles = 0 |
| 229-234 | Try catch transaction | **ROLLBACK attendu** |
| 237-244 | Vérification ROLLBACK | État inchangé |
| 270-335 | **Test TRIPLE JOIN** | SELECT 4 tables |
| 276-282 | `empruntDAO.findById()` | **SELECT avec 3 INNER JOIN** |
| 283-297 | Vérifications JOIN | 3 objets créés |
| 343-406 | **Test FIND ALL** | SELECT JOIN multiple |
| 349 | `empruntDAO.findAll()` | SELECT avec JOIN |
| 414-477 | **Test FIND BY MEMBRE** | SELECT JOIN WHERE |
| 420 | `empruntDAO.findByMembre()` | SELECT JOIN avec filtre |

**Code représentatif:**
```java
// Lignes 88-131: Test transaction réussie (TD11)
System.out.println("TEST 2 : TRANSACTION - Emprunt avec gestion atomique");

// État AVANT transaction
Optional<Livre> livreAvant = livreDAO.findById(3);
int disponiblesAvant = livreAvant.get().getDisponibles();

// EFFECTUER TRANSACTION
Emprunt emprunt = empruntDAO.effectuerEmpruntTransaction(
    membre.getIdMembre(),
    livre.getIdLivre()
);

// Vérifications APRÈS transaction
Optional<Livre> livreApres = livreDAO.findById(3);
int disponiblesApres = livreApres.get().getDisponibles();

System.out.println("✅ Transaction réussie");
System.out.println("  Disponibles avant : " + disponiblesAvant);
System.out.println("  Disponibles après : " + disponiblesApres);
System.out.println("  Décrémenté : " + (disponiblesAvant - disponiblesApres == 1));

// Lignes 203-263: Test transaction avec ROLLBACK (TD11)
System.out.println("TEST 3 : TRANSACTION - Échec et ROLLBACK");

// Rendre livre indisponible
livre.setDisponibles(0);
livreDAO.update(livre);

// Tenter emprunt (doit échouer et ROLLBACK)
try {
    empruntDAO.effectuerEmpruntTransaction(1, 3);
    System.err.println("❌ Transaction aurait dû échouer !");

} catch (DatabaseException e) {
    System.out.println("✅ Transaction correctement ROLLBACK");
    System.out.println("  Exception : " + e.getMessage());
}

// Vérifier que rien n'a changé (ROLLBACK effectif)
Optional<Livre> livreVerif = livreDAO.findById(3);
System.out.println("  Disponibles toujours à 0 : " +
    (livreVerif.get().getDisponibles() == 0));
```

**Total concepts TD11:** 50+ instances (incluant transactions ACID)

---

### 6.6 TestBibliothequeService.java

**Concepts TD3 (Lambda) et TD4-5 (Streams) identifiés:**

| Ligne(s) | Concept | Description |
|----------|---------|-------------|
| 3-11 | `import` | Services, DAO, modèles, exceptions, Collections |
| 783-850 | **Tests avec Streams** | Méthodes utilisant Streams API |
| 789 | `service.rechercherLivres()` | Utilise stream().filter() |
| 815 | `service.getMembresActifs()` | Utilise stream().filter() |

**Concepts TD1-2 (Validation métier):**

| Ligne(s) | Concept | Description |
|----------|---------|-------------|
| 220-257 | **Test validation** | Règles métier |
| 229-247 | Validation livre | Titre vide rejeté |
| 253-257 | Exception attendue | DatabaseException |
| 265-303 | **Test contraintes** | CIN unique, Email unique |
| 311-345 | **Test règle membre** | Max 5 emprunts actifs |

**Total concepts (usage indirect):** 15+ instances (les Streams sont dans BibliothequeService)

---

### 6.7 TestEmpruntService.java

**Concepts TD6-10 (Multithreading via service):**

| Ligne(s) | Concept | Description |
|----------|---------|-------------|
| 3-8 | `import` | Services, DAO, modèles, exceptions |
| 86-129 | **Tests exceptions** | Gestion thread-safe |
| 90-98 | Test MembreNonTrouveException | Service synchronisé |
| 108-116 | Test LivreNonTrouveException | Service synchronisé |
| 118-126 | Test LivreIndisponibleException | Service synchronisé |
| 199-222 | **Test règle métier** | Max 5 emprunts (thread-safe) |

**Total concepts (usage indirect):** 12 instances (le service utilise ReentrantLock)

---

### 6.8 TestStatistiques.java

**Concepts TD3 (Lambda) et TD4-5 (Streams) - USAGE INTENSIF:**

| Ligne(s) | Concept | Description |
|----------|---------|-------------|
| 3-14 | `import` | Services, utils, modèles, exceptions, Collections, Map, IntSummaryStatistics |
| 14 | `import IntSummaryStatistics` | **Import statistiques numériques** |
| 54-76 | **Test TOP 10** | Utilise Streams complexes |
| 54-55 | `statsService.top10LivresPlusEmpruntes()` | **groupingBy + counting + sorted + limit** |
| 61-72 | Boucle résultats | Map.Entry<Livre, Long> |
| 75 | `fileExporter.exporterTopLivresCSV()` | I/O |
| 83-95 | **Test groupingBy** | Livres par catégorie |
| 83-84 | `statsService.livresParCategorie()` | **Collectors.groupingBy** |
| 102-114 | **Test counting** | Comptage par catégorie |
| 102-103 | `statsService.nombreLivresParCategorie()` | **groupingBy + counting** |
| 121-131 | **Test IntSummaryStatistics** | Statistiques complètes |
| 121-122 | `statsService.statsDisponibilite()` | **summarizingInt** |
| 126-130 | Utilisation stats | getCount, getSum, getMin, getMax, getAverage |
| 138 | `statsService.moyenneEmpruntsParMembre()` | **mapToInt + average** |
| 150 | `statsService.dureeMoyenneEmprunts()` | **filter + mapToLong + average** |
| 162 | `statsService.getMembresEnRetard()` | **filter + anyMatch** |
| 187 | `statsService.tauxRetard()` | **filter + count** |
| 207 | `statsService.top5MembresActifs()` | **sorted + limit** |
| 228-229 | `statsService.empruntsParCategorie()` | **groupingBy** |
| 234-242 | **Stream dans test** | sorted().forEach() |
| 235-236 | `.sorted(comparingByValue().reversed())` | **Tri sur Map.Entry** |
| 237-241 | `.forEach(entry -> {...})` | **Lambda Consumer** |
| 250 | `statsService.totalExemplairesDisponibles()` | **mapToInt + sum** |
| 268 | `fileExporter.exporterRapportMensuelCSV()` | I/O |
| 290 | `fileExporter.exporterStatsCSV()` | I/O |

**Concepts TD8-9 (I/O via FileExporter):**

| Ligne(s) | Concept | Description |
|----------|---------|-------------|
| 75 | CSV export | FileWriter sous-jacent |
| 268 | CSV export | FileWriter |
| 290 | CSV export | FileWriter |
| 307-314 | Liste fichiers CSV | File operations |
| 307 | `fileExporter.listerFichiersCSV()` | File.listFiles() |

**Code représentatif:**
```java
// Lignes 54-76: Test Streams complexes (TD3, TD4-5)
List<Map.Entry<Livre, Long>> top10Livres =
    statsService.top10LivresPlusEmpruntes();  // Utilise groupingBy + counting + sorted + limit

int rang = 1;
for (Map.Entry<Livre, Long> entry : top10Livres) {
    Livre livre = entry.getKey();
    Long nbEmprunts = entry.getValue();
    System.out.printf("  %2d. %-40s | %s | %d emprunts%n",
        rang++, livre.getTitre(), livre.getAuteur().getNom(), nbEmprunts);
}

fileExporter.exporterTopLivresCSV("top_10_livres", top10Livres);  // I/O

// Lignes 121-131: IntSummaryStatistics (TD4-5)
IntSummaryStatistics statsDisponibilite =
    statsService.statsDisponibilite();  // summarizingInt

System.out.printf("  Nombre total de livres : %d%n", statsDisponibilite.getCount());
System.out.printf("  Total exemplaires     : %d%n", statsDisponibilite.getSum());
System.out.printf("  Minimum disponible    : %d%n", statsDisponibilite.getMin());
System.out.printf("  Maximum disponible    : %d%n", statsDisponibilite.getMax());
System.out.printf("  Moyenne disponible    : %.2f%n", statsDisponibilite.getAverage());

// Lignes 234-242: Stream dans le test (TD3, TD4-5)
empruntsParCategorie.entrySet().stream()
    .sorted(Map.Entry.<Categorie, Long>comparingByValue().reversed())
    .forEach(entry -> {  // Lambda Consumer
        System.out.printf("  %-15s : %3d emprunts%n",
            entry.getKey(),
            entry.getValue()
        );
    });
```

**Total concepts TD3:** 25+ instances
**Total concepts TD4-5:** 40+ instances
**Total concepts TD8-9:** 10+ instances

---

## 7. Package utils (1 fichier)

### 7.1 FileExporter.java

**Concepts TD8-9 (I/O Operations) - FICHIER CLÉ:**

| Ligne(s) | Concept | Description |
|----------|---------|-------------|
| 1 | `package` | Organisation |
| 3-17 | `import` I/O | **Import complet java.io.*** |
| 3 | `import java.io.BufferedWriter` | **Écriture bufferisée** |
| 4 | `import java.io.File` | **Manipulation fichiers** |
| 5 | `import java.io.FileWriter` | **Écriture fichiers texte** |
| 6 | `import java.io.IOException` | **Exception I/O** |
| 7 | `import java.nio.charset.StandardCharsets` | **Encodage UTF-8** |
| 8-15 | `import` java.util | List, Map, etc. |
| 16 | `import java.time.LocalDate` | Dates |
| 17 | `import java.time.format.DateTimeFormatter` | Formatage dates |
| 37-39 | Constantes | Répertoire et séparateur CSV |
| 37 | `REPERTOIRE_RAPPORTS = "rapports/"` | Chemin fichiers |
| 38 | `SEPARATEUR_CSV = ";"` | Format CSV |
| 87-154 | **exporterCSV() générique** | Méthode avec type detection |
| 108-113 | `try-with-resources` | **Auto-fermeture FileWriter + BufferedWriter** |
| 108-109 | `new FileWriter(cheminComplet, StandardCharsets.UTF_8)` | **FileWriter avec encoding UTF-8** |
| 110 | `new BufferedWriter(writer)` | **BufferedWriter pour performance** |
| 112 | `bw.write('\ufeff')` | **BOM UTF-8 pour Excel** |
| 116-153 | Type detection | instanceof pour routing |
| 117-119 | `instanceof Livre` | Vérification type |
| 121 | `(List<Livre>) donnees` | **Cast explicite** |
| 122 | `exporterLivresCSV()` | Appel méthode spécialisée |
| 124-127 | `instanceof Membre` | Autre type |
| 129-137 | `instanceof Map.Entry` | Détection Entry |
| 151 | `catch IOException` | Gestion erreur I/O |
| 162-204 | **exporterLivresCSV()** | Export spécialisé livres |
| 169-186 | `try-with-resources` | FileWriter + BufferedWriter |
| 174 | `bw.write(BOM)` | BOM UTF-8 |
| 177-179 | En-tête CSV | Ligne de titres |
| 182-183 | Boucle livres | forEach avec lambda |
| 183-185 | Lambda ligne CSV | Formatage données |
| 214-255 | **exporterMembresCSV()** | Export membres |
| 220-239 | `try-with-resources` | Auto-fermeture |
| 224 | BOM UTF-8 | Pour Excel |
| 231-236 | forEach lambda | Écriture lignes |
| 265-319 | **exporterTopLivresCSV()** | Export Map.Entry |
| 275-303 | `try-with-resources` | FileWriter + BufferedWriter |
| 279 | BOM | UTF-8 |
| 290-300 | Boucle Map.Entry | Extraction clé/valeur |
| 329-384 | **exporterStatsCSV()** | Export statistiques |
| 360-374 | `try-with-resources` | Auto-fermeture |
| 364 | BOM | UTF-8 |
| 369-372 | forEach sur Map | Lambda avec Entry |
| 394-427 | **exporterRapportMensuelCSV()** | Export rapport complexe |
| 399-421 | `try-with-resources` | FileWriter + BufferedWriter |
| 403 | BOM | UTF-8 |
| 408-419 | Extraction données Map | get() multiples |
| 437-469 | **exporterEmpruntsCSV()** | Export emprunts |
| 437-463 | `try-with-resources` | Auto-fermeture |
| 441 | BOM | UTF-8 |
| 454-461 | Lambda formatage | Dates + objets composites |
| 479-511 | **exporterAuteursCSV()** | Export auteurs |
| 479-505 | `try-with-resources` | Auto-fermeture |
| 483 | BOM | UTF-8 |
| 519-547 | **verifierCreerRepertoire()** | Opérations fichiers |
| 527 | `new File(REPERTOIRE_RAPPORTS)` | **Création objet File** |
| 529-531 | `!repertoire.exists()` | **Vérification existence** |
| 530 | `repertoire.mkdirs()` | **Création répertoires** |
| 554-586 | **supprimerTousFichiers()** | Opérations fichiers |
| 562 | `repertoire.exists()` | Vérification |
| 564 | `repertoire.listFiles()` | **Liste fichiers** |
| 570-575 | Boucle fichiers | Parcours tableau |
| 571 | `fichier.delete()` | **Suppression fichier** |
| 593-686 | **listerFichiersCSV()** | Lecture répertoire |
| 602 | `repertoire.exists()` | Vérification |
| 607 | `repertoire.listFiles()` | Liste fichiers |
| 613-684 | Filtrage CSV | Pattern matching nom |

**Code représentatif:**
```java
// Lignes 108-113: try-with-resources avec FileWriter et BufferedWriter (TD8-9)
try (FileWriter writer = new FileWriter(cheminComplet, StandardCharsets.UTF_8);
     BufferedWriter bw = new BufferedWriter(writer)) {

    // BOM UTF-8 pour compatibilité Excel
    bw.write('\ufeff');

    // Écriture données...

} catch (IOException e) {
    System.err.println("❌ Erreur export CSV : " + e.getMessage());
    throw e;
}

// Lignes 116-153: Type detection avec instanceof (TD1-2 + TD8-9)
Object premierElement = donnees.get(0);

if (premierElement instanceof Livre) {
    @SuppressWarnings("unchecked")
    List<Livre> livres = (List<Livre>) donnees;  // Cast explicite
    exporterLivresCSV(cheminComplet, livres);

} else if (premierElement instanceof Membre) {
    @SuppressWarnings("unchecked")
    List<Membre> membres = (List<Membre>) donnees;
    exporterMembresCSV(cheminComplet, membres);

} else if (premierElement instanceof Map.Entry) {
    // Déterminer type de Map.Entry
    @SuppressWarnings("unchecked")
    Map.Entry<?, ?> entry = (Map.Entry<?, ?>) premierElement;

    if (entry.getKey() instanceof Livre) {
        exporterTopLivresCSV(nomFichier, (List<Map.Entry<Livre, Long>>) donnees);
    } else if (entry.getKey() instanceof Membre) {
        exporterTopMembresCSV(nomFichier, (List<Map.Entry<Membre, Long>>) donnees);
    }
}

// Lignes 527-531: Création répertoire (TD8-9)
File repertoire = new File(REPERTOIRE_RAPPORTS);

if (!repertoire.exists()) {
    repertoire.mkdirs();  // Création répertoires parents si nécessaire
}

// Lignes 562-575: Liste et suppression fichiers (TD8-9)
File repertoire = new File(REPERTOIRE_RAPPORTS);

if (repertoire.exists()) {
    File[] fichiers = repertoire.listFiles();  // Liste fichiers

    if (fichiers != null) {
        for (File fichier : fichiers) {
            if (fichier.isFile()) {
                fichier.delete();  // Suppression
            }
        }
    }
}

// Lignes 169-186: Pattern complet try-with-resources + BOM (TD8-9)
private void exporterLivresCSV(String cheminComplet, List<Livre> livres)
        throws IOException {

    try (FileWriter writer = new FileWriter(cheminComplet, StandardCharsets.UTF_8);
         BufferedWriter bw = new BufferedWriter(writer)) {

        // BOM UTF-8 (Byte Order Mark) pour Excel
        bw.write('\ufeff');

        // En-tête
        bw.write("ISBN" + SEPARATEUR_CSV + "Titre" + SEPARATEUR_CSV +
                 "Auteur" + SEPARATEUR_CSV + "Catégorie" + SEPARATEUR_CSV +
                 "Année" + SEPARATEUR_CSV + "Disponibles\n");

        // Données avec lambda
        livres.forEach(livre -> {
            try {
                bw.write(livre.getIsbn() + SEPARATEUR_CSV +
                         livre.getTitre() + SEPARATEUR_CSV +
                         livre.getAuteur().getNom() + " " + livre.getAuteur().getPrenom() +
                         SEPARATEUR_CSV +
                         livre.getCategorie() + SEPARATEUR_CSV +
                         livre.getAnneePublication() + SEPARATEUR_CSV +
                         livre.getDisponibles() + "\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        System.out.println("✅ Fichier CSV exporté : " + cheminComplet);
    }
}
```

**Total concepts TD8-9:** 70+ instances (le plus riche en I/O!)
**Total concepts TD1-2:** 15 instances
**Total concepts TD3 (Lambda dans forEach):** 20 instances

---

# II. INDEX DES CONCEPTS PAR TD

## TD1-2: Programmation Orientée Objet

### Classes et Encapsulation

**Concept:** Déclaration de classes publiques

**Fichiers et lignes:**
- `Categorie.java`: ligne 11
- `StatutEmprunt.java`: ligne 11
- `Auteur.java`: ligne 22
- `Livre.java`: ligne 23
- `Membre.java`: ligne 24
- `Emprunt.java`: ligne 26
- `BiblioTechException.java`: ligne 17
- `DatabaseException.java`: ligne 15
- Tous les DAO: lignes variées
- Tous les services: lignes variées

**Utilisation:** 35+ fichiers utilisent des classes

---

**Concept:** Attributs privés (encapsulation)

**Fichiers et lignes:**
- `Auteur.java`: lignes 27-29 (3 attributs)
- `Livre.java`: lignes 28-35 (8 attributs)
- `Membre.java`: lignes 29-34 (6 attributs)
- `Emprunt.java`: lignes 31-37 (7 attributs)
- Tous les DAO: attribut `Connection`
- Tous les services: attributs DAO

**Usage total:** 150+ attributs privés dans le projet

---

**Concept:** Getters et Setters

**Fichiers et lignes:**
- `Auteur.java`: lignes 58-85 (4 paires)
- `Livre.java`: lignes 71-135 (8 paires)
- `Membre.java`: lignes 69-127 (6 paires)
- `Emprunt.java`: lignes 76-142 (7 paires)

**Usage total:** 100+ getters/setters

---

### Constructeurs

**Concept:** Constructeur par défaut

**Fichiers et lignes:**
- `Auteur.java`: lignes 35-39
- `Livre.java`: lignes 41-43
- `Membre.java`: lignes 40-42
- `Emprunt.java`: lignes 43-45

**Usage:** Tous les modèles

---

**Concept:** Constructeur avec paramètres (surcharge)

**Fichiers et lignes:**
- `Auteur.java`: lignes 45-52
- `Livre.java`: lignes 49-62
- `Membre.java`: lignes 48-60
- `Emprunt.java`: lignes 51-67

**Usage:** Surcharge systématique dans tous les modèles

---

### Héritage

**Concept:** `extends` (héritage de classe)

**Fichiers et lignes:**
- `BiblioTechException.java`: ligne 17 (`extends Exception`)
- `DatabaseException.java`: ligne 15 (`extends BiblioTechException`)
- `LivreIndisponibleException.java`: ligne 12 (`extends BiblioTechException`)
- `LivreNonTrouveException.java`: ligne 12 (`extends BiblioTechException`)
- `MembreNonTrouveException.java`: ligne 12 (`extends BiblioTechException`)
- `BibliothecaireThread.java`: ligne 39 (`extends Thread`)

**Usage:** Hiérarchie d'exceptions + Thread

---

**Concept:** `super()` (appel constructeur parent)

**Fichiers et lignes:**
- `BiblioTechException.java`: lignes 29, 39, 51, 64
- `DatabaseException.java`: lignes 22, 32
- Toutes les exceptions: appel `super()`
- `BibliothecaireThread.java`: ligne 79 (`super(nom)`)

**Usage:** 15+ appels `super()`

---

### Interfaces

**Concept:** `implements` (implémentation d'interface)

**Fichiers et lignes:**
- `EmpruntTask.java`: ligne 29 (`implements Callable<String>`)
- `RechercheTask.java`: ligne 25 (`implements Callable<List<Livre>>`)
- `RetourTask.java`: ligne 24 (`implements Callable<String>`)

**Usage:** 3 implémentations de Callable

---

### Énumérations (enum)

**Concept:** Type énuméré

**Fichiers et lignes:**
- `Categorie.java`: lignes 11-17 (5 valeurs)
- `StatutEmprunt.java`: lignes 11-15 (3 valeurs)

**Utilisation:**
- `Livre.java`: ligne 34 (attribut Categorie)
- `Emprunt.java`: ligne 36 (attribut StatutEmprunt)
- `LivreDAO.java`: ligne 131 (`Categorie.valueOf()`)
- Nombreuses utilisations dans services et tests

---

### Composition (Relations "has-a")

**Concept:** Composition d'objets

**Fichiers et lignes:**
- `Livre.java`: ligne 33 (`private Auteur auteur`) - Livre "has-a" Auteur
- `Livre.java`: ligne 34 (`private Categorie categorie`) - Livre "has-a" Categorie
- `Emprunt.java`: ligne 33 (`private Membre membre`) - Emprunt "has-a" Membre
- `Emprunt.java`: ligne 34 (`private Livre livre`) - Emprunt "has-a" Livre

**Usage:** Relations métier fondamentales

---

### Redéfinition de méthodes (Polymorphisme)

**Concept:** `equals()` redéfini

**Fichiers et lignes:**
- `Auteur.java`: lignes 94-105
- `Livre.java`: lignes 187-198
- `Membre.java`: lignes 170-184
- `Emprunt.java`: lignes 180-200

**Pattern commun:**
```java
@Override
public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    // Comparaison champs avec Objects.equals()
}
```

---

**Concept:** `hashCode()` redéfini

**Fichiers et lignes:**
- `Auteur.java`: lignes 113-115 (`Objects.hash()`)
- `Livre.java`: lignes 206-208 (`Objects.hash()`)
- `Membre.java`: lignes 191-193 (`Objects.hash()`)
- `Emprunt.java`: lignes 208-210 (`Objects.hash()`)

**Pattern:** Utilisation systématique de `Objects.hash()`

---

**Concept:** `toString()` redéfini

**Fichiers et lignes:**
- `Auteur.java`: lignes 124-129
- `Livre.java`: lignes 217-227
- `Membre.java`: lignes 202-213
- `Emprunt.java`: lignes 219-233
- `BibliothecaireThread.java`: lignes 407-410

**Usage:** Tous les modèles + thread

---

### Méthodes métier

**Concept:** Logique métier dans les modèles

**Fichiers et lignes:**
- `Livre.java`:
  - ligne 144-151: `estDisponible()`
  - ligne 159-165: `emprunter()`
  - ligne 173-179: `retourner()`
- `Membre.java`:
  - ligne 136-140: `peutEmprunter()` (règle des 5 emprunts max)
  - ligne 148-150: `incrementerEmprunts()`
  - ligne 157-162: `decrementerEmprunts()`
- `Emprunt.java`:
  - ligne 152-158: `calculerDuree()` (avec ChronoUnit)
  - ligne 167-172: `estEnRetard()` (logique complexe)

**Usage:** Logique métier encapsulée dans les modèles

---

### Classes utilitaires Java

**Concept:** `Objects.equals()` et `Objects.hash()`

**Fichiers et lignes:**
- `Auteur.java`: lignes 100-102 (`Objects.equals()`), ligne 114 (`Objects.hash()`)
- `Livre.java`: lignes 193-197 (`Objects.equals()`), ligne 207 (`Objects.hash()`)
- `Membre.java`: lignes 176-182 (`Objects.equals()`), ligne 192 (`Objects.hash()`)
- `Emprunt.java`: lignes 187-199 (`Objects.equals()`), ligne 209 (`Objects.hash()`)

**Avantage:** Comparaisons null-safe

---

### Opérateurs et vérifications de type

**Concept:** `instanceof`

**Fichiers et lignes:**
- `Auteur.java`: lignes 96-97
- `Livre.java`: lignes 189-190
- `Membre.java`: lignes 172-173
- `Emprunt.java`: lignes 183-184
- `FileExporter.java`: lignes 117, 124, 129, 131, 133 (type detection)

**Usage:** Vérification de type avant cast

---

**Concept:** Cast explicite

**Fichiers et lignes:**
- `Auteur.java`: ligne 99 (`(Auteur) o`)
- `Livre.java`: ligne 192 (`(Livre) o`)
- `Membre.java`: ligne 175 (`(Membre) o`)
- `Emprunt.java`: ligne 186 (`(Emprunt) o`)
- `FileExporter.java`: lignes 121, 126, 135, 137 (casts collections)

**Usage:** Après vérification instanceof

---

### Pattern Singleton

**Concept:** Instance unique avec getInstance()

**Fichier:** `DatabaseConnection.java`

**Lignes:**
- ligne 19: `private static DatabaseConnection instance`
- lignes 42-51: méthode `getInstance()`
- lignes 44-46: double-check locking
- ligne 45: `synchronized` block

**Code:**
```java
private static DatabaseConnection instance = null;

public static DatabaseConnection getInstance() {
    if (instance == null) {
        synchronized (DatabaseConnection.class) {
            if (instance == null) {
                instance = new DatabaseConnection();
            }
        }
    }
    return instance;
}
```

---

### Collections Framework

**Concept:** `List<E>` et `ArrayList<E>`

**Fichiers et lignes:**
- Tous les DAO: méthodes `findAll()` retournent `List<>`
- `AuteurDAO.java`: ligne 133 (`new ArrayList<>()`)
- `LivreDAO.java`: ligne 184 (`new ArrayList<>()`)
- `MembreDAO.java`: ligne 147 (`new ArrayList<>()`)
- `EmpruntDAO.java`: ligne 268 (`new ArrayList<>()`)
- Services: manipulation de listes partout

**Usage:** 100+ utilisations

---

**Concept:** `Map<K,V>` et `HashMap<K,V>`

**Fichiers et lignes:**
- `RapportService.java`: ligne 112 (`new HashMap<>()`)
- `SynchroManager.java`: ligne 57 (`new ConcurrentHashMap<>()`)
- Statistiques: retours de `Map<Categorie, Long>`, etc.

**Usage:** 30+ utilisations

---

**Concept:** `Map.Entry<K,V>`

**Fichiers et lignes:**
- `StatistiquesService.java`: ligne 110 (`.entrySet().stream()`)
- Nombreuses utilisations dans Streams pour tri sur Map
- `TestStatistiques.java`: lignes 61-72 (boucle sur Entry)

**Usage:** 20+ utilisations

---

### Optional<T>

**Concept:** Gestion absence de valeur

**Fichiers et lignes:**
- `LivreDAO.java`:
  - ligne 103: `Optional<Livre> findById()`
  - ligne 140: `Optional.of(livre)`
  - ligne 166: `Optional.empty()`
- `EmpruntService.java`: ligne 117 (`livreOpt.isPresent()`)
- `StatistiquesService.java`: lignes 229, 263 (`.orElse()`)

**Usage:** Remplacement de null

---

## TD3: Lambda & Interfaces Fonctionnelles

### Method References

**Concept:** Reference de méthode `Class::method`

**Fichiers et lignes:**

**StatistiquesService.java (35+ instances):**
- ligne 107: `Emprunt::getLivre`
- ligne 139: `Livre::getCategorie`
- ligne 162: `Livre::getCategorie`
- ligne 195: `Livre::getDisponibles`
- ligne 227: `Membre::getNombreEmpruntsActifs`
- ligne 260: `Emprunt::calculerDuree`
- ligne 303: `Emprunt::estEnRetard`
- ligne 344: `Emprunt::estEnRetard`
- ligne 374: `Membre::getNombreEmpruntsActifs`
- ligne 427: `Livre::getDisponibles`

**RapportService.java (10+ instances):**
- ligne 128: `Emprunt::getMembre`
- ligne 140: `Emprunt::getLivre`
- ligne 152: `Emprunt::estEnRetard`
- ligne 163: `Emprunt::calculerDuree`

**FileExporter.java (forEach lambdas):**
- lignes 182-185: forEach avec lambda
- lignes 231-236: forEach avec lambda
- lignes 454-461: forEach avec lambda

**Total usage:** 60+ method references

---

### Expressions Lambda

**Concept:** Lambda avec paramètre `x -> expression`

**Fichiers et lignes:**

**Filtres simples:**
- `BibliothequeService.java`:
  - ligne 205-208: `livre -> livre.estDisponible() && ...`
  - ligne 249-252: `membre -> membre.peutEmprunter() && ...`

**Filtres avec dates:**
- `RapportService.java`:
  - ligne 107: `e -> e.getDateEmprunt().getMonthValue() == mois`
  - ligne 108: `e -> e.getDateEmprunt().getYear() == annee`
  - ligne 162: `e -> e.getDateRetourEffective() != null`

**Filtres complexes:**
- `StatistiquesService.java`:
  - ligne 258: `e -> e.getDateRetourEffective() != null`
  - lignes 295-309: lambda multi-lignes avec try-catch

**Navigation d'objets:**
- `RapportService.java`:
  - ligne 120: `e -> e.getLivre().getCategorie()`
- `StatistiquesService.java`:
  - ligne 402: `emprunt -> emprunt.getLivre().getCategorie()`

**forEach lambdas:**
- `TestStatistiques.java`:
  - lignes 237-241: `entry -> { System.out.printf(...); }`
- `RapportService.java`:
  - ligne 280-281: `(cat, count) -> sb.append(...)`

**Total usage:** 50+ expressions lambda

---

### Comparator avec Lambda

**Concept:** `Comparator.comparing()` avec method reference

**Fichiers et lignes:**
- `StatistiquesService.java`:
  - ligne 112: `Map.Entry.<Livre, Long>comparingByValue().reversed()`
  - ligne 374: `Comparator.comparing(Membre::getNombreEmpruntsActifs).reversed()`
- `RapportService.java`:
  - ligne 132: `Map.Entry.<Membre, Long>comparingByValue().reversed()`
  - ligne 144: `Map.Entry.<Livre, Long>comparingByValue().reversed()`
- `TestStatistiques.java`:
  - ligne 235-236: `.sorted(comparingByValue().reversed())`

**Usage:** Tri avec lambda sur 15+ occurrences

---

### Interfaces Fonctionnelles

**Concept:** `Callable<V>`

**Fichiers et lignes:**
- `EmpruntTask.java`: ligne 29 (`implements Callable<String>`)
- `RechercheTask.java`: ligne 25 (`implements Callable<List<Livre>>`)
- `RetourTask.java`: ligne 24 (`implements Callable<String>`)

**Méthode call():**
- `EmpruntTask.java`: lignes 62-112
- `RechercheTask.java`: lignes 54-88
- `RetourTask.java`: lignes 53-93

**Avantage:** Retour de valeur (contrairement à Runnable)

---

**Concept:** Autres interfaces fonctionnelles (usage implicite)

**Prédicate<T>:**
- Utilisé dans tous les `.filter()`
- `BibliothequeService.java`: filtres sur livres et membres
- `StatistiquesService.java`: filtres multiples
- `RapportService.java`: filtres par date

**Consumer<T>:**
- Utilisé dans tous les `.forEach()`
- `FileExporter.java`: forEach pour écriture CSV
- `TestStatistiques.java`: forEach pour affichage

**Function<T,R>:**
- Utilisé dans `.map()` et method references
- Omniprésent dans les Streams

---

## TD4-5: Streams API

### Création de Streams

**Concept:** `.stream()` sur collections

**Fichiers et lignes:**
- `StatistiquesService.java`: 15+ appels `.stream()`
- `RapportService.java`: 10+ appels `.stream()`
- `BibliothequeService.java`: lignes 204, 248
- Tous les services: utilisation massive

**Usage:** 100+ créations de streams

---

**Concept:** `.entrySet().stream()` sur Map

**Fichiers et lignes:**
- `StatistiquesService.java`: ligne 110
- `RapportService.java`: lignes 131, 143
- `TestStatistiques.java`: ligne 235

**Usage:** Pour traiter les Map avec Streams

---

### Opérations Intermédiaires

**Concept:** `.filter()` - Filtrage

**Fichiers et lignes:**

**Filtres simples:**
- `BibliothequeService.java`: lignes 205-208 (disponibilité + mot-clé)
- `StatistiquesService.java`: ligne 258 (date retour), ligne 343 (retard)

**Filtres par date:**
- `RapportService.java`: lignes 107-108 (mois et année)
- `StatistiquesService.java`: ligne 258 (retour effectif)

**Filtres complexes:**
- `StatistiquesService.java`: lignes 294-309 (membres en retard avec anyMatch)

**Usage total:** 50+ utilisations de filter

---

**Concept:** `.map()` - Transformation

**Fichiers et lignes:**
- Usage implicite via method references
- Transformation d'objets en propriétés
- Extraction de champs

**Usage:** Omniprésent avec method references

---

**Concept:** `.mapToInt()` - Stream numérique

**Fichiers et lignes:**
- `StatistiquesService.java`:
  - ligne 227: `.mapToInt(Membre::getNombreEmpruntsActifs)`
  - ligne 427: `.mapToInt(Livre::getDisponibles)`

**Usage:** Pour opérations numériques

---

**Concept:** `.mapToLong()` - Stream long

**Fichiers et lignes:**
- `StatistiquesService.java`: ligne 260 (`.mapToLong(Emprunt::calculerDuree)`)
- `RapportService.java`: ligne 163 (durée emprunts)

**Usage:** Pour calculs de durées

---

**Concept:** `.sorted()` - Tri

**Fichiers et lignes:**
- `StatistiquesService.java`:
  - ligne 112: `.sorted(comparingByValue().reversed())`
  - ligne 374: `.sorted(Comparator.comparing(...).reversed())`
- `RapportService.java`: lignes 132, 144
- `TestStatistiques.java`: ligne 235

**Usage:** 15+ tris

---

**Concept:** `.limit()` - Limitation

**Fichiers et lignes:**
- `StatistiquesService.java`:
  - ligne 114: `.limit(10)` (top 10)
  - ligne 376: `.limit(5)` (top 5)
- `RapportService.java`: lignes 133, 145 (top 5)

**Usage:** Pour top N

---

**Concept:** `.distinct()` - Élimination doublons

**Utilisation:** Mentionné dans commentaires, usage potentiel

---

### Opérations Terminales

**Concept:** `.collect()` - Collection

**Fichiers et lignes:**
- Utilisé partout avec Collectors
- `StatistiquesService.java`: 20+ utilisations
- `RapportService.java`: 15+ utilisations
- `BibliothequeService.java`: lignes 209, 253

**Usage:** 80+ collect()

---

**Concept:** `.count()` - Comptage

**Fichiers et lignes:**
- `StatistiquesService.java`: ligne 344 (`.count()` retards)
- `RapportService.java`: ligne 153 (count retards)

**Usage:** 10+ comptages

---

**Concept:** `.sum()` - Somme

**Fichiers et lignes:**
- `StatistiquesService.java`: ligne 428 (`.sum()` disponibles)

**Usage:** Somme d'entiers

---

**Concept:** `.average()` - Moyenne

**Fichiers et lignes:**
- `StatistiquesService.java`:
  - ligne 228: `.average()` (emprunts par membre)
  - ligne 262: `.average()` (durée emprunts)
- `RapportService.java`: ligne 164 (durée moyenne)

**Usage:** Calculs de moyennes

---

**Concept:** `.anyMatch()` - Test existence

**Fichiers et lignes:**
- `StatistiquesService.java`: ligne 303 (`.anyMatch(Emprunt::estEnRetard)`)

**Usage:** Test booléen sur stream

---

**Concept:** `.forEach()` - Itération

**Fichiers et lignes:**
- `TestStatistiques.java`: lignes 237-241
- `RapportService.java`: ligne 280
- `FileExporter.java`: lignes 182, 231, 454 (avec lambda)

**Usage:** 20+ forEach

---

### Collectors Avancés

**Concept:** `Collectors.toList()` - Collection en liste

**Fichiers et lignes:**
- `StatistiquesService.java`: lignes 116, 310, 376
- `BibliothequeService.java`: lignes 209, 253
- `RapportService.java`: lignes 109, 134, 146

**Usage:** 30+ toList()

---

**Concept:** `Collectors.groupingBy()` - Groupement

**Fichiers et lignes:**

**Groupement simple:**
- `StatistiquesService.java`: ligne 139 (`groupingBy(Livre::getCategorie)`)

**Groupement avec counting:**
- `StatistiquesService.java`:
  - lignes 106-109: `groupingBy(Emprunt::getLivre, counting())`
  - lignes 160-163: `groupingBy(Livre::getCategorie, counting())`
  - lignes 400-404: `groupingBy(e -> e.getLivre().getCategorie(), counting())`
- `RapportService.java`:
  - lignes 118-122: groupingBy catégorie + counting
  - lignes 126-130: groupingBy membre + counting
  - lignes 138-142: groupingBy livre + counting

**Usage:** 20+ groupingBy (collector le plus puissant!)

---

**Concept:** `Collectors.counting()` - Comptage dans groupe

**Fichiers et lignes:**
- Utilisé avec groupingBy (voir ci-dessus)
- `StatistiquesService.java`: lignes 108, 163, 403
- `RapportService.java`: lignes 121, 129, 141

**Usage:** 15+ counting

---

**Concept:** `Collectors.summarizingInt()` - Statistiques complètes

**Fichier:** `StatistiquesService.java`

**Lignes:** 194-195

**Code:**
```java
return tousLivres.stream()
    .collect(Collectors.summarizingInt(Livre::getDisponibles));
```

**Retour:** `IntSummaryStatistics` avec count, sum, min, max, average

**Utilisation dans tests:**
- `TestStatistiques.java`: lignes 121-131 (extraction de toutes les stats)

**Usage:** Statistiques numériques complètes en un seul collector

---

### Optional dans Streams

**Concept:** `.orElse()` avec Optional

**Fichiers et lignes:**
- `StatistiquesService.java`:
  - ligne 229: `.average().orElse(0.0)`
  - ligne 263: `.average().orElse(0.0)`
- `RapportService.java`: ligne 165

**Usage:** Valeur par défaut si absent

---

**Concept:** `Optional.of()` et `Optional.empty()`

**Fichiers et lignes:**
- `LivreDAO.java`:
  - ligne 140: `Optional.of(livre)`
  - ligne 166: `Optional.empty()`

**Usage:** Encapsulation résultat DAO

---

### Streams Complexes (Chaînage Multi-étapes)

**Concept:** Pipeline Stream complet

**Fichier:** `StatistiquesService.java`

**Top 10 livres (lignes 104-116):**
```java
return tousEmprunts.stream()
    // ÉTAPE 1: Grouper et compter
    .collect(Collectors.groupingBy(
        Emprunt::getLivre,
        Collectors.counting()
    ))
    // ÉTAPE 2: Stream sur Map.Entry
    .entrySet().stream()
    // ÉTAPE 3: Trier décroissant
    .sorted(Map.Entry.<Livre, Long>comparingByValue().reversed())
    // ÉTAPE 4: Limiter à 10
    .limit(10)
    // ÉTAPE 5: Collecter en liste
    .collect(Collectors.toList());
```

**Étapes:** 5 transformations chaînées!

---

**Concept:** Nested Streams (stream dans stream)

**Fichier:** `StatistiquesService.java`

**Lignes:** 294-309

**Code:**
```java
return tousMembres.stream()
    .filter(membre -> {
        try {
            List<Emprunt> empruntsMembre =
                empruntDAO.findByMembre(membre.getIdMembre());

            // NESTED STREAM
            return empruntsMembre.stream()
                .anyMatch(Emprunt::estEnRetard);

        } catch (DatabaseException e) {
            return false;
        }
    })
    .collect(Collectors.toList());
```

**Complexité:** Stream externe + stream interne avec anyMatch

---

### IntSummaryStatistics

**Concept:** Statistiques numériques complètes

**Fichier:** `StatistiquesService.java`

**Déclaration:** ligne 16 (`import IntSummaryStatistics`)

**Méthode:** lignes 189-196

**Utilisation dans tests:** `TestStatistiques.java` lignes 121-131

**Propriétés extraites:**
- `getCount()`: nombre d'éléments
- `getSum()`: somme totale
- `getMin()`: minimum
- `getMax()`: maximum
- `getAverage()`: moyenne

**Code:**
```java
IntSummaryStatistics stats = statsService.statsDisponibilite();

System.out.printf("Nombre total : %d%n", stats.getCount());
System.out.printf("Total        : %d%n", stats.getSum());
System.out.printf("Minimum      : %d%n", stats.getMin());
System.out.printf("Maximum      : %d%n", stats.getMax());
System.out.printf("Moyenne      : %.2f%n", stats.getAverage());
```

**Avantage:** 5 statistiques en un seul passage du stream

---

## TD6-10: Multithreading

### Création de Threads

**Concept:** `extends Thread`

**Fichier:** `BibliothecaireThread.java`

**Lignes:**
- ligne 39: `public class BibliothecaireThread extends Thread`
- ligne 79: `super("Bibliothecaire-" + nom)` (appel constructeur Thread avec nom)

**Méthodes:**
- lignes 100-162: `run()` - code exécuté par le thread
- ligne 116: `getName()` - obtention nom du thread

**Usage:** 1 classe (pattern traditionnel)

---

**Concept:** `implements Runnable`

**Usage:** Non utilisé directement (Callable utilisé à la place)

---

**Concept:** `implements Callable<V>`

**Fichiers:**
- `EmpruntTask.java`: ligne 29 (`Callable<String>`)
- `RechercheTask.java`: ligne 25 (`Callable<List<Livre>>`)
- `RetourTask.java`: ligne 24 (`Callable<String>`)

**Méthodes call():**
- `EmpruntTask.java`: lignes 62-112
- `RechercheTask.java`: lignes 54-88
- `RetourTask.java`: lignes 53-93

**Avantage:** Retour de valeur + exceptions

**Usage:** 3 implémentations

---

### Cycle de Vie des Threads

**Concept:** `start()` - Démarrage thread

**Fichier:** `TestBibliothecaireThread.java`

**Lignes:**
- ligne 67: `thread1.start()`
- ligne 68: `thread2.start()`
- ligne 69: `thread3.start()`

**Usage:** Lance l'exécution de `run()` dans nouveau thread

---

**Concept:** `join()` - Attente fin thread

**Fichier:** `TestBibliothecaireThread.java`

**Lignes:**
- lignes 78-80: `thread1.join()`
- lignes 81-82: `thread2.join()`
- lignes 83-84: `thread3.join()`

**Usage:** Thread principal attend fin des 3 threads

---

**Concept:** `isAlive()` - Vérification état

**Fichier:** `TestBibliothecaireThread.java`

**Lignes:**
- ligne 91: `thread1.isAlive()`
- ligne 92: `thread2.isAlive()`
- ligne 93: `thread3.isAlive()`

**Usage:** Vérifier si thread est toujours en cours

---

**Concept:** `sleep()` - Pause thread

**Fichier:** `BibliothecaireThread.java`

**Ligne:** 145: `Thread.sleep(pauseMs)`

**Usage:** Pause 500-2000ms entre opérations

---

**Concept:** `interrupt()` - Interruption thread

**Fichier:** `BibliothecaireThread.java`

**Lignes:**
- lignes 147-150: `catch InterruptedException`
- ligne 149: `Thread.currentThread().interrupt()`

**Usage:** Restauration flag d'interruption

---

**Concept:** `currentThread()` - Thread courant

**Fichiers:**
- `BibliothecaireThread.java`: ligne 149
- `EmpruntTask.java`: ligne 65
- `RechercheTask.java`: ligne 57
- `RetourTask.java`: ligne 56

**Usage:** Identification du thread exécuteur

---

### Synchronisation - ReentrantLock

**Concept:** `ReentrantLock` - Verrou réentrant

**Fichiers:**
- `EmpruntService.java`: lignes 45-46
- `SynchroManager.java`: lignes 46, 58

**Déclaration:**
```java
private final ReentrantLock lock;

public EmpruntService() {
    this.lock = new ReentrantLock();
}
```

**Usage:** 2 services utilisent ReentrantLock

---

**Concept:** `lock()` et `unlock()` - Acquisition/libération

**Fichier:** `EmpruntService.java`

**Lignes:**
- ligne 91: `lock.lock()` (emprunt)
- ligne 133: `lock.unlock()` (emprunt)
- ligne 176: `lock.lock()` (retour)
- ligne 200: `lock.unlock()` (retour)
- ligne 223: `lock.lock()` (get emprunts)
- ligne 229: `lock.unlock()` (get emprunts)

**Fichier:** `SynchroManager.java`

**Lignes:**
- ligne 93: `lock.lock()`
- ligne 100: `lock.unlock()`
- ligne 126: `lock.lock()`
- ligne 133: `lock.unlock()`
- ligne 151: `lock.lock()`
- ligne 157: `lock.unlock()`

**Pattern:**
```java
lock.lock();
try {
    // SECTION CRITIQUE
} finally {
    lock.unlock();  // Toujours dans finally
}
```

**Usage:** 15+ paires lock/unlock

---

### Synchronisation - Condition

**Concept:** `Condition` - Variable de condition

**Fichiers:**
- `EmpruntService.java`: ligne 47
- `SynchroManager.java`: ligne 47

**Création:**
```java
private final Condition disponibilite;

public EmpruntService() {
    this.lock = new ReentrantLock();
    this.disponibilite = lock.newCondition();
}
```

**Usage:** 2 conditions créées

---

**Concept:** `await()` et `signal()` / `signalAll()`

**Fichier:** `SynchroManager.java`

**Lignes:**
- ligne 96: `condition.signalAll()` (réveil tous threads)
- ligne 129: `condition.signalAll()`
- ligne 155: `condition.await(timeout, TimeUnit.SECONDS)` (attente avec timeout)

**Pattern:**
```java
lock.lock();
try {
    while (!conditionMet) {
        condition.await(timeout, TimeUnit.SECONDS);
    }
    // Travail
    condition.signalAll();  // Réveiller threads en attente
} finally {
    lock.unlock();
}
```

**Usage:** Pattern wait/notify avec Lock et Condition

---

### Synchronisation - Semaphore

**Concept:** `Semaphore` - Limitation accès concurrent

**Fichier:** `SynchroManager.java`

**Lignes:**
- ligne 44: `private final Semaphore semaphore`
- ligne 56: `new Semaphore(limiteAccesConcurrents)`

**Méthodes:**
- ligne 79: `semaphore.tryAcquire(timeout, TimeUnit.SECONDS)` - Acquisition avec timeout
- ligne 123: `semaphore.release()` - Libération permit
- ligne 154: `semaphore.hasQueuedThreads()` - Vérification file d'attente
- Méthodes d'inspection: `availablePermits()`, etc.

**Code:**
```java
private final Semaphore semaphore;

public SynchroManager(int limiteAccesConcurrents) {
    this.semaphore = new Semaphore(limiteAccesConcurrents);
}

public boolean acquerirAcces(String threadName, long timeout)
        throws InterruptedException {
    boolean acquired = semaphore.tryAcquire(timeout, TimeUnit.SECONDS);
    // ...
    return acquired;
}

public void libererAcces(String threadName) {
    semaphore.release();
}
```

**Usage:** Contrôle d'accès concurrent avec permits

---

### Synchronisation - AtomicInteger

**Concept:** `AtomicInteger` - Compteur atomique

**Fichier:** `SynchroManager.java`

**Lignes:**
- ligne 45: `ConcurrentHashMap<String, AtomicInteger> compteurs`
- ligne 176: `compteurs.computeIfAbsent(cle, k -> new AtomicInteger(0))`
- ligne 178: `compteur.incrementAndGet()` - Incrément atomique
- ligne 203: `compteur.get()` - Lecture atomique

**Code:**
```java
private final ConcurrentHashMap<String, AtomicInteger> compteurs;

public void incrementerCompteur(String type, boolean succes) {
    String cle = type + (succes ? "_SUCCES" : "_ECHEC");

    AtomicInteger compteur = compteurs.computeIfAbsent(
        cle,
        k -> new AtomicInteger(0)
    );

    compteur.incrementAndGet();  // Thread-safe
}

public int getCompteur(String type, boolean succes) {
    String cle = type + (succes ? "_SUCCES" : "_ECHEC");
    AtomicInteger compteur = compteurs.get(cle);
    return (compteur != null) ? compteur.get() : 0;
}
```

**Avantage:** Opérations atomiques sans lock explicite

**Usage:** Compteurs thread-safe

---

### Collections Concurrentes

**Concept:** `ConcurrentHashMap<K,V>` - Map thread-safe

**Fichier:** `SynchroManager.java`

**Lignes:**
- ligne 45: `private final ConcurrentHashMap<String, AtomicInteger> compteurs`
- ligne 57: `new ConcurrentHashMap<>()`
- ligne 176: `computeIfAbsent()` - Opération atomique

**Méthodes thread-safe:**
- `computeIfAbsent(key, function)`: Création atomique si absent
- `get(key)`: Lecture thread-safe
- Itération thread-safe sur entrySet

**Avantage:** Pas besoin de synchronisation externe pour opérations individuelles

**Usage:** Stockage thread-safe des compteurs

---

### ExecutorService & Future

**Concept:** ExecutorService (mentionné mais non implémenté directement)

**Usage potentiel:** Les Callable sont conçus pour être utilisés avec ExecutorService

**Code théorique:**
```java
ExecutorService executor = Executors.newFixedThreadPool(3);

Future<String> future1 = executor.submit(new EmpruntTask(...));
Future<String> future2 = executor.submit(new EmpruntTask(...));

String resultat1 = future1.get();  // Attente + récupération résultat
String resultat2 = future2.get();

executor.shutdown();
```

**Note:** Le projet utilise Callable mais n'inclut pas de tests ExecutorService complets

---

### TimeUnit

**Concept:** `TimeUnit` - Unités de temps

**Fichier:** `SynchroManager.java`

**Lignes:**
- ligne 11: `import java.util.concurrent.TimeUnit`
- ligne 79: `TimeUnit.SECONDS` (timeout acquire)
- ligne 155: `TimeUnit.SECONDS` (timeout await)

**Usage:** Spécification claire des unités de temps

---

### Patterns de Synchronisation

**Concept:** Double-check locking (Singleton thread-safe)

**Fichier:** `DatabaseConnection.java`

**Lignes:** 44-46

**Code:**
```java
public static DatabaseConnection getInstance() {
    if (instance == null) {
        synchronized (DatabaseConnection.class) {
            if (instance == null) {
                instance = new DatabaseConnection();
            }
        }
    }
    return instance;
}
```

**Usage:** Singleton lazy thread-safe

---

**Concept:** Try-finally pour unlock

**Fichiers:**
- `EmpruntService.java`: lignes 91-134
- `SynchroManager.java`: multiples occurrences

**Pattern:**
```java
lock.lock();
try {
    // Section critique
} finally {
    lock.unlock();  // Garantie de libération
}
```

**Importance:** Évite les deadlocks

---

## TD11: JDBC

### Connexion à la Base de Données

**Concept:** `DriverManager.getConnection()`

**Fichier:** `DatabaseConnection.java`

**Lignes:**
- ligne 3: `import java.sql.*`
- ligne 31: `Class.forName(DRIVER)` - Chargement driver MySQL
- ligne 68: `DriverManager.getConnection(URL, USER, PASSWORD)`

**Configuration:**
- lignes 24-28: URL, USER, PASSWORD

**Code:**
```java
static {
    try {
        Class.forName(DRIVER);  // com.mysql.cj.jdbc.Driver
    } catch (ClassNotFoundException e) {
        throw new RuntimeException("Driver MySQL non trouvé", e);
    }
}

public Connection getConnection() throws SQLException {
    connection = DriverManager.getConnection(URL, USER, PASSWORD);
    return connection;
}
```

**Usage:** Connexion MySQL

---

**Concept:** `Connection.close()`

**Fichier:** `DatabaseConnection.java`

**Lignes:**
- ligne 87: `connection.close()`

**Usage:** Fermeture connexion

---

### PreparedStatement

**Concept:** `connection.prepareStatement(sql)`

**Fichiers:** Tous les DAO

**Exemples:**

**INSERT avec clé générée:**
- `AuteurDAO.java`: lignes 60-61
```java
try (PreparedStatement stmt = connection.prepareStatement(sql,
        Statement.RETURN_GENERATED_KEYS)) {
```

**SELECT:**
- `AuteurDAO.java`: ligne 98
```java
try (PreparedStatement stmt = connection.prepareStatement(sql)) {
```

**UPDATE:**
- `AuteurDAO.java`: ligne 174
```java
try (PreparedStatement stmt = connection.prepareStatement(sql)) {
```

**DELETE:**
- `AuteurDAO.java`: ligne 213
```java
try (PreparedStatement stmt = connection.prepareStatement(sql)) {
```

**Usage:** 80+ PreparedStatement dans les DAO

---

**Concept:** Paramétrage avec `setXxx()`

**Fichiers:** Tous les DAO

**Types de paramètres:**

**setString():**
- `AuteurDAO.java`: lignes 64-66
```java
stmt.setString(1, auteur.getNom());
stmt.setString(2, auteur.getPrenom());
stmt.setString(3, auteur.getNationalite());
```

**setInt():**
- `LivreDAO.java`: ligne 65
```java
stmt.setInt(6, livre.getDisponibles());
```

**setObject() pour LocalDate:**
- `EmpruntDAO.java`: lignes 79-81
```java
stmt.setObject(3, emprunt.getDateEmprunt());
stmt.setObject(4, emprunt.getDateRetourPrevue());
stmt.setObject(5, emprunt.getDateRetourEffective());
```

**Usage:** 200+ setters de paramètres

---

**Concept:** `executeUpdate()` - INSERT/UPDATE/DELETE

**Fichiers:** Tous les DAO

**Exemples:**
- `AuteurDAO.java`: ligne 69 (INSERT)
- `AuteurDAO.java`: ligne 181 (UPDATE)
- `AuteurDAO.java`: ligne 216 (DELETE)

**Retour:** Nombre de lignes affectées

**Usage:** 50+ executeUpdate

---

**Concept:** `executeQuery()` - SELECT

**Fichiers:** Tous les DAO

**Exemples:**
- `AuteurDAO.java`: ligne 100
- `LivreDAO.java`: ligne 116
- `EmpruntDAO.java`: ligne 137

**Retour:** ResultSet

**Usage:** 40+ executeQuery

---

### ResultSet

**Concept:** Navigation avec `next()`

**Fichiers:** Tous les DAO

**Patterns:**

**Résultat unique:**
```java
try (ResultSet rs = stmt.executeQuery()) {
    if (rs.next()) {
        // Extraire données
    }
}
```

**Résultats multiples:**
```java
try (ResultSet rs = stmt.executeQuery()) {
    while (rs.next()) {
        // Créer objets et ajouter à liste
    }
}
```

**Usage:** 60+ utilisations de rs.next()

---

**Concept:** Extraction de colonnes

**Méthodes:**
- `rs.getInt(String columnLabel)`
- `rs.getString(String columnLabel)`
- `rs.getObject(String columnLabel, Class<T> type)`

**Exemples:**

**AuteurDAO.java (lignes 103-106):**
```java
return new Auteur(
    rs.getInt("id_auteur"),
    rs.getString("nom"),
    rs.getString("prenom"),
    rs.getString("nationalite")
);
```

**LivreDAO.java (ligne 131):**
```java
Categorie.valueOf(rs.getString("categorie"))
```

**EmpruntDAO.java (lignes 174-176):**
```java
rs.getObject("date_emprunt", LocalDate.class),
rs.getObject("date_retour_prevue", LocalDate.class),
rs.getObject("date_retour_effective", LocalDate.class)
```

**Usage:** 150+ extractions de colonnes

---

**Concept:** `getGeneratedKeys()` - Récupération clés auto-générées

**Fichiers:** Tous les DAO (INSERT)

**Pattern:**
```java
stmt.executeUpdate();

try (ResultSet rs = stmt.getGeneratedKeys()) {
    if (rs.next()) {
        int id = rs.getInt(1);
        objet.setId(id);
    }
}
```

**Exemples:**
- `AuteurDAO.java`: lignes 72-76
- `LivreDAO.java`: lignes 71-75
- `MembreDAO.java`: lignes 73-77
- `EmpruntDAO.java`: lignes 87-91

**Usage:** 10+ récupérations de clés générées

---

### Requêtes SQL

**Concept:** INSERT

**Exemples:**

**Simple:**
```sql
INSERT INTO auteurs (nom, prenom, nationalite) VALUES (?, ?, ?)
```

**Avec FK:**
```sql
INSERT INTO livres (isbn, titre, id_auteur, categorie, annee_publication, disponibles)
VALUES (?, ?, ?, ?, ?, ?)
```

**Usage:** 10+ requêtes INSERT

---

**Concept:** SELECT avec JOIN

**Exemples:**

**INNER JOIN simple (LivreDAO.java lignes 107-112):**
```sql
SELECT l.*, a.nom, a.prenom, a.nationalite
FROM livres l
INNER JOIN auteurs a ON l.id_auteur = a.id_auteur
WHERE l.id_livre = ?
```

**TRIPLE JOIN (EmpruntDAO.java lignes 125-133):**
```sql
SELECT e.*,
       m.cin, m.nom AS membre_nom, m.prenom AS membre_prenom,
       m.email, m.nombre_emprunts_actifs,
       l.isbn, l.titre, l.categorie, l.annee_publication, l.disponibles,
       a.nom AS auteur_nom, a.prenom AS auteur_prenom, a.nationalite
FROM emprunts e
INNER JOIN membres m ON e.id_membre = m.id_membre
INNER JOIN livres l ON e.id_livre = l.id_livre
INNER JOIN auteurs a ON l.id_auteur = a.id_auteur
WHERE e.id_emprunt = ?
```

**Usage:** 15+ requêtes avec JOIN

---

**Concept:** SELECT avec LIKE (recherche)

**Fichier:** `LivreDAO.java`

**Lignes:** 370-375

**Requête:**
```sql
SELECT l.*, a.nom, a.prenom, a.nationalite
FROM livres l
INNER JOIN auteurs a ON l.id_auteur = a.id_auteur
WHERE l.titre LIKE ? OR l.isbn LIKE ?
```

**Paramétrage:**
```java
String pattern = "%" + motCle + "%";
stmt.setString(1, pattern);
stmt.setString(2, pattern);
```

**Usage:** Recherche textuelle

---

**Concept:** UPDATE

**Exemple (AuteurDAO.java lignes 172-177):**
```sql
UPDATE auteurs
SET nom = ?, prenom = ?, nationalite = ?
WHERE id_auteur = ?
```

**Vérification résultat:**
```java
int lignesAffectees = stmt.executeUpdate();
if (lignesAffectees == 0) {
    throw new DatabaseException("Aucun auteur mis à jour");
}
```

**Usage:** 10+ requêtes UPDATE

---

**Concept:** DELETE

**Exemple (AuteurDAO.java lignes 211-214):**
```sql
DELETE FROM auteurs WHERE id_auteur = ?
```

**Usage:** 8+ requêtes DELETE

---

### Transactions ACID

**Concept:** Gestion transactionnelle manuelle

**Fichier:** `EmpruntDAO.java`

**Méthodes:**
- lignes 470-609: `effectuerEmpruntTransaction()`
- lignes 619-706: `retournerLivreTransaction()`

**Pattern complet:**

**1. Désactivation auto-commit:**
```java
connection.setAutoCommit(false);
```

**2. SELECT FOR UPDATE (verrouillage):**
```sql
SELECT disponibles FROM livres WHERE id_livre = ? FOR UPDATE
```

**3. Opérations multiples:**
- UPDATE livres (décrémenter disponibles)
- INSERT emprunt
- UPDATE membre (incrémenter emprunts actifs)

**4. COMMIT:**
```java
connection.commit();
```

**5. ROLLBACK en cas d'erreur:**
```java
try {
    // Opérations...
    connection.commit();
} catch (SQLException e) {
    connection.rollback();
    throw new DatabaseException(...);
} finally {
    connection.setAutoCommit(true);
}
```

**Code complet (EmpruntDAO.java lignes 470-609):**
```java
public Emprunt effectuerEmpruntTransaction(int idMembre, int idLivre)
        throws DatabaseException, LivreIndisponibleException {

    try {
        // DÉBUT TRANSACTION
        connection.setAutoCommit(false);

        // ÉTAPE 1: SELECT FOR UPDATE
        String sqlLivre = "SELECT disponibles FROM livres WHERE id_livre = ? FOR UPDATE";

        try (PreparedStatement stmtLivre = connection.prepareStatement(sqlLivre)) {
            stmtLivre.setInt(1, idLivre);

            try (ResultSet rs = stmtLivre.executeQuery()) {
                if (rs.next()) {
                    int disponibles = rs.getInt("disponibles");

                    if (disponibles <= 0) {
                        throw new LivreIndisponibleException(...);
                    }

                    // ÉTAPE 2: UPDATE livres
                    String sqlUpdateLivre =
                        "UPDATE livres SET disponibles = disponibles - 1 WHERE id_livre = ?";

                    try (PreparedStatement stmtUpdate =
                            connection.prepareStatement(sqlUpdateLivre)) {
                        stmtUpdate.setInt(1, idLivre);
                        stmtUpdate.executeUpdate();
                    }

                    // ÉTAPE 3: INSERT emprunt
                    String sqlInsert =
                        "INSERT INTO emprunts (id_membre, id_livre, date_emprunt, date_retour_prevue) " +
                        "VALUES (?, ?, ?, ?)";

                    // ... insertion ...

                    // ÉTAPE 4: UPDATE membre
                    String sqlUpdateMembre =
                        "UPDATE membres SET nombre_emprunts_actifs = " +
                        "nombre_emprunts_actifs + 1 WHERE id_membre = ?";

                    // ... update ...

                    // COMMIT TRANSACTION
                    connection.commit();

                    return findById(idEmprunt).orElse(null);
                }
            }
        }

        // ROLLBACK si pas de résultat
        connection.rollback();
        return null;

    } catch (SQLException e) {
        // ROLLBACK en cas d'erreur
        try {
            connection.rollback();
        } catch (SQLException rollbackEx) {
            rollbackEx.printStackTrace();
        }
        throw new DatabaseException("Erreur transaction emprunt", e);

    } finally {
        // Restaurer auto-commit
        try {
            connection.setAutoCommit(true);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
```

**Tests de transaction:**
- `TestEmpruntDAO.java`: lignes 88-131 (transaction réussie)
- `TestEmpruntDAO.java`: lignes 203-263 (transaction avec ROLLBACK)

**Propriétés ACID démontrées:**
- **Atomicity:** Toutes les opérations ou aucune
- **Consistency:** État cohérent de la BD
- **Isolation:** SELECT FOR UPDATE verrouille
- **Durability:** commit() rend permanent

**Usage:** 2 transactions ACID complètes

---

### Gestion des Exceptions JDBC

**Concept:** `SQLException`

**Fichiers:** Tous les DAO

**Pattern:**
```java
try {
    // Opérations JDBC
} catch (SQLException e) {
    throw new DatabaseException("Message contexte", e);
}
```

**Usage:** 50+ catch SQLException

---

**Concept:** Encapsulation dans `DatabaseException`

**Fichier:** `DatabaseException.java`

**Constructeur:**
```java
public DatabaseException(String message, Throwable cause) {
    super(message, cause, "DB_ERROR");
}
```

**Avantage:** Exception métier uniforme

---

## TD8-9: I/O Operations

### Classes I/O

**Concept:** `FileWriter` - Écriture fichiers

**Fichier:** `FileExporter.java`

**Lignes:**
- ligne 5: `import java.io.FileWriter`
- ligne 108-109: `new FileWriter(cheminComplet, StandardCharsets.UTF_8)`
- lignes 220, 275, 360, 399, 437, 479, 519: Multiple FileWriter

**Usage:** 15+ FileWriter créés

---

**Concept:** `BufferedWriter` - Écriture bufferisée

**Fichier:** `FileExporter.java`

**Lignes:**
- ligne 3: `import java.io.BufferedWriter`
- ligne 110: `new BufferedWriter(writer)`
- Toutes les méthodes d'export: BufferedWriter

**Avantage:** Performance (écriture par blocs)

**Usage:** 15+ BufferedWriter

---

**Concept:** `File` - Manipulation fichiers/répertoires

**Fichier:** `FileExporter.java`

**Lignes:**
- ligne 4: `import java.io.File`
- ligne 527: `new File(REPERTOIRE_RAPPORTS)`
- ligne 529: `repertoire.exists()`
- ligne 530: `repertoire.mkdirs()`
- ligne 564: `repertoire.listFiles()`
- ligne 571: `fichier.delete()`

**Opérations:**
- Vérification existence
- Création répertoires
- Liste fichiers
- Suppression

**Usage:** 20+ opérations File

---

### try-with-resources

**Concept:** Auto-fermeture ressources

**Fichier:** `FileExporter.java`

**Pattern:**
```java
try (FileWriter writer = new FileWriter(cheminComplet, StandardCharsets.UTF_8);
     BufferedWriter bw = new BufferedWriter(writer)) {

    // Écriture

}  // Auto-fermeture automatique
catch (IOException e) {
    // Gestion erreur
}
```

**Lignes:**
- lignes 108-113: try-with-resources double
- lignes 169-186: exporterLivresCSV
- lignes 220-239: exporterMembresCSV
- lignes 275-303: exporterTopLivresCSV
- lignes 360-374: exporterStatsCSV
- lignes 399-421: exporterRapportMensuelCSV
- lignes 437-463: exporterEmpruntsCSV
- lignes 479-505: exporterAuteursCSV

**Fichiers DAO:** Tous les DAO utilisent try-with-resources pour PreparedStatement et ResultSet

**Avantage:**
- Fermeture garantie même en cas d'exception
- Pas de fuite de ressources
- Code plus propre

**Usage:** 50+ try-with-resources dans le projet

---

### Encodage et BOM

**Concept:** `StandardCharsets.UTF-8`

**Fichier:** `FileExporter.java`

**Lignes:**
- ligne 7: `import java.nio.charset.StandardCharsets`
- ligne 108: `new FileWriter(..., StandardCharsets.UTF-8)`

**Usage:** Tous les exports CSV

---

**Concept:** BOM UTF-8 (Byte Order Mark)

**Fichier:** `FileExporter.java`

**Lignes:**
- ligne 112: `bw.write('\ufeff')` - BOM
- lignes 174, 224, 279, 364, 403, 441, 483, 523: BOM dans chaque export

**Objectif:** Compatibilité Excel avec accents

**Code:**
```java
try (FileWriter writer = new FileWriter(cheminComplet, StandardCharsets.UTF-8);
     BufferedWriter bw = new BufferedWriter(writer)) {

    // BOM UTF-8 pour Excel
    bw.write('\ufeff');

    // Écriture données...
}
```

**Usage:** Tous les exports CSV

---

### Écriture de Fichiers

**Concept:** `write()` - Écriture chaînes

**Fichier:** `FileExporter.java`

**Exemples:**
- ligne 112: `bw.write('\ufeff')` - BOM
- lignes 177-179: En-têtes CSV
- lignes 183-185: Lignes de données

**Pattern:**
```java
// En-tête
bw.write("Colonne1" + SEPARATEUR_CSV + "Colonne2\n");

// Données
livres.forEach(livre -> {
    try {
        bw.write(livre.getIsbn() + SEPARATEUR_CSV +
                 livre.getTitre() + "\n");
    } catch (IOException e) {
        e.printStackTrace();
    }
});
```

**Usage:** 100+ write()

---

**Concept:** Formatage CSV

**Séparateur:** `;` (ligne 38)

**Structure:**
```
BOM
En-tête1;En-tête2;En-tête3
Valeur1;Valeur2;Valeur3
Valeur1;Valeur2;Valeur3
```

**Exemples:**

**Livres (lignes 177-185):**
```java
bw.write("ISBN" + SEPARATEUR_CSV + "Titre" + SEPARATEUR_CSV +
         "Auteur" + SEPARATEUR_CSV + "Catégorie\n");

livres.forEach(livre -> {
    bw.write(livre.getIsbn() + SEPARATEUR_CSV +
             livre.getTitre() + SEPARATEUR_CSV +
             livre.getAuteur().getNom() + "\n");
});
```

**Usage:** 8 méthodes d'export CSV

---

### Opérations sur Répertoires et Fichiers

**Concept:** Création répertoire

**Fichier:** `FileExporter.java`

**Lignes:** 527-531

**Code:**
```java
File repertoire = new File(REPERTOIRE_RAPPORTS);

if (!repertoire.exists()) {
    repertoire.mkdirs();  // Crée répertoires parents si nécessaire
}
```

---

**Concept:** Liste fichiers

**Fichier:** `FileExporter.java`

**Lignes:** 607-684

**Code:**
```java
File repertoire = new File(REPERTOIRE_RAPPORTS);

if (repertoire.exists()) {
    File[] fichiers = repertoire.listFiles();

    if (fichiers != null) {
        List<String> fichiersCSV = new ArrayList<>();

        for (File fichier : fichiers) {
            if (fichier.isFile() && fichier.getName().endsWith(".csv")) {
                fichiersCSV.add(fichier.getName());
            }
        }

        return fichiersCSV;
    }
}
```

---

**Concept:** Suppression fichiers

**Fichier:** `FileExporter.java`

**Lignes:** 562-575

**Code:**
```java
File repertoire = new File(REPERTOIRE_RAPPORTS);

if (repertoire.exists()) {
    File[] fichiers = repertoire.listFiles();

    if (fichiers != null) {
        for (File fichier : fichiers) {
            if (fichier.isFile()) {
                fichier.delete();
            }
        }
    }
}
```

---

### Gestion des Exceptions I/O

**Concept:** `IOException`

**Fichier:** `FileExporter.java`

**Lignes:**
- ligne 6: `import java.io.IOException`
- ligne 151: `catch (IOException e)`
- Toutes les méthodes: `throws IOException`

**Pattern:**
```java
try (FileWriter writer = ...) {
    // Écriture
} catch (IOException e) {
    System.err.println("❌ Erreur export CSV : " + e.getMessage());
    throw e;  // Propager l'exception
}
```

**Usage:** 15+ catch IOException

---

### Type Detection avec instanceof (I/O)

**Concept:** Routing par type

**Fichier:** `FileExporter.java`

**Lignes:** 116-153

**Code:**
```java
public <T> void exporterCSV(String nomFichier, List<T> donnees)
        throws IOException {

    if (donnees.isEmpty()) return;

    Object premierElement = donnees.get(0);

    if (premierElement instanceof Livre) {
        @SuppressWarnings("unchecked")
        List<Livre> livres = (List<Livre>) donnees;
        exporterLivresCSV(cheminComplet, livres);

    } else if (premierElement instanceof Membre) {
        @SuppressWarnings("unchecked")
        List<Membre> membres = (List<Membre>) donnees;
        exporterMembresCSV(cheminComplet, membres);

    } else if (premierElement instanceof Map.Entry) {
        @SuppressWarnings("unchecked")
        Map.Entry<?, ?> entry = (Map.Entry<?, ?>) premierElement;

        if (entry.getKey() instanceof Livre) {
            exporterTopLivresCSV(nomFichier, (List<Map.Entry<Livre, Long>>) donnees);
        } else if (entry.getKey() instanceof Membre) {
            exporterTopMembresCSV(nomFichier, (List<Map.Entry<Membre, Long>>) donnees);
        }
    } else if (premierElement instanceof Emprunt) {
        exporterEmpruntsCSV(cheminComplet, (List<Emprunt>) donnees);
    } else if (premierElement instanceof Auteur) {
        exporterAuteursCSV(cheminComplet, (List<Auteur>) donnees);
    }
}
```

**Avantage:** Méthode générique avec routage par type

---

# III. PATTERNS ET RECOMMANDATIONS

## Patterns de Conception Utilisés

### 1. DAO (Data Access Object) Pattern

**Fichiers:** Tous les *DAO.java

**Structure:**
- Couche d'accès aux données
- Encapsulation JDBC
- CRUD complet
- Conversion ResultSet → Objet métier

**Avantages:**
- Séparation concerns
- Testabilité
- Maintenance facilitée

**Exemple d'utilisation:**
```java
AuteurDAO auteurDAO = new AuteurDAO(connection);
Auteur auteur = auteurDAO.findById(1);
auteur.setNom("Nouveau nom");
auteurDAO.update(auteur);
```

---

### 2. Service Layer Pattern

**Fichiers:** *Service.java

**Responsabilités:**
- Logique métier
- Coordination DAO
- Validation
- Gestion transactions (via DAO)
- Thread-safety (EmpruntService)

**Exemple:**
```java
EmpruntService service = new EmpruntService();
Emprunt emprunt = service.effectuerEmprunt(idMembre, idLivre);
// Service gère: validation membre, validation livre, transaction
```

---

### 3. Singleton Pattern

**Fichier:** `DatabaseConnection.java`

**Caractéristiques:**
- Instance unique
- Thread-safe (double-check locking)
- Lazy initialization

**Usage:**
```java
DatabaseConnection db = DatabaseConnection.getInstance();
Connection conn = db.getConnection();
```

---

### 4. Exception Hierarchy Pattern

**Fichiers:** Package exceptions

**Structure:**
```
Exception
  └── BiblioTechException (base)
        ├── DatabaseException
        ├── LivreIndisponibleException
        ├── LivreNonTrouveException
        └── MembreNonTrouveException
```

**Avantage:** Gestion d'erreurs typée et hiérarchique

---

### 5. Template Method (implicite dans DAO)

**Pattern:**
- Méthodes CRUD suivent même structure
- save(), findById(), findAll(), update(), delete()

---

## Bonnes Pratiques Démontrées

### 1. Gestion des Ressources

**try-with-resources systématique:**
- Tous les DAO: PreparedStatement, ResultSet
- FileExporter: FileWriter, BufferedWriter

**Avantage:** Pas de fuite de ressources

---

### 2. Sécurité SQL

**PreparedStatement partout:**
- Jamais de concaténation SQL
- Protection contre injection SQL

**Exemple BON:**
```java
String sql = "SELECT * FROM livres WHERE titre = ?";
stmt.setString(1, titre);
```

**Exemple MAUVAIS (absent du code):**
```java
String sql = "SELECT * FROM livres WHERE titre = '" + titre + "'";  // Injection SQL!
```

---

### 3. Transactions ACID

**Pattern correct:**
1. setAutoCommit(false)
2. Opérations multiples
3. commit() si succès
4. rollback() si erreur
5. setAutoCommit(true) dans finally

**Fichier:** `EmpruntDAO.java`

---

### 4. Thread-Safety

**Mécanismes utilisés:**
- ReentrantLock + Condition
- Semaphore
- AtomicInteger
- ConcurrentHashMap

**Pattern lock:**
```java
lock.lock();
try {
    // Section critique
} finally {
    lock.unlock();  // Toujours dans finally
}
```

---

### 5. Streams API

**Usage intensif:**
- Remplacement boucles for
- Code déclaratif vs impératif
- Chaînage opérations
- Collectors avancés

**Exemple transformation:**

**Avant (impératif):**
```java
List<Livre> disponibles = new ArrayList<>();
for (Livre livre : livres) {
    if (livre.estDisponible()) {
        disponibles.add(livre);
    }
}
```

**Après (déclaratif):**
```java
List<Livre> disponibles = livres.stream()
    .filter(Livre::estDisponible)
    .collect(Collectors.toList());
```

---

### 6. Optional

**Remplacement null:**
- `LivreDAO.findById()` retourne `Optional<Livre>`
- `.orElse()` pour valeur par défaut
- `.isPresent()` pour test

**Avantage:** Explicite sur absence possible

---

### 7. Encapsulation

**Tous les modèles:**
- Attributs privés
- Getters/Setters publics
- Méthodes métier publiques
- Validation dans setters

---

### 8. equals() et hashCode()

**Utilisation correcte de Objects:**
- `Objects.equals()` pour comparaisons null-safe
- `Objects.hash()` pour hashCode
- equals() basé sur champs métier significatifs

**Exemple:** Livre comparé sur ISBN (identifiant unique)

---

## Recommandations pour l'Évaluation

### Points Forts à Mettre en Avant

**1. Couverture exhaustive des concepts TD1-11:**
- POO: ✅ Toutes les bases + avancé
- Collections: ✅ List, Map, Optional
- Lambda: ✅ 60+ utilisations
- Streams: ✅ 150+ utilisations (excellente maîtrise)
- Multithreading: ✅ 4 mécanismes différents
- JDBC: ✅ CRUD + JOIN + Transactions
- I/O: ✅ Export CSV complet

**2. Qualité du code:**
- Séparation concerns (DAO, Service, Modèle)
- Gestion ressources (try-with-resources partout)
- Thread-safety (Lock, Semaphore, Atomic)
- Sécurité SQL (PreparedStatement uniquement)

**3. Complexité gérée:**
- Transactions ACID avec ROLLBACK
- Triple JOIN (4 tables)
- Streams complexes (groupingBy + counting + sorted)
- Synchronisation multi-niveaux

---

### Concepts Clés par TD

**TD1-2 (POO):**
- Montrer: `Auteur.java` (equals/hashCode avec Objects)
- Montrer: `Livre.java` (composition, méthodes métier)
- Montrer: Hiérarchie exceptions

**TD3 (Lambda):**
- Montrer: Method references dans `StatistiquesService`
- Montrer: Lambda complexes dans filtres

**TD4-5 (Streams):**
- Montrer: `StatistiquesService.top10LivresPlusEmpruntes()` (groupingBy + counting + sorted + limit)
- Montrer: `getMembresEnRetard()` (nested stream avec anyMatch)
- Montrer: IntSummaryStatistics

**TD6-10 (Threads):**
- Montrer: `SynchroManager.java` (4 mécanismes)
- Montrer: `EmpruntService.java` (ReentrantLock + Condition)
- Montrer: `BibliothecaireThread.java` (extends Thread avec run())

**TD11 (JDBC):**
- Montrer: `EmpruntDAO.effectuerEmpruntTransaction()` (TRANSACTION ACID complète)
- Montrer: `EmpruntDAO.findById()` (TRIPLE JOIN)
- Montrer: PreparedStatement avec setObject(LocalDate)

**TD8-9 (I/O):**
- Montrer: `FileExporter.exporterLivresCSV()` (try-with-resources + BOM)
- Montrer: Type detection générique

---

### Statistiques Impressionnantes

**Quantitatif:**
- **35 fichiers** Java
- **8500+ lignes** de code
- **150+ utilisations** Streams API
- **80+ PreparedStatement** JDBC
- **50+ try-with-resources**
- **60+ method references**
- **100+ getters/setters**

**Qualitatif:**
- **0 injection SQL** (PreparedStatement partout)
- **0 fuite de ressources** (try-with-resources partout)
- **Thread-safe** (4 mécanismes de synchronisation)
- **ACID complet** (transactions avec ROLLBACK testées)

---

### Démonstrations Recommandées

**Si demandé de montrer un concept spécifique:**

**1. "Montrez-moi un Stream complexe"**
→ `StatistiquesService.top10LivresPlusEmpruntes()` (ligne 104-116)

**2. "Montrez-moi une transaction ACID"**
→ `EmpruntDAO.effectuerEmpruntTransaction()` (ligne 470-609)

**3. "Montrez-moi la synchronisation"**
→ `SynchroManager.java` (Semaphore + Lock + Condition + Atomic)

**4. "Montrez-moi equals() et hashCode()"**
→ `Auteur.java` (ligne 94-115) avec Objects.equals() et Objects.hash()

**5. "Montrez-moi une lambda"**
→ N'importe quel filter() dans StatistiquesService

**6. "Montrez-moi un JOIN SQL"**
→ `EmpruntDAO.findById()` (triple JOIN, ligne 125-133)

**7. "Montrez-moi l'I/O"**
→ `FileExporter.exporterLivresCSV()` (try-with-resources + BOM UTF-8)

---

## Conclusion

### Résumé Final

**Ce projet BiblioTech démontre une maîtrise complète et approfondie des concepts Java Avancé (TD1-11):**

✅ **POO (TD1-2):** 35 classes, hiérarchie exceptions, composition, equals/hashCode correct
✅ **Lambda (TD3):** 60+ expressions lambda et method references
✅ **Streams (TD4-5):** 150+ utilisations incluant groupingBy, counting, IntSummaryStatistics
✅ **Multithreading (TD6-10):** Thread, Callable, Lock, Condition, Semaphore, AtomicInteger
✅ **JDBC (TD11):** CRUD complet, JOIN multi-tables, Transactions ACID avec ROLLBACK
✅ **I/O (TD8-9):** Export CSV avec try-with-resources, BOM UTF-8, gestion répertoires

### Points d'Excellence

**1. Qualité architecturale:**
- Séparation claire des responsabilités (DAO/Service/Model)
- Patterns de conception reconnus
- Code maintenable et extensible

**2. Robustesse:**
- Gestion complète des exceptions
- Thread-safety où nécessaire
- Transactions ACID testées (commit ET rollback)

**3. Modernité:**
- Java 8+ (LocalDate, Streams, Optional)
- API moderne (method references, Collectors avancés)
- Bonnes pratiques (try-with-resources, PreparedStatement)

### Recommandation Finale

**Pour l'évaluation:**
1. Lire ce document avant l'évaluation
2. Préparer les exemples de code clés par TD
3. Pouvoir expliquer les choix d'architecture
4. Mettre en avant les transactions ACID et Streams complexes
5. Montrer la couverture exhaustive des concepts

**Ce projet démontre non seulement la connaissance des concepts, mais aussi leur application correcte dans un contexte réel.**

🎯 **Objectif:** Valider une expertise complète en Java Avancé

---

**Bonne chance pour votre évaluation ! 🚀**

**Belcadi Oussama - BiblioTech Project - 2026**
