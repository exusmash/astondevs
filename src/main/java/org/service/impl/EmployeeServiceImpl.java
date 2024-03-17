package org.service.impl;

import org.dto.EmployeeIncomingDTO;
import org.dto.EmployeeOutGoingDTO;
import org.dto.EmployeeUpdateDTO;
import org.exeption.NotFoundException;
import org.mapper.EmployeeDTOMapper;
import org.mapper.impl.EmployeeDTOMapperImpl;
import org.model.Employee;
import org.repository.EmployeeRepository;
import org.repository.impl.EmployeeRepositoryImpl;
import org.service.EmployeeService;

import java.util.List;

public class EmployeeServiceImpl implements EmployeeService {
    private final EmployeeRepository employeeRepository = EmployeeRepositoryImpl.getInstance();
    private static final EmployeeDTOMapper EmployeeDTOMapper = EmployeeDTOMapperImpl.getInstance();
    private static EmployeeService instance;


    private EmployeeServiceImpl() {
    }

    public static synchronized EmployeeService getInstance() {
        if (instance == null) {
            instance = new EmployeeServiceImpl();
        }
        return instance;
    }

    private void checkExistEmployee(Long employeeId) throws NotFoundException {
        if (!employeeRepository.exitsById(employeeId)) {
            throw new NotFoundException("Employee not found.");
        }
    }

    @Override
    public EmployeeOutGoingDTO save(EmployeeIncomingDTO employeeDto) {
        Employee employee = employeeRepository.save(EmployeeDTOMapper.map(employeeDto));
        return EmployeeDTOMapper.map(employeeRepository.findById(employee.getId()).orElse(employee));
    }

    @Override
    public void update(EmployeeUpdateDTO employeeDto) throws NotFoundException {
        if (employeeDto == null || employeeDto.getId() == null) {
            throw new IllegalArgumentException();
        }
        checkExistEmployee(employeeDto.getId());
        employeeRepository.update(EmployeeDTOMapper.map(employeeDto));
    }

    @Override
    public EmployeeOutGoingDTO findById(Long employeeId) throws NotFoundException {
        checkExistEmployee(employeeId);
        Employee employee = employeeRepository.findById(employeeId).orElseThrow();
        return EmployeeDTOMapper.map(employee);
    }

    @Override
    public List<EmployeeOutGoingDTO> findAll() {
        List<Employee> all = employeeRepository.findAll();
        return EmployeeDTOMapper.map(all);
    }

    @Override
    public void delete(Long employeeId) throws NotFoundException {
        checkExistEmployee(employeeId);
        employeeRepository.deleteById(employeeId);
    }
}
