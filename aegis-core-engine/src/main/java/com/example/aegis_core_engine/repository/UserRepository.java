package com.example.aegis_core_engine.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.aegis_core_engine.model.User;

import java.util.Optional;

// @Repository tells Spring that this interface is a Data Access Component. 
// It also enables automatic exception translation (turning SQL exceptions into Spring Data exceptions).
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Spring Data JPA uses "Method Name Derivation". 
    // Because this method is named "findByUsername", Spring automatically reads the name 
    // and generates the SQL query: SELECT * FROM users WHERE username = ?
    Optional<User> findByUsername(String username);

    // Generates SQL: SELECT * FROM users WHERE email = ?
    // Used during registration to check if an email is already registered.
    boolean existsByEmail(String email);
}