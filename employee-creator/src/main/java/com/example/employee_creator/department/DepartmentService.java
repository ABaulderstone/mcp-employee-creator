package com.example.employee_creator.department;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.employee_creator.department.dtos.CreateDepartmentDto;
import com.example.employee_creator.department.entities.Department;

@Service
public class DepartmentService {
    private final DepartmentRepository repo;

    public DepartmentService(DepartmentRepository repo) {
        this.repo = repo;
    }

    public Department createDepartment(CreateDepartmentDto data) {
        Department newDepartment = new Department();
        newDepartment.setName(data.name());
        newDepartment.setDescription(data.description());
        return this.repo.saveAndFlush(newDepartment);
    }

    public Optional<Department> findById(Long id) {
        return this.repo.findById(id);
    }

    public long getCount() {
        return this.repo.count();
    }

    public List<Department> findAll() {
        return this.repo.findAll();
    }

}
