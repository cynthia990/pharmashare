package be.profacile.pharmashare.domain.service;

import be.profacile.pharmashare.domain.model.*;
import be.profacile.pharmashare.domain.repository.PublicationStockRepository;
import java.time.*;
import java.util.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import static org.junit.jupiter.api.Assertions.*;

class PublicationStockServiceTest {
    private final Instant maintenant = Instant.parse("2026-11-19T18:00:00Z");
    private final Clock clock = Clock.fixed(maintenant, ZoneOffset.UTC);
    private final UUID pharmacieId = UUID.fromString("00000000-0000-0000-0000-000000000020");
    private final StockEnMemoire repository = new StockEnMemoire();

    @ParameterizedTest
    @CsvSource({"0,A_JOUR", "86399,A_JOUR", "86400,A_CONFIRMER",
        "259200,A_CONFIRMER", "259201,MASQUE"})
    void respecteLesBornesDeFraicheur(long ageSecondes, FraicheurStock attendu) {
        PublicationStockService service = new PublicationStockService(repository, clock);
        assertEquals(attendu, service.fraicheur(maintenant.minusSeconds(ageSecondes)));
    }

    @Test
    void refuseUnePublicationDateeDansLeFutur() {
        PublicationStockService service = new PublicationStockService(repository, clock);
        assertThrows(IllegalArgumentException.class,
            () -> service.fraicheur(maintenant.plusSeconds(1)));
    }

    @Test
    void ecarteLesLotsExpiresEtSauvegardeLaNouvellePublication() {
        PublicationStockService service = new PublicationStockService(repository, clock);
        LocalDate date = LocalDate.now(clock);
        OffreStock valide = new OffreStock(UUID.randomUUID(), date.plusDays(1));
        OffreStock expire = new OffreStock(UUID.randomUUID(), date);
        PublicationStock resultat = service.publier(pharmacieId, List.of(valide, expire));
        assertEquals(pharmacieId, resultat.pharmacieId());
        assertEquals(maintenant, resultat.publieLe());
        assertEquals(List.of(valide), resultat.offres());
        assertSame(resultat, repository.findByPharmacieId(pharmacieId).orElseThrow());
        assertEquals(1, repository.sauvegardes);
    }

    @Test
    void unImportInvalideConserveLaDernierePublicationReussie() {
        PublicationStockService service = new PublicationStockService(repository, clock);
        OffreStock offre = new OffreStock(UUID.randomUUID(), LocalDate.now(clock).plusDays(1));
        PublicationStock precedente = service.publier(pharmacieId, List.of(offre));
        assertThrows(IllegalArgumentException.class,
            () -> service.publier(pharmacieId, List.of(offre, offre)));
        assertSame(precedente, repository.findByPharmacieId(pharmacieId).orElseThrow());
        assertEquals(1, repository.sauvegardes);
    }

    @Test
    void unePublicationVideEffaceLesAnciennesDisponibilites() {
        PublicationStockService service = new PublicationStockService(repository, clock);
        OffreStock offre = new OffreStock(UUID.randomUUID(), LocalDate.now(clock).plusDays(1));
        service.publier(pharmacieId, List.of(offre));
        service.publier(pharmacieId, List.of());
        assertTrue(repository.findByPharmacieId(pharmacieId).orElseThrow().offres().isEmpty());
    }

    @Test
    void laPhotographieDuStockEstImmuable() {
        List<OffreStock> offres = new ArrayList<>();
        offres.add(new OffreStock(UUID.randomUUID(), LocalDate.now(clock).plusDays(1)));
        PublicationStock publication = new PublicationStock(pharmacieId, maintenant, offres);
        offres.clear();
        assertEquals(1, publication.offres().size());
        assertThrows(UnsupportedOperationException.class, () -> publication.offres().clear());
    }

    @Test
    void accepteUneListeDejaImmuableEtRefuseLesParametresAbsents() {
        OffreStock offre = new OffreStock(UUID.randomUUID(), LocalDate.now(clock).plusDays(1));
        PublicationStock publication = new PublicationStock(pharmacieId, maintenant, List.of(offre));
        assertEquals(List.of(offre), publication.offres());
        assertThrows(IllegalArgumentException.class,
            () -> new PublicationStock(null, maintenant, List.of()));
        assertThrows(IllegalArgumentException.class,
            () -> new PublicationStock(pharmacieId, null, List.of()));
        assertThrows(IllegalArgumentException.class,
            () -> new PublicationStock(pharmacieId, maintenant, null));
    }

    @Test
    void refuseLesDependancesEtLesImportsNulsAvantTouteSauvegarde() {
        assertThrows(IllegalArgumentException.class, () -> new PublicationStockService(null, clock));
        assertThrows(IllegalArgumentException.class, () -> new PublicationStockService(repository, null));
        PublicationStockService service = new PublicationStockService(repository, clock);
        assertThrows(IllegalArgumentException.class, () -> service.publier(null, List.of()));
        assertThrows(IllegalArgumentException.class, () -> service.publier(pharmacieId, null));
        List<OffreStock> listeInvalide = new ArrayList<>();
        listeInvalide.add(null);
        assertThrows(IllegalArgumentException.class, () -> service.publier(pharmacieId, listeInvalide));
        assertThrows(IllegalArgumentException.class, () -> service.fraicheur(null));
        assertEquals(0, repository.sauvegardes);
    }

    private static final class StockEnMemoire implements PublicationStockRepository {
        private final Map<UUID, PublicationStock> publications = new HashMap<>();
        private int sauvegardes;

        @Override
        public Optional<PublicationStock> findByPharmacieId(UUID id) {
            return Optional.ofNullable(publications.get(id));
        }

        @Override
        public void save(PublicationStock publication) {
            publications.put(publication.pharmacieId(), publication);
            sauvegardes++;
        }
    }
}
