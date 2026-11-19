package be.profacile.pharmashare.domain.service;

import be.profacile.pharmashare.domain.model.FraicheurStock;
import be.profacile.pharmashare.domain.model.OffreStock;
import be.profacile.pharmashare.domain.model.PublicationStock;
import be.profacile.pharmashare.domain.repository.PublicationStockRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

/** Jordan — publication validée avant l'unique sauvegarde. */
public final class PublicationStockService {
    public PublicationStockService(PublicationStockRepository repository, Clock clock) {
        throw new UnsupportedOperationException("TODO Jordan : injecter les dépendances");
    }

    public PublicationStock publier(UUID pharmacieId, List<OffreStock> offres) {
        throw new UnsupportedOperationException("TODO Jordan : publier le stock");
    }

    public FraicheurStock fraicheur(Instant publieLe) {
        throw new UnsupportedOperationException("TODO Jordan : calculer la fraîcheur");
    }
}
