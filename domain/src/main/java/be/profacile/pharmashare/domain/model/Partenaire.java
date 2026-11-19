package be.profacile.pharmashare.domain.model;

import java.util.UUID;

/** Contrat fourni pour le service de Florent. Habilitation fictive du pilote. */
public record Partenaire(UUID id, boolean habilite) {
    public Partenaire {
        if (id == null) {
            throw new IllegalArgumentException("Identifiant du partenaire obligatoire");
        }
    }
}
