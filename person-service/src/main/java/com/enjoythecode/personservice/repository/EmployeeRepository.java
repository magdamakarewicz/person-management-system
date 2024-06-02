package com.enjoythecode.personservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.enjoythecode.personservice.model.Employee;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

}
