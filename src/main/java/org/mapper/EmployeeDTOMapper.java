package org.mapper;

import org.dto.EmployeeIncomingDTO;
import org.dto.EmployeeOutGoingDTO;
import org.dto.EmployeeUpdateDTO;
import org.model.Employee;

import java.util.List;

public interface EmployeeDTOMapper {
    Employee map(EmployeeIncomingDTO employeeIncomingDTO);

    Employee map(EmployeeUpdateDTO employeeUpdateDTO);

    EmployeeOutGoingDTO map(Employee employee);

    List<EmployeeOutGoingDTO> map(List<Employee> employeeList);
}
