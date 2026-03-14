package com.sa.event_mng.repository;

import com.sa.event_mng.model.entity.Event;
import com.sa.event_mng.model.enums.EventStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    Page<Event> findByStatus(EventStatus status, Pageable pageable);

    List<Event> findByOrganizerId(Long organizerId);
}
