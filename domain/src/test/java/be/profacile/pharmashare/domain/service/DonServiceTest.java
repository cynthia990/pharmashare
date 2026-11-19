package be.profacile.pharmashare.domain.service;

import be.profacile.pharmashare.domain.model.*;
import be.profacile.pharmashare.domain.repository.*;
import java.time.*;
import java.util.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DonServiceTest {
    private final Clock clock = Clock.fixed(Instant.parse("2026-11-19T18:00:00Z"), ZoneOffset.UTC);
    private final UUID lotId = UUID.fromString("00000000-0000-0000-0000-000000000030");
    private final UUID partenaireId = UUID.fromString("00000000-0000-0000-0000-000000000031");
    private final LotsEnMemoire lots = new LotsEnMemoire();

    private Lot lotPropose() {
        LocalDate date = LocalDate.now(clock);
        Lot lot = new Lot(lotId, UUID.randomUUID(), UUID.randomUUID(), "FICTIF", 10,
            date.plusDays(30), date);
        lot.proposer("origine-fictive.pdf", date);
        lots.donnees.put(lotId, lot);
        return lot;
    }

    @Test
    void demandeUnLotPourUnPartenaireHabilite() {
        Lot lot = lotPropose();
        DonService service = new DonService(lots,
            id -> Optional.of(new Partenaire(id, true)), clock);
        service.demander(lotId, partenaireId);
        assertEquals(EtatLot.DEMANDE, lot.etat());
        assertEquals(partenaireId, lot.partenaireId());
        assertEquals(1, lots.sauvegardes);
    }

    @Test
    void refuseUnPartenaireNonHabiliteSansChangerLeLot() {
        Lot lot = lotPropose();
        DonService service = new DonService(lots,
            id -> Optional.of(new Partenaire(id, false)), clock);
        assertThrows(IllegalStateException.class, () -> service.demander(lotId, partenaireId));
        assertEquals(EtatLot.PROPOSE, lot.etat());
        assertNull(lot.partenaireId());
        assertEquals(0, lots.sauvegardes);
    }

    @Test
    void signaleUnLotInconnuSansSauvegarder() {
        DonService service = new DonService(lots,
            id -> Optional.of(new Partenaire(id, true)), clock);
        assertThrows(NoSuchElementException.class, () -> service.demander(lotId, partenaireId));
        assertEquals(0, lots.sauvegardes);
    }

    @Test
    void signaleUnPartenaireInconnuSansChangerLeLot() {
        Lot lot = lotPropose();
        DonService service = new DonService(lots, id -> Optional.empty(), clock);
        assertThrows(NoSuchElementException.class, () -> service.demander(lotId, partenaireId));
        assertEquals(EtatLot.PROPOSE, lot.etat());
        assertEquals(0, lots.sauvegardes);
    }

    @Test
    void refuseUneDeuxiemeDemandeSansSauvegarderDeuxFois() {
        Lot lot = lotPropose();
        DonService service = new DonService(lots,
            id -> Optional.of(new Partenaire(id, true)), clock);
        service.demander(lotId, partenaireId);
        assertThrows(IllegalStateException.class, () -> service.demander(lotId, partenaireId));
        assertEquals(partenaireId, lot.partenaireId());
        assertEquals(1, lots.sauvegardes);
    }

    @Test
    void refuseUnLotExpireSelonLHorlogeInjectee() {
        Lot lot = lotPropose();
        Clock plusTard = Clock.offset(clock, Duration.ofDays(30));
        DonService service = new DonService(lots,
            id -> Optional.of(new Partenaire(id, true)), plusTard);
        assertThrows(IllegalStateException.class, () -> service.demander(lotId, partenaireId));
        assertEquals(EtatLot.PROPOSE, lot.etat());
        assertEquals(0, lots.sauvegardes);
    }

    @Test
    void exigeLesDependancesEtLesIdentifiants() {
        PartenaireRepository partenaires = id -> Optional.of(new Partenaire(id, true));
        assertThrows(IllegalArgumentException.class, () -> new DonService(null, partenaires, clock));
        assertThrows(IllegalArgumentException.class, () -> new DonService(lots, null, clock));
        assertThrows(IllegalArgumentException.class, () -> new DonService(lots, partenaires, null));
        DonService service = new DonService(lots, partenaires, clock);
        assertThrows(IllegalArgumentException.class, () -> service.demander(null, partenaireId));
        assertThrows(IllegalArgumentException.class, () -> service.demander(lotId, null));
        assertEquals(0, lots.sauvegardes);
    }

    private static final class LotsEnMemoire implements LotRepository {
        private final Map<UUID, Lot> donnees = new HashMap<>();
        private int sauvegardes;

        @Override
        public Optional<Lot> findById(UUID id) {
            return Optional.ofNullable(donnees.get(id));
        }

        @Override
        public void save(Lot lot) {
            donnees.put(lot.id(), lot);
            sauvegardes++;
        }
    }
}
