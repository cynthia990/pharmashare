package be.profacile.pharmashare.domain.model;

import java.time.LocalDate;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import static org.junit.jupiter.api.Assertions.*;

class LotTest {
    private final LocalDate date = LocalDate.of(2026, 11, 19);
    private final UUID lotId = UUID.fromString("00000000-0000-0000-0000-000000000010");
    private final UUID medicamentId = UUID.fromString("00000000-0000-0000-0000-000000000011");
    private final UUID pharmacieId = UUID.fromString("00000000-0000-0000-0000-000000000012");
    private final UUID partenaireId = UUID.fromString("00000000-0000-0000-0000-000000000013");

    private Lot nouveauLot() {
        return new Lot(lotId, medicamentId, pharmacieId, "LOT-FICTIF", 10, date.plusDays(30), date);
    }

    @Test
    void declareUnLotACompleter() {
        Lot lot = nouveauLot();
        assertEquals(lotId, lot.id());
        assertEquals(medicamentId, lot.medicamentId());
        assertEquals(pharmacieId, lot.pharmacieId());
        assertEquals("LOT-FICTIF", lot.numero());
        assertEquals(10, lot.quantite());
        assertEquals(date.plusDays(30), lot.peremption());
        assertEquals(EtatLot.A_COMPLETER, lot.etat());
        assertNull(lot.partenaireId());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, Integer.MIN_VALUE})
    void refuseUneQuantiteNonPositive(int quantite) {
        assertThrows(IllegalArgumentException.class, () -> new Lot(lotId, medicamentId,
            pharmacieId, "LOT", quantite, date.plusDays(30), date));
    }

    @Test
    void refuseUnePeremptionAtteinte() {
        assertThrows(IllegalArgumentException.class, () -> new Lot(lotId, medicamentId,
            pharmacieId, "LOT", 10, date, date));
        assertThrows(IllegalArgumentException.class, () -> new Lot(lotId, medicamentId,
            pharmacieId, "LOT", 10, date.minusDays(1), date));
    }

    @Test
    void exigeLesDonneesDeTracabilite() {
        assertAll(
            () -> assertThrows(IllegalArgumentException.class, () -> new Lot(null, medicamentId,
                pharmacieId, "LOT", 10, date.plusDays(30), date)),
            () -> assertThrows(IllegalArgumentException.class, () -> new Lot(lotId, null,
                pharmacieId, "LOT", 10, date.plusDays(30), date)),
            () -> assertThrows(IllegalArgumentException.class, () -> new Lot(lotId, medicamentId,
                null, "LOT", 10, date.plusDays(30), date)),
            () -> assertThrows(IllegalArgumentException.class, () -> new Lot(lotId, medicamentId,
                pharmacieId, " ", 10, date.plusDays(30), date)),
            () -> assertThrows(IllegalArgumentException.class, () -> new Lot(lotId, medicamentId,
                pharmacieId, "LOT", 10, null, date)),
            () -> assertThrows(IllegalArgumentException.class, () -> new Lot(lotId, medicamentId,
                pharmacieId, "LOT", 10, date.plusDays(30), null))
        );
    }

    @Test
    void uneDemandeSansBeneficiaireNeChangePasLeLot() {
        Lot lot = nouveauLot();
        lot.proposer("origine.pdf", date);
        assertThrows(IllegalArgumentException.class, () -> lot.demander(null, date));
        assertEquals(EtatLot.PROPOSE, lot.etat());
        assertNull(lot.partenaireId());
    }

    @Test
    void refuseLaRemiseSiLeLotAExpireDepuisLaReservation() {
        Lot lot = nouveauLot();
        lot.proposer("origine.pdf", date);
        lot.demander(partenaireId, date);
        lot.reserver(date);
        assertThrows(IllegalStateException.class, () -> lot.remettre(lot.peremption()));
        assertEquals(EtatLot.RESERVE, lot.etat());
    }

    @Test
    void exigeUnJustificatifAvantLaProposition() {
        Lot lot = nouveauLot();
        assertThrows(IllegalArgumentException.class, () -> lot.proposer(" ", date));
        assertEquals(EtatLot.A_COMPLETER, lot.etat());
        lot.proposer("origine-fictive.pdf", date);
        assertEquals(EtatLot.PROPOSE, lot.etat());
    }

    @Test
    void suitLeParcoursNominalEtConserveLeBeneficiaire() {
        Lot lot = nouveauLot();
        lot.proposer("origine-fictive.pdf", date);
        lot.demander(partenaireId, date);
        assertEquals(EtatLot.DEMANDE, lot.etat());
        assertEquals(partenaireId, lot.partenaireId());
        lot.reserver(date);
        assertEquals(EtatLot.RESERVE, lot.etat());
        lot.remettre(date);
        assertEquals(EtatLot.REMIS, lot.etat());
        lot.confirmerReception(date);
        assertEquals(EtatLot.RECEPTIONNE, lot.etat());
    }

    @Test
    void refuseLesSautsEtUneDeuxiemeDemandeSansChangerLeBeneficiaire() {
        Lot lot = nouveauLot();
        assertThrows(IllegalStateException.class, () -> lot.remettre(date));
        lot.proposer("origine-fictive.pdf", date);
        lot.demander(partenaireId, date);
        assertThrows(IllegalStateException.class, () -> lot.demander(pharmacieId, date));
        assertEquals(partenaireId, lot.partenaireId());
        assertEquals(EtatLot.DEMANDE, lot.etat());
    }

    @Test
    void refuseUneDemandeAExpirationSansModifierLeLot() {
        Lot lot = nouveauLot();
        lot.proposer("origine-fictive.pdf", date);
        assertThrows(IllegalStateException.class,
            () -> lot.demander(partenaireId, lot.peremption()));
        assertEquals(EtatLot.PROPOSE, lot.etat());
        assertNull(lot.partenaireId());
    }

    @Test
    void traiteUneAnomalieApresRemisePuisLaReception() {
        Lot lot = nouveauLot();
        assertThrows(IllegalStateException.class, lot::signalerAnomalie);
        lot.proposer("origine-fictive.pdf", date);
        lot.demander(partenaireId, date);
        lot.reserver(date);
        lot.remettre(date);
        lot.signalerAnomalie();
        assertEquals(EtatLot.EN_ANOMALIE, lot.etat());
        lot.confirmerReception(date);
        assertEquals(EtatLot.RECEPTIONNE, lot.etat());
        assertThrows(IllegalStateException.class, lot::retirer);
    }

    @Test
    void unLotRetireResteIndisponible() {
        Lot lot = nouveauLot();
        lot.proposer("origine-fictive.pdf", date);
        lot.retirer();
        assertEquals(EtatLot.RETIRE, lot.etat());
        assertThrows(IllegalStateException.class, () -> lot.demander(partenaireId, date));
        assertThrows(IllegalStateException.class, () -> lot.proposer("origine.pdf", date));
    }
}
