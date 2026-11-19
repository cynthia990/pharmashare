package be.profacile.pharmashare.domain.model;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/** Jordan — photographie immuable d'une publication réussie. */
public final class PublicationStock {
    public PublicationStock(UUID pharmacieId, Instant publieLe, List<OffreStock> offres) {
        throw new UnsupportedOperationException("TODO Jordan : publication immuable");
    }

    public UUID pharmacieId() {
        throw new UnsupportedOperationException("TODO Jordan");
    }
    public Instant publieLe() {
        throw new UnsupportedOperationException("TODO Jordan");
    }
    public List<OffreStock> offres() {
        throw new UnsupportedOperationException("TODO Jordan");
    }
}
