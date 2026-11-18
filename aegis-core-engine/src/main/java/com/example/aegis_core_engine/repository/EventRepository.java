package com.example.aegis_core_engine.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.aegis_core_engine.model.Event;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    // JpaRepository gives us built-in methods like:
    // - save(Event event) -> INSERT or UPDATE
    // - findById(Long id) -> SELECT * FROM events WHERE id = ?
    // - findAll() -> SELECT * FROM events
    // - deleteById(Long id) -> DELETE FROM events WHERE id = ?
}