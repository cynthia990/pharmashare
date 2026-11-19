package be.profacile.pharmashare.domain.repository;

import be.profacile.pharmashare.domain.model.Partenaire;
import java.util.Optional;
import java.util.UUID;

public interface PartenaireRepository {
    Optional<Partenaire> findById(UUID id);
}
