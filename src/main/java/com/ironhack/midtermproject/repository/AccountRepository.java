package com.ironhack.midtermproject.repository;

import com.ironhack.midtermproject.enums.MovementType;
import com.ironhack.midtermproject.model.Account;
import com.ironhack.midtermproject.model.Owner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountRepository extends JpaRepository<Account, Integer> {

    @Query("SELECT a FROM Account AS a WHERE a.primaryOwner = :owner ORDER BY a.id")
    List<Account> findAllByOwner(@Param("owner") Owner owner);

    @Query("SELECT a FROM Account AS a LEFT JOIN FETCH a.movementList m WHERE a.id = :id and m.movementType = :type ORDER BY m.id DESC")
    List<Account> findByIdAndMovementType(@Param("id") int id,
                                          @Param("type") MovementType movementType);

    @Query("SELECT a FROM Account AS a LEFT JOIN FETCH a.movementList m WHERE a.id = :id")
    List<Account> getByIdWithMovements(@Param("id") int id);
}
