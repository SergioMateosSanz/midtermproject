package com.ironhack.midtermproject.repository;

import com.ironhack.midtermproject.model.Checking;
import com.ironhack.midtermproject.model.Owner;
import com.ironhack.midtermproject.model.Saving;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CheckingRepository extends JpaRepository<Checking, Integer> {

    @Query("SELECT c FROM Checking AS c WHERE c.primaryOwner = :owner ORDER BY c.id")
    List<Checking> getAllByOwner(@Param("owner") Owner owner);
}
