package mapperTest;

import org.dto.RoleIncomingDTO;
import org.dto.RoleOutGoingDTO;
import org.dto.RoleUpdateDTO;
import org.junit.jupiter.api.*;
import org.mapper.RoleDTOMapper;
import org.mapper.impl.RoleDTOMapperImpl;
import org.model.Role;

import java.util.List;


public class RoleDTOMapperTest {
    private static Role role;
    private static RoleIncomingDTO roleIncomingDTO;
    private static RoleUpdateDTO roleUpdateDTO;
    private static RoleOutGoingDTO roleOutGoingDTO;
    private RoleDTOMapper roleDTOMapper;

    @BeforeAll
    static void beforeAll() {
        role = new Role(
                10L,
                "Role for Test"
        );

        roleIncomingDTO = new RoleIncomingDTO(
                "Incoming DTO"
        );

        roleUpdateDTO = new RoleUpdateDTO(
                100L,
                "Update DTO"
        );
    }

    @BeforeEach
    void setUp() {
        roleDTOMapper = RoleDTOMapperImpl.getInstance();
    }

    @DisplayName("Role map(RoleIncomingDTO")
    @Test
    void mapIncoming() {
        Role resultRole = roleDTOMapper.map(roleIncomingDTO);

        Assertions.assertNull(resultRole.getId());
        Assertions.assertEquals(roleIncomingDTO.getName(), resultRole.getName());
    }

    @DisplayName("Role map(RoleUpdateDTO")
    @Test
    void testMapUpdate() {
        Role resultRole = roleDTOMapper.map(roleUpdateDTO);

        Assertions.assertEquals(roleUpdateDTO.getId(), resultRole.getId());
        Assertions.assertEquals(roleUpdateDTO.getName(), resultRole.getName());
    }

    @DisplayName("RoleOutGoingDTO map(Role")
    @Test
    void testMapOutgoing() {
        RoleOutGoingDTO resultRole = roleDTOMapper.map(role);

        Assertions.assertEquals(role.getId(), resultRole.getId());
        Assertions.assertEquals(role.getName(), resultRole.getName());
    }


    @DisplayName("List<RoleOutGoingDTO> map(List<Role> roleList")
    @Test
    void testMapList() {
        List<RoleOutGoingDTO> resultList = roleDTOMapper.map(
                List.of(
                        role,
                        role,
                        role
                )
        );

        Assertions.assertEquals(3, resultList.size());
    }
}
