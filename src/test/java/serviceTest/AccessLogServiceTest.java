package serviceTest;

import org.dto.AccessLogIncomingDTO;
import org.dto.AccessLogOutGoingDTO;
import org.dto.AccessLogUpdateDTO;
import org.exeption.NotFoundException;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.model.AccessLog;
import org.model.Role;
import org.repository.AccessLogRepository;
import org.repository.impl.AccessLogRepositoryImpl;
import org.service.AccessLogService;
import org.service.impl.AccessLogServiceImpl;

import java.lang.reflect.Field;
import java.util.Optional;

public class AccessLogServiceTest {
    private static AccessLogService accessLogService;
    private static AccessLogRepository mockAccessLogRepository;
    private static AccessLogRepositoryImpl oldInstance;

    private static void setMock(AccessLogRepository mock) {
        try {
            Field instance = AccessLogRepositoryImpl.class.getDeclaredField("instance");
            instance.setAccessible(true);
            oldInstance = (AccessLogRepositoryImpl) instance.get(instance);
            instance.set(instance, mock);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeAll
    static void beforeAll() {
        mockAccessLogRepository = Mockito.mock(AccessLogRepository.class);
        setMock(mockAccessLogRepository);
        accessLogService = AccessLogServiceImpl.getInstance();
    }

    @AfterAll
    static void afterAll() throws Exception {
        Field instance = AccessLogRepositoryImpl.class.getDeclaredField("instance");
        instance.setAccessible(true);
        instance.set(instance, oldInstance);
    }

    @BeforeEach
    void setUp() {
        Mockito.reset(mockAccessLogRepository);
    }

    @Test
    void save() {
        Long expectedId = 1L;

        AccessLogIncomingDTO dto = new AccessLogIncomingDTO("desc");
        AccessLog accessLog = new AccessLog(expectedId, "desc", null);

        Mockito.doReturn(accessLog).when(mockAccessLogRepository).save(Mockito.any(AccessLog.class));

        AccessLogOutGoingDTO result = accessLogService.save(dto);

        Assertions.assertEquals(expectedId, result.getId());
    }

    @Test
    void update() throws NotFoundException {
        Long expectedId = 1L;

        AccessLogUpdateDTO dto = new AccessLogUpdateDTO(expectedId, "desc", null);

        Mockito.doReturn(true).when(mockAccessLogRepository).exitsById(Mockito.any());

        accessLogService.update(dto);

        ArgumentCaptor<AccessLog> argumentCaptor = ArgumentCaptor.forClass(AccessLog.class);
        Mockito.verify(mockAccessLogRepository).update(argumentCaptor.capture());

        AccessLog result = argumentCaptor.getValue();
        Assertions.assertEquals(expectedId, result.getId());
    }

    @Test
    void updateNotFound() {
        AccessLogUpdateDTO dto = new AccessLogUpdateDTO(1L, "desc", null);

        Mockito.doReturn(false).when(mockAccessLogRepository).exitsById(Mockito.any());

        NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> {
                    accessLogService.update(dto);
                }, "Not found."
        );
        Assertions.assertEquals("Access log not found.", exception.getMessage());
    }

    @Test
    void findById() throws NotFoundException {
        Long expectedId = 1L;

        Optional<AccessLog> accessLog = Optional.of(new AccessLog(expectedId, "desc", null));

        Mockito.doReturn(true).when(mockAccessLogRepository).exitsById(Mockito.any());
        Mockito.doReturn(accessLog).when(mockAccessLogRepository).findById(Mockito.anyLong());

        AccessLogOutGoingDTO dto = accessLogService.findById(expectedId);

        Assertions.assertEquals(expectedId, dto.getId());
    }

    @Test
    void findByIdNotFound() {
        Optional<Role> role = Optional.empty();

        Mockito.doReturn(false).when(mockAccessLogRepository).exitsById(Mockito.any());

        NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> {
                    accessLogService.findById(1L);
                }, "Not found."
        );
        Assertions.assertEquals("Access log not found.", exception.getMessage());
    }

    @Test
    void findAll() {
        accessLogService.findAll();
        Mockito.verify(mockAccessLogRepository).findAll();
    }

    @Test
    void delete() {
        Long expectedId = 100L;

        accessLogService.delete(expectedId);

        ArgumentCaptor<Long> argumentCaptor = ArgumentCaptor.forClass(Long.class);
        Mockito.verify(mockAccessLogRepository).deleteById(argumentCaptor.capture());

        Long result = argumentCaptor.getValue();
        Assertions.assertEquals(expectedId, result);
    }
}
