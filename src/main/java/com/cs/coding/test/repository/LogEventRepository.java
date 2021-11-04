package com.cs.coding.test.repository;

import com.cs.coding.test.entity.LogEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LogEventRepository extends JpaRepository<LogEventEntity,Long> {
    Optional<LogEventEntity> findLogEventByEventId(String eventId);
    List<LogEventEntity> findLogEventByEventAlert(Boolean eventAlert);
}
