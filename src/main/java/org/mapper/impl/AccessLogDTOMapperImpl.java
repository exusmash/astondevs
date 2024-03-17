package org.mapper.impl;

import org.dto.AccessLogIncomingDTO;
import org.dto.AccessLogOutGoingDTO;
import org.dto.AccessLogUpdateDTO;
import org.dto.EmployeeSmallOutGoingDTO;
import org.mapper.AccessLogDTOMapper;
import org.model.AccessLog;
import org.model.Employee;

import java.util.List;

public class AccessLogDTOMapperImpl implements AccessLogDTOMapper {
    private static AccessLogDTOMapper instance;

    private AccessLogDTOMapperImpl() {
    }

    public static synchronized AccessLogDTOMapper getInstance() {
        if (instance == null) {
            instance = new AccessLogDTOMapperImpl();
        }
        return instance;
    }

    @Override
    public AccessLog map(AccessLogIncomingDTO accessLogDTO) {
        return new AccessLog(
                null,
                accessLogDTO.getDescription(),
                null
        );
    }

    @Override
    public AccessLogOutGoingDTO map(AccessLog accessLog) {
        return new AccessLogOutGoingDTO(
                accessLog.getId(),
                accessLog.getDescription(),
                accessLog.getEmployee() == null ?
                        null :
                        new EmployeeSmallOutGoingDTO(
                                accessLog.getEmployee().getId(),
                                accessLog.getEmployee().getFirstName(),
                                accessLog.getEmployee().getLastName()
                        )
        );
    }

    @Override
    public List<AccessLogOutGoingDTO> map(List<AccessLog> accessLogList) {
        return accessLogList.stream().map(this::map).toList();
    }

    @Override
    public List<AccessLog> mapUpdateList(List<AccessLogUpdateDTO> accessLogUpdateList) {
        return accessLogUpdateList.stream().map(this::map).toList();
    }

    @Override
    public AccessLog map(AccessLogUpdateDTO accessLogDTO) {
        return new AccessLog(
                accessLogDTO.getId(),
                accessLogDTO.getDescription(),
                new Employee(
                        accessLogDTO.getEmployeeId(),
                        null,
                        null,
                        null,
                        List.of(),
                        List.of()
                )
        );
    }
}
