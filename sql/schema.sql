-- Schéma BiblioTech (MySQL 8.x).
-- Exécuté automatiquement par docker-compose et par les tests d'intégration (Testcontainers).
-- Installation manuelle : mysql -u <utilisateur> -p bibliotech_db < sql/schema.sql

CREATE TABLE IF NOT EXISTS auteur (
    id     INT          NOT NULL AUTO_INCREMENT,
    nom    VARCHAR(100) NOT NULL,
    prenom VARCHAR(100) NULL,
    PRIMARY KEY (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS livre (
    id        INT          NOT NULL AUTO_INCREMENT,
    titre     VARCHAR(255) NOT NULL,
    categorie ENUM ('ROMAN', 'SCIENCE', 'HISTOIRE', 'BIOGRAPHIE', 'FANTASY', 'INFORMATIQUE') NOT NULL,
    id_auteur INT          NOT NULL,
    PRIMARY KEY (id),
    -- Un titre n'existe qu'une fois (comparaison insensible à la casse et aux accents) :
    -- c'est cette contrainte qui garantit l'absence de doublon lors d'insertions concurrentes.
    CONSTRAINT uq_livre_titre UNIQUE (titre),
    CONSTRAINT fk_livre_auteur FOREIGN KEY (id_auteur) REFERENCES auteur (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;
