package org.nmu.labwork.repositories;

import jakarta.transaction.Transactional;
import org.nmu.labwork.models.PhoneSearch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PhoneSearchRepository extends JpaRepository<PhoneSearch, Integer> {
    Optional<PhoneSearch> getFirstBySearchOrderByCreatedAtDesc(String search);
}
