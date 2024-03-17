package org.service;

import org.dto.EmployeeIncomingDTO;
import org.dto.EmployeeOutGoingDTO;
import org.dto.EmployeeUpdateDTO;
import org.exeption.NotFoundException;

import java.util.List;

public interface EmployeeService {
    EmployeeOutGoingDTO save(EmployeeIncomingDTO employeeDto);

    void update(EmployeeUpdateDTO employeeDto) throws NotFoundException;

    EmployeeOutGoingDTO findById(Long employeeId) throws NotFoundException;

    List<EmployeeOutGoingDTO> findAll();

    void delete(Long employeeId) throws NotFoundException;
}
