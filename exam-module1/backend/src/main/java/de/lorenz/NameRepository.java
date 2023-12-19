package de.lorenz;

import org.springframework.data.repository.CrudRepository;

import de.lorenz.models.Name;

public interface NameRepository extends CrudRepository<Name, Long> {
}
