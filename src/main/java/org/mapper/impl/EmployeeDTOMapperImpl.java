package org.mapper.impl;

import org.dto.EmployeeIncomingDTO;
import org.dto.EmployeeOutGoingDTO;
import org.dto.EmployeeUpdateDTO;
import org.mapper.AccessLogDTOMapper;
import org.mapper.EmployeeDTOMapper;
import org.mapper.ProductDTOMapper;
import org.mapper.RoleDTOMapper;
import org.model.Employee;

import java.util.List;

public class EmployeeDTOMapperImpl implements EmployeeDTOMapper {
    private static final RoleDTOMapper roleDTOMapper = RoleDTOMapperImpl.getInstance();
    private static final AccessLogDTOMapper accessLogDTOMapper = AccessLogDTOMapperImpl.getInstance();
    private static final ProductDTOMapper productDTOMapper = ProductDTOMapperImpl.getInstance();


    private static EmployeeDTOMapper instance;

    private EmployeeDTOMapperImpl() {
    }

    public static synchronized EmployeeDTOMapper getInstance() {
        if (instance == null) {
            instance = new EmployeeDTOMapperImpl();
        }
        return instance;
    }

    @Override
    public Employee map(EmployeeIncomingDTO employeeDTO) {
        return new Employee(
                null,
                employeeDTO.getFirstName(),
                employeeDTO.getLastName(),
                employeeDTO.getRole(),
                null,
                null
        );
    }

    @Override
    public Employee map(EmployeeUpdateDTO employeeDTO) {
        return new Employee(
                employeeDTO.getId(),
                employeeDTO.getFirstName(),
                employeeDTO.getLastName(),
                roleDTOMapper.map(employeeDTO.getRole()),
                accessLogDTOMapper.mapUpdateList(employeeDTO.getAccessLogList()),
                productDTOMapper.mapUpdateList(employeeDTO.getProductList())
        );
    }

    @Override
    public EmployeeOutGoingDTO map(Employee employee) {
        return new EmployeeOutGoingDTO(
                employee.getId(),
                employee.getFirstName(),
                employee.getLastName(),
                roleDTOMapper.map(employee.getRole()),
                accessLogDTOMapper.map(employee.getAccessLogList()),
                productDTOMapper.map(employee.getProductList())
        );
    }

    @Override
    public List<EmployeeOutGoingDTO> map(List<Employee> employeeList) {
        return employeeList.stream().map(this::map).toList();
    }
}
