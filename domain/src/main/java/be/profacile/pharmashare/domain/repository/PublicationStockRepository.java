package be.profacile.pharmashare.domain.repository;

import be.profacile.pharmashare.domain.model.PublicationStock;
import java.util.Optional;
import java.util.UUID;

public interface PublicationStockRepository {
    Optional<PublicationStock> findByPharmacieId(UUID pharmacieId);
    void save(PublicationStock publication);
}
