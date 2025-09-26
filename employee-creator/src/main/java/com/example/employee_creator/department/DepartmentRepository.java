package com.example.employee_creator.department;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.employee_creator.department.entities.Department;

public interface DepartmentRepository extends JpaRepository<Department, Long> {

}
