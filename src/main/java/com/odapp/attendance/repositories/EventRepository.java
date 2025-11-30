package com.odapp.attendance.repositories;

import com.odapp.attendance.models.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findTop5ByOrderByEventDateDesc();
}
