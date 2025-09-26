package com.example.employee_creator.employee;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.employee_creator.employee.entities.Employee;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    Optional<Employee> findByEmail(String email);

    List<Employee> findAllByEmailStartingWith(String base);
}
