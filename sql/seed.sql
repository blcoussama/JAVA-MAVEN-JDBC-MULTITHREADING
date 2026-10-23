-- Données de démonstration (auteurs fictifs), chargées après schema.sql.
SET NAMES utf8mb4;

INSERT INTO auteur (id, nom, prenom) VALUES
    (1, 'Martin',  'Claire'),
    (2, 'Durand',  'Paul'),
    (3, 'Lefèvre', 'Inès'),
    (4, 'Moreau',  'Hugo');

INSERT INTO livre (titre, categorie, id_auteur) VALUES
    ('Le Guide du Java',              'INFORMATIQUE', 1),
    ('Design Patterns en pratique',   'INFORMATIQUE', 1),
    ('Code propre',                   'INFORMATIQUE', 2),
    ('Une histoire du code',          'HISTOIRE',     3),
    ('Les grandes bibliothèques',     'HISTOIRE',     3),
    ('Le Royaume des dragons',        'FANTASY',      4),
    ('La Maison au bord du lac',      'ROMAN',        4),
    ('Physique pour curieux',         'SCIENCE',      2);
