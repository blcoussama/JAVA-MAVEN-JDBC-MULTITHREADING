package com.bibliotech.support;

import com.bibliotech.dao.AuteurDAO;
import com.bibliotech.dao.ConnectionFactory;
import com.bibliotech.dao.LivreDAO;
import com.bibliotech.model.Livre;
import java.sql.SQLException;
import java.time.Duration;
import java.util.List;
import java.util.function.BooleanSupplier;

/** Doublures de DAO et utilitaires pour les tests unitaires (aucune base de données). */
public final class DaoDeTest {

    /** Toute tentative de connexion fait échouer le test : la base ne doit pas être sollicitée. */
    public static final ConnectionFactory AUCUNE_BASE = () -> {
        throw new AssertionError("La base de données ne doit pas être appelée dans ce test.");
    };

    private DaoDeTest() {
    }

    @FunctionalInterface
    public interface Lecture {
        List<Livre> lire() throws Exception;
    }

    /** LivreDAO dont la lecture est remplacée par le comportement fourni. */
    public static LivreDAO livreDAO(Lecture lecture) {
        return new LivreDAO(AUCUNE_BASE) {
            @Override
            public List<Livre> listerTousLesLivres() throws SQLException {
                try {
                    return lecture.lire();
                } catch (SQLException e) {
                    throw e;
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new SQLException("lecture interrompue", e);
                } catch (Exception e) {
                    throw new SQLException(e);
                }
            }
        };
    }

    public static AuteurDAO auteurDAOInutilise() {
        return new AuteurDAO(AUCUNE_BASE);
    }

    /** Attend qu'une condition devienne vraie (au plus 10 s), sinon fait échouer le test. */
    public static void attendreQue(BooleanSupplier condition, String description) throws InterruptedException {
        long limite = System.nanoTime() + Duration.ofSeconds(10).toNanos();
        while (!condition.getAsBoolean()) {
            if (System.nanoTime() > limite) {
                throw new AssertionError("Condition non atteinte en 10 s : " + description);
            }
            Thread.sleep(5);
        }
    }
}
