package be.profacile.pharmashare.domain.service;

import be.profacile.pharmashare.domain.repository.LotRepository;
import be.profacile.pharmashare.domain.repository.PartenaireRepository;
import java.time.Clock;
import java.util.UUID;

/** Florent — vérifier le partenaire et le lot, puis enregistrer la demande. */
public final class DonService {
    public DonService(LotRepository lots, PartenaireRepository partenaires, Clock clock) {
        throw new UnsupportedOperationException("TODO Florent : injecter les ports");
    }

    public void demander(UUID lotId, UUID partenaireId) {
        throw new UnsupportedOperationException("TODO Florent : demander le lot");
    }
}
