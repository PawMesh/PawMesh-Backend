package com.pawmesh.backend.domain.dog.repository;

import com.pawmesh.backend.domain.dog.entity.Pet;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PetRepository extends JpaRepository<Pet, Long> {

    Optional<Pet> findByUserUserId(Long userId);
}
