package com.bibliotech.dao;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Fournit une connexion JDBC. Les DAO dépendent de cette abstraction et non d'une configuration figée :
 * l'application utilise les variables d'environnement, les tests d'intégration un MySQL jetable.
 */
@FunctionalInterface
public interface ConnectionFactory {

    Connection getConnection() throws SQLException;
}
