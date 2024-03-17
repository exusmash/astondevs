package org.mapper;

import org.dto.AccessLogIncomingDTO;
import org.dto.AccessLogOutGoingDTO;
import org.dto.AccessLogUpdateDTO;
import org.model.AccessLog;

import java.util.List;

public interface AccessLogDTOMapper {
    AccessLog map(AccessLogIncomingDTO accessLogIncomingDTO);

    AccessLogOutGoingDTO map(AccessLog accessLog);

    List<AccessLogOutGoingDTO> map(List<AccessLog> accessLogList);

    List<AccessLog> mapUpdateList(List<AccessLogUpdateDTO> accessLogUpdateList);

    AccessLog map(AccessLogUpdateDTO accessLogIncomingDTO);
}
