package mapperTest;

import org.dto.AccessLogIncomingDTO;
import org.dto.AccessLogOutGoingDTO;
import org.dto.AccessLogUpdateDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapper.AccessLogDTOMapper;
import org.mapper.impl.AccessLogDTOMapperImpl;
import org.model.AccessLog;
import org.model.Employee;

import java.util.List;

public class AccessLogDTOMapperTest {
    private AccessLogDTOMapper accessLogDTOMapper;

    @BeforeEach
    void setUp() {
        accessLogDTOMapper = AccessLogDTOMapperImpl.getInstance();
    }

    @DisplayName("AccessLog map(AccessLogIncomingDTO")
    @Test
    void mapIncoming() {
        AccessLogIncomingDTO dto = new AccessLogIncomingDTO("desc");

        AccessLog result = accessLogDTOMapper.map(dto);
        Assertions.assertNull(result.getId());
        Assertions.assertEquals(dto.getDescription(), result.getDescription());
    }

    @DisplayName("AccessLogOutGoingDTO map(AccessLog")
    @Test
    void testMapOutgoing() {
        AccessLog accessLog = new AccessLog(
                100L,
                "desc",
                new Employee(3L,
                        "f1",
                        "f2",
                        null,
                        List.of(),
                        List.of())
        );

        AccessLogOutGoingDTO result = accessLogDTOMapper.map(accessLog);

        Assertions.assertEquals(accessLog.getId(), result.getId());
        Assertions.assertEquals(accessLog.getDescription(), result.getDescription());
        Assertions.assertEquals(accessLog.getEmployee().getId(), result.getEmployeeDto().getId());
    }

    @DisplayName("List<AccessLogOutGoingDTO> map(List<AccessLog>")
    @Test
    void testMapList() {
        List<AccessLog> accessLogList = List.of(
                new AccessLog(
                        100L,
                        "+desc",
                        new Employee(3L,
                                "f1",
                                "f2",
                                null,
                                List.of(),
                                List.of())
                ),
                new AccessLog(
                        101L,
                        "desc",
                        new Employee(4L,
                                "f3",
                                "f4",
                                null,
                                List.of(),
                                List.of())
                )

        );

        List<AccessLogOutGoingDTO> result = accessLogDTOMapper.map(accessLogList);

        Assertions.assertEquals(accessLogList.size(), result.size());
    }

    @DisplayName("List<AccessLog> mapUpdateList(List<AccessLogUpdateDTO>")
    @Test
    void mapUpdateList() {
        List<AccessLogUpdateDTO> updateDtoList = List.of(
                new AccessLogUpdateDTO(
                        100L,
                        "desc", 1L
                ),
                new AccessLogUpdateDTO(
                        101L,
                        "desc",
                        2L
                )
        );

        List<AccessLog> result = accessLogDTOMapper.mapUpdateList(updateDtoList);

        Assertions.assertEquals(updateDtoList.size(), result.size());
    }

    @DisplayName("AccessLog map(AccessLogUpdateDTO ")
    @Test
    void testMapUpdate() {
        AccessLogUpdateDTO dto = new AccessLogUpdateDTO(
                100L,
                "desc",
                1L
        );

        AccessLog result = accessLogDTOMapper.map(dto);

        Assertions.assertEquals(dto.getId(), result.getId());
        Assertions.assertEquals(dto.getDescription(), result.getDescription());
    }
}
