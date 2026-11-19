package be.profacile.pharmashare.domain.repository;

import be.profacile.pharmashare.domain.model.Lot;
import java.util.Optional;
import java.util.UUID;

/** Port fourni. L'adaptateur de persistance appartient au prochain sprint. */
public interface LotRepository {
    Optional<Lot> findById(UUID id);
    void save(Lot lot);
}
