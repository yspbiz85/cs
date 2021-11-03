package com.cs.coding.test.repository;

import com.cs.coding.test.entity.LogEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LogEventRepository extends JpaRepository<LogEvent,Long> {

}
