package be.profacile.pharmashare.domain.repository;

import be.profacile.pharmashare.domain.model.Pharmacie;
import be.profacile.pharmashare.domain.support.JpaExerciseSupport.BaseJpa;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import static be.profacile.pharmashare.domain.support.JpaExerciseSupport.*;
import static org.junit.jupiter.api.Assertions.*;

class PharmacieRepositoryTest {
    @Test
    void lesChampsEtLeMappingCorrespondentAuContrat() {
        entite(Pharmacie.class, "pharmacie");
        colonne(Pharmacie.class, "nom", String.class, "nom", false);
        colonne(Pharmacie.class, "adresse", String.class, "adresse", false);
        colonne(Pharmacie.class, "dateDernierePublication", LocalDateTime.class, "date_derniere_publication", true);
        repository(PharmacieRepository.class, Pharmacie.class);
    }

    @Test
    void sauvegardeRelitEtRechercheUnePharmacieSansTenirCompteDeLaCasse() throws Exception {
        lesChampsEtLeMappingCorrespondentAuContrat();
        try (BaseJpa base = new BaseJpa(Pharmacie.class)) {
            var repository = base.creerRepository(PharmacieRepository.class);
            LocalDateTime publication = LocalDateTime.of(2026, 11, 19, 18, 0);
            Object pharmacie = nouveau(Pharmacie.class,
                Map.of("nom", "Officine du Centre fictive", "adresse", "Adresse fictive",
                    "dateDernierePublication", publication));
            base.entityManager.getTransaction().begin();
            repository.saveAndFlush(pharmacie);
            Long id = (Long) lire(pharmacie, "id");
            assertNotNull(id);
            base.entityManager.clear();
            Object recharge = repository.findById(id).orElseThrow();
            assertEquals(publication, lire(recharge, "dateDernierePublication"));
            assertEquals("Adresse fictive", lire(recharge, "adresse"));
            List<?> resultats = (List<?>) rechercher(repository, "findByNomContainingIgnoreCase", String.class, "CENTRE");
            assertEquals(1, resultats.size());
            assertEquals(id, lire(resultats.getFirst(), "id"));
            assertTrue(((List<?>) rechercher(repository, "findByNomContainingIgnoreCase", String.class, "introuvable")).isEmpty());
            base.entityManager.getTransaction().commit();
        }
    }
}
