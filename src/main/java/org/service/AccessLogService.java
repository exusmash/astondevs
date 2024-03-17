package org.service;

import org.dto.AccessLogIncomingDTO;
import org.dto.AccessLogOutGoingDTO;
import org.dto.AccessLogUpdateDTO;
import org.exeption.NotFoundException;

import java.util.List;

public interface AccessLogService {
    AccessLogOutGoingDTO save(AccessLogIncomingDTO accessLog);

    void update(AccessLogUpdateDTO accessLog) throws NotFoundException;

    AccessLogOutGoingDTO findById(Long accessLogId) throws NotFoundException;

    List<AccessLogOutGoingDTO> findAll();

    boolean delete(Long accessLogId);
}
