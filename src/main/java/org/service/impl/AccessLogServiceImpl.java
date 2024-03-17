package org.service.impl;

import org.dto.AccessLogIncomingDTO;
import org.dto.AccessLogOutGoingDTO;
import org.dto.AccessLogUpdateDTO;
import org.exeption.NotFoundException;
import org.mapper.AccessLogDTOMapper;
import org.mapper.impl.AccessLogDTOMapperImpl;
import org.model.AccessLog;
import org.repository.AccessLogRepository;
import org.repository.impl.AccessLogRepositoryImpl;
import org.service.AccessLogService;

import java.util.List;

public class AccessLogServiceImpl implements AccessLogService {
    private final AccessLogDTOMapper accessLogDTOMapper = AccessLogDTOMapperImpl.getInstance();
    private static AccessLogService instance;
    private final AccessLogRepository accessLogRepository = AccessLogRepositoryImpl.getInstance();


    private AccessLogServiceImpl() {
    }

    public static synchronized AccessLogService getInstance() {
        if (instance == null) {
            instance = new AccessLogServiceImpl();
        }
        return instance;
    }

    @Override
    public AccessLogOutGoingDTO save(AccessLogIncomingDTO accessLogDto) {
        AccessLog accessLog = accessLogDTOMapper.map(accessLogDto);
        accessLog = accessLogRepository.save(accessLog);
        return accessLogDTOMapper.map(accessLog);
    }

    @Override
    public void update(AccessLogUpdateDTO accessLogUpdateDTO) throws NotFoundException {
        if (accessLogRepository.exitsById(accessLogUpdateDTO.getId())) {
            AccessLog accessLog = accessLogDTOMapper.map(accessLogUpdateDTO);
            accessLogRepository.update(accessLog);
        } else {
            throw new NotFoundException("Access log not found.");
        }
    }

    @Override
    public AccessLogOutGoingDTO findById(Long accessLogId) throws NotFoundException {
        AccessLog accessLog = accessLogRepository.findById(accessLogId).orElseThrow(() ->
                new NotFoundException("Access log not found."));
        return accessLogDTOMapper.map(accessLog);
    }

    @Override
    public List<AccessLogOutGoingDTO> findAll() {
        List<AccessLog> accessLogList = accessLogRepository.findAll();
        return accessLogDTOMapper.map(accessLogList);
    }

    @Override
    public boolean delete(Long accessLogId) {
        return accessLogRepository.deleteById(accessLogId);
    }
}
