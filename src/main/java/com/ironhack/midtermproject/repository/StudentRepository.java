package com.ironhack.midtermproject.repository;

import com.ironhack.midtermproject.model.Owner;
import com.ironhack.midtermproject.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentRepository extends JpaRepository<Student, Integer> {

    @Query("SELECT s FROM Student AS s WHERE s.primaryOwner = :owner ORDER BY s.id")
    List<Student> getAllByOwner(@Param("owner") Owner owner);
}
