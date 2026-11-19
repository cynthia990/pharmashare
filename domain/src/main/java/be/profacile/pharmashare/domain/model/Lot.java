package be.profacile.pharmashare.domain.model;

import java.time.LocalDate;
import java.util.UUID;

/** Idriss — agrégat contrôlant ses transitions, sans setters publics. */
public final class Lot {
    public Lot(UUID id, UUID medicamentId, UUID pharmacieId, String numero,
               int quantite, LocalDate peremption, LocalDate aujourdHui) {
        throw new UnsupportedOperationException("TODO Idriss : déclaration du lot");
    }

    public UUID id() {
        throw new UnsupportedOperationException("TODO Idriss");
    }
    public UUID medicamentId() {
        throw new UnsupportedOperationException("TODO Idriss");
    }
    public UUID pharmacieId() {
        throw new UnsupportedOperationException("TODO Idriss");
    }
    public String numero() {
        throw new UnsupportedOperationException("TODO Idriss");
    }
    public int quantite() {
        throw new UnsupportedOperationException("TODO Idriss");
    }
    public EtatLot etat() {
        throw new UnsupportedOperationException("TODO Idriss");
    }
    public LocalDate peremption() {
        throw new UnsupportedOperationException("TODO Idriss");
    }
    public UUID partenaireId() {
        throw new UnsupportedOperationException("TODO Idriss");
    }
    public void proposer(String justificatif, LocalDate date) {
        throw new UnsupportedOperationException("TODO Idriss");
    }
    public void demander(UUID partenaireId, LocalDate date) {
        throw new UnsupportedOperationException("TODO Idriss");
    }
    public void reserver(LocalDate date) {
        throw new UnsupportedOperationException("TODO Idriss");
    }
    public void remettre(LocalDate date) {
        throw new UnsupportedOperationException("TODO Idriss");
    }
    public void confirmerReception(LocalDate date) {
        throw new UnsupportedOperationException("TODO Idriss");
    }
    public void signalerAnomalie() {
        throw new UnsupportedOperationException("TODO Idriss");
    }
    public void retirer() {
        throw new UnsupportedOperationException("TODO Idriss");
    }
}
