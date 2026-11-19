package be.profacile.pharmashare.domain.model;

import java.time.LocalDate;
import java.util.UUID;

/** Ligne fournie pour isoler l'exercice de Jordan du cycle de don. */
public record OffreStock(UUID lotId, LocalDate peremption) {
    public OffreStock {
        if (lotId == null || peremption == null) {
            throw new IllegalArgumentException("Lot et péremption obligatoires");
        }
    }
}
