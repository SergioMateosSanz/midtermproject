package com.ironhack.midtermproject.repository;

import com.ironhack.midtermproject.model.CreditCard;
import com.ironhack.midtermproject.model.Owner;
import com.ironhack.midtermproject.model.Saving;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CreditCardRepository extends JpaRepository<CreditCard, Integer> {

    @Query("SELECT c FROM CreditCard AS c WHERE c.primaryOwner = :owner ORDER BY c.id")
    List<CreditCard> getAllByOwner(@Param("owner") Owner owner);
}
