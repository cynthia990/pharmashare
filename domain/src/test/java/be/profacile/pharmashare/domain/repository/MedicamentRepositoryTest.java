package be.profacile.pharmashare.domain.repository;

import be.profacile.pharmashare.domain.model.Medicament;
import be.profacile.pharmashare.domain.support.JpaExerciseSupport.BaseJpa;
import jakarta.persistence.Column;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import static be.profacile.pharmashare.domain.support.JpaExerciseSupport.*;
import static org.junit.jupiter.api.Assertions.*;

class MedicamentRepositoryTest {
    @Test
    void lesChampsEtLeMappingCorrespondentAuContrat() {
        entite(Medicament.class, "medicament");
        Column cnk = colonne(Medicament.class, "codeCnk", String.class, "code_cnk", false)
            .getAnnotation(Column.class);
        assertEquals(7, cnk.length());
        assertTrue(cnk.unique(), "Le CNK identifie une référence unique");
        colonne(Medicament.class, "nom", String.class, "nom", false);
        colonne(Medicament.class, "principeActif", String.class, "principe_actif", false);
        repository(MedicamentRepository.class, Medicament.class);
    }

    @Test
    void sauvegardeRelitRechercheEtSupprimeUnMedicament() throws Exception {
        lesChampsEtLeMappingCorrespondentAuContrat();
        try (BaseJpa base = new BaseJpa(Medicament.class)) {
            var repository = base.creerRepository(MedicamentRepository.class);
            Object medicament = nouveau(Medicament.class,
                Map.of("codeCnk", "1234567", "nom", "Produit fictif", "principeActif", "Substance fictive"));
            base.entityManager.getTransaction().begin();
            repository.saveAndFlush(medicament);
            Long id = (Long) lire(medicament, "id");
            assertNotNull(id, "La base doit générer l'identifiant");
            base.entityManager.clear();
            Object recharge = repository.findById(id).orElseThrow();
            assertEquals("Produit fictif", lire(recharge, "nom"));
            assertEquals("Substance fictive", lire(recharge, "principeActif"));
            Optional<?> trouve = (Optional<?>) rechercher(repository, "findByCodeCnk", String.class, "1234567");
            assertEquals(id, lire(trouve.orElseThrow(), "id"));
            assertTrue(((Optional<?>) rechercher(repository, "findByCodeCnk", String.class, "7654321")).isEmpty());
            repository.deleteById(id);
            repository.flush();
            base.entityManager.clear();
            assertTrue(repository.findById(id).isEmpty());
            base.entityManager.getTransaction().commit();
        }
    }

    @Test
    void deuxMedicamentsNePeuventPasPartagerLeMemeCnk() throws Exception {
        lesChampsEtLeMappingCorrespondentAuContrat();
        try (BaseJpa base = new BaseJpa(Medicament.class)) {
            var repository = base.creerRepository(MedicamentRepository.class);
            base.entityManager.getTransaction().begin();
            repository.saveAndFlush(nouveau(Medicament.class,
                Map.of("codeCnk", "1234567", "nom", "Produit A", "principeActif", "Fictif")));
            Object doublon = nouveau(Medicament.class,
                Map.of("codeCnk", "1234567", "nom", "Produit B", "principeActif", "Fictif"));
            assertThrows(RuntimeException.class, () -> repository.saveAndFlush(doublon));
        }
    }
}
