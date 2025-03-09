package org.nmu.labwork.repositories;

import org.nmu.labwork.models.Phone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PhonesRepository extends JpaRepository<Phone,Long> {
}
