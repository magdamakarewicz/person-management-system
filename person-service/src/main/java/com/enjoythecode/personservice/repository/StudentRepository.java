package com.enjoythecode.personservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.enjoythecode.personservice.model.Student;

public interface StudentRepository extends JpaRepository<Student, Long> {

}
