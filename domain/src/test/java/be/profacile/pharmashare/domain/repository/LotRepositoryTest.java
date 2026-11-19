package be.profacile.pharmashare.domain.repository;

import be.profacile.pharmashare.domain.model.*;
import be.profacile.pharmashare.domain.support.JpaExerciseSupport.BaseJpa;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import static be.profacile.pharmashare.domain.support.JpaExerciseSupport.*;
import static org.junit.jupiter.api.Assertions.*;

class LotRepositoryTest {
    @Test
    void lesChampsLesRelationsEtLEnumerationCorrespondentAuContrat() {
        entite(Lot.class, "lot");
        colonne(Lot.class, "numeroLot", String.class, "numero_lot", false);
        colonne(Lot.class, "datePeremption", LocalDate.class, "date_peremption", false);
        colonne(Lot.class, "quantite", Integer.class, "quantite", false);
        colonne(Lot.class, "prixNormal", BigDecimal.class, "prix_normal", false);
        colonne(Lot.class, "prixReduit", BigDecimal.class, "prix_reduit", true);
        Enumerated mapping = colonne(Lot.class, "statut", EtatLot.class, "statut", false)
            .getAnnotation(Enumerated.class);
        assertNotNull(mapping, "Mapper le statut avec @Enumerated");
        assertEquals(EnumType.STRING, mapping.value());
        assertEquals(List.of("A_COMPLETER", "PROPOSE", "DEMANDE", "RESERVE", "REMIS",
            "EN_ANOMALIE", "RECEPTIONNE", "RETIRE"),
            Arrays.stream(EtatLot.values()).map(Enum::name).toList());
        relation(Lot.class, "medicament", Medicament.class, "medicament_id");
        relation(Lot.class, "pharmacie", Pharmacie.class, "pharmacie_id");
        repository(LotRepository.class, Lot.class);
    }

    @Test
    void sauvegardeLeLotAvecSesRelationsEtLeRechercheParPharmacie() throws Exception {
        lesChampsLesRelationsEtLEnumerationCorrespondentAuContrat();
        try (BaseJpa base = new BaseJpa(Lot.class, Medicament.class, Pharmacie.class)) {
            var repository = base.creerRepository(LotRepository.class);
            Object medicament = nouveau(Medicament.class,
                Map.of("codeCnk", "1234567", "nom", "Produit fictif", "principeActif", "Fictif"));
            Object pharmacie = nouveau(Pharmacie.class,
                Map.of("nom", "Officine fictive", "adresse", "Adresse fictive"));
            Object lot = nouveau(Lot.class, Map.of("numeroLot", "LOT-FICTIF-01",
                "datePeremption", LocalDate.of(2027, 6, 30), "quantite", 10,
                "prixNormal", new BigDecimal("12.50"), "prixReduit", new BigDecimal("6.25"),
                "statut", Enum.valueOf(EtatLot.class, "PROPOSE"),
                "medicament", medicament, "pharmacie", pharmacie));
            base.entityManager.getTransaction().begin();
            base.entityManager.persist(medicament);
            base.entityManager.persist(pharmacie);
            repository.saveAndFlush(lot);
            Long id = (Long) lire(lot, "id");
            Long pharmacieId = (Long) lire(pharmacie, "id");
            assertNotNull(id);
            base.entityManager.clear();
            Object recharge = repository.findById(id).orElseThrow();
            assertEquals("LOT-FICTIF-01", lire(recharge, "numeroLot"));
            assertEquals(10, lire(recharge, "quantite"));
            assertEquals(0, new BigDecimal("6.25").compareTo((BigDecimal) lire(recharge, "prixReduit")));
            assertEquals("PROPOSE", ((Enum<?>) lire(recharge, "statut")).name());
            assertEquals("1234567", lire(lire(recharge, "medicament"), "codeCnk"));
            assertEquals(pharmacieId, lire(lire(recharge, "pharmacie"), "id"));
            List<?> resultats = (List<?>) rechercher(repository, "findByPharmacieId", Long.class, pharmacieId);
            assertEquals(1, resultats.size());
            assertEquals(id, lire(resultats.getFirst(), "id"));
            assertTrue(((List<?>) rechercher(repository, "findByPharmacieId", Long.class, Long.MAX_VALUE)).isEmpty());
            base.entityManager.getTransaction().commit();
        }
    }
}
