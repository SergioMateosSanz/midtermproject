package com.ironhack.midtermproject.repository;

import com.ironhack.midtermproject.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AddressRepository extends JpaRepository<Address, Integer> {

    @Query("SELECT a FROM Address AS a WHERE a.mailingAddress = :address")
    List<Address> findByMailingAddress(@Param("address") String mailing);
}
