package com.ironhack.midtermproject.repository;

import com.ironhack.midtermproject.model.Saving;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SavingRepository extends JpaRepository<Saving, Integer> {
}
