package com.enjoythecode.personservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.enjoythecode.personservice.exception.InvalidIdException;
import com.enjoythecode.personservice.exception.PersonNotFoundException;
import com.enjoythecode.personservice.model.Employee;
import com.enjoythecode.personservice.repository.EmployeeRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    public Employee getById(Long id) {
        return employeeRepository.findById(
                Optional.ofNullable(id)
                        .orElseThrow(() -> new InvalidIdException("Wrong id."))
        ).orElseThrow(() -> new PersonNotFoundException("Employee with id " + id + " not found."));
    }

}
