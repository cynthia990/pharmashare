package be.profacile.pharmashare.domain.repository;

import be.profacile.pharmashare.domain.model.Partenaire;
import be.profacile.pharmashare.domain.support.JpaExerciseSupport.BaseJpa;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import static be.profacile.pharmashare.domain.support.JpaExerciseSupport.*;
import static org.junit.jupiter.api.Assertions.*;

class PartenaireRepositoryTest {
    @Test
    void lesChampsEtLeMappingCorrespondentAuContrat() {
        entite(Partenaire.class, "partenaire");
        colonne(Partenaire.class, "nom", String.class, "nom", false);
        colonne(Partenaire.class, "type", String.class, "type", false);
        colonne(Partenaire.class, "pays", String.class, "pays", false);
        colonne(Partenaire.class, "adresse", String.class, "adresse", false);
        colonne(Partenaire.class, "habilite", Boolean.class, "habilite", false);
        repository(PartenaireRepository.class, Partenaire.class);
    }

    @Test
    void sauvegardeEtRetourneUniquementLesPartenairesHabilites() throws Exception {
        lesChampsEtLeMappingCorrespondentAuContrat();
        try (BaseJpa base = new BaseJpa(Partenaire.class)) {
            var repository = base.creerRepository(PartenaireRepository.class);
            Object habilite = nouveau(Partenaire.class,
                Map.of("nom", "Centre fictif A", "type", "Hôpital", "pays", "Pays fictif",
                    "adresse", "Adresse A", "habilite", true));
            Object nonHabilite = nouveau(Partenaire.class,
                Map.of("nom", "Centre fictif B", "type", "Hôpital", "pays", "Pays fictif",
                    "adresse", "Adresse B", "habilite", false));
            base.entityManager.getTransaction().begin();
            repository.saveAndFlush(habilite);
            repository.saveAndFlush(nonHabilite);
            Long id = (Long) lire(habilite, "id");
            assertNotNull(id);
            base.entityManager.clear();
            Object recharge = repository.findById(id).orElseThrow();
            assertEquals("Centre fictif A", lire(recharge, "nom"));
            assertEquals(true, lire(recharge, "habilite"));
            List<?> resultats = (List<?>) rechercherSansParametre(repository, "findByHabiliteTrue");
            assertEquals(1, resultats.size());
            assertEquals(id, lire(resultats.getFirst(), "id"));
            base.entityManager.getTransaction().commit();
        }
    }
}
