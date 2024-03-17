package org.repository;

import org.model.AccessLog;

import java.util.List;
import java.util.Optional;

public interface AccessLogRepository extends Repository<AccessLog, Long> {
    List<AccessLog> findAllByEmployeeId(Long employeeId);

    boolean deleteByEmployeeId(Long userId);

    boolean existsByDescription(String description);

    Optional<AccessLog> findByDescription(String description);
}
