package com.ironhack.midtermproject.repository;

import com.ironhack.midtermproject.model.Owner;
import com.ironhack.midtermproject.model.Saving;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SavingRepository extends JpaRepository<Saving, Integer> {

    @Query("SELECT s FROM Saving AS s WHERE s.primaryOwner = :owner ORDER BY s.id")
    List<Saving> getAllByOwner(@Param("owner") Owner owner);
}
