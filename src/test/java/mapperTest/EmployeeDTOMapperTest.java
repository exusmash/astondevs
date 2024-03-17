package mapperTest;

import org.dto.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapper.EmployeeDTOMapper;
import org.mapper.impl.EmployeeDTOMapperImpl;
import org.model.AccessLog;
import org.model.Employee;
import org.model.Product;
import org.model.Role;

import java.util.List;

public class EmployeeDTOMapperTest {
    private EmployeeDTOMapper employeeDTOMapper;

    @BeforeEach
    void setUp() {
        employeeDTOMapper = EmployeeDTOMapperImpl.getInstance();
    }

    @DisplayName("Employee map(EmployeeIncomingDTO")
    @Test
    void mapIncoming() {
        EmployeeIncomingDTO dto = new EmployeeIncomingDTO(
                "f1",
                "l2",
                new Role(1L, "role1")
        );
        Employee result = employeeDTOMapper.map(dto);

        Assertions.assertNull(result.getId());
        Assertions.assertEquals(dto.getFirstName(), result.getFirstName());
        Assertions.assertEquals(dto.getLastName(), result.getLastName());
        Assertions.assertEquals(dto.getRole().getId(), result.getRole().getId());
    }

    @DisplayName("Employee map(EmployeeUpdateDTO")
    @Test
    void testMapUpdate() {
        EmployeeUpdateDTO dto = new EmployeeUpdateDTO(
                100L,
                "f1",
                "l2",
                new RoleUpdateDTO(1L, "Role update"),
                List.of(new AccessLogUpdateDTO()),
                List.of(new ProductUpdateDTO())
        );
        Employee result = employeeDTOMapper.map(dto);

        Assertions.assertEquals(dto.getId(), result.getId());
        Assertions.assertEquals(dto.getFirstName(), result.getFirstName());
        Assertions.assertEquals(dto.getLastName(), result.getLastName());
        Assertions.assertEquals(dto.getRole().getId(), result.getRole().getId());
        Assertions.assertEquals(dto.getAccessLogList().size(), result.getAccessLogList().size());
        Assertions.assertEquals(dto.getProductList().size(), result.getProductList().size());
    }

    @DisplayName("EmployeeOutGoingDTO map(Employee")
    @Test
    void testMapOutgoing() {
        Employee employee = new Employee(
                100L,
                "f1",
                "l2",
                new Role(1L, "Role #1"),
                List.of(new AccessLog(1L, "desc", null)),
                List.of(new Product(1L, "p1", List.of()))
        );
        EmployeeOutGoingDTO result = employeeDTOMapper.map(employee);

        Assertions.assertEquals(employee.getId(), result.getId());
        Assertions.assertEquals(employee.getFirstName(), result.getFirstName());
        Assertions.assertEquals(employee.getLastName(), result.getLastName());
        Assertions.assertEquals(employee.getRole().getId(), result.getRole().getId());
        Assertions.assertEquals(employee.getAccessLogList().size(), result.getAccessLogList().size());
        Assertions.assertEquals(employee.getProductList().size(), result.getProductList().size());
    }

    @DisplayName("List<EmployeeOutGoingDTO> map(List<Employee>")
    @Test
    void testMapList() {
        List<Employee> userList = List.of(
                new Employee(
                        100L,
                        "f1",
                        "l2",
                        new Role(1L, "Role #1"),
                        List.of(new AccessLog(1L, "desc", null)),
                        List.of(new Product(1L, "p1", List.of()))
                ),
                new Employee(
                        101L,
                        "f3",
                        "l4",
                        new Role(1L, "Role #1"),
                        List.of(new AccessLog(2L, "descc", null)),
                        List.of(new Product(2L, "p2", List.of()))
                )
        );
        int result = employeeDTOMapper.map(userList).size();
        Assertions.assertEquals(userList.size(), result);
    }
}
