package com.pawmesh.backend.domain.user.repository;

import com.pawmesh.backend.domain.user.entity.Pet;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PetRepository extends JpaRepository<Pet, Long> {

    Optional<Pet> findByUserUserId(Long userId);
}
