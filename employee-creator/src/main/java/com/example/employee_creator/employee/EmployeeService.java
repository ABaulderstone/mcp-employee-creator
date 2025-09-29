package com.example.employee_creator.employee;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.example.employee_creator.employee.dtos.CreateEmployeeDto;
import com.example.employee_creator.employee.dtos.EmployeeSearchFilterDto;
import com.example.employee_creator.employee.entities.Employee;

@Service
public class EmployeeService {
    private final EmployeeRepository repo;

    public EmployeeService(EmployeeRepository repo) {
        this.repo = repo;
    }

    public Optional<Employee> findById(Long id) {
        return this.repo.findById(id);
    }

    public Employee create(CreateEmployeeDto data) {
        Employee employee = new Employee();
        String firstName = data.firstName().trim();
        String lastName = data.lastName().trim();

        employee.setFirstName(firstName);
        employee.setLastName(lastName);
        employee.setDateOfBirth(data.dateOfBirth());
        String email = generateEmail(firstName, lastName);
        employee.setEmail(email);
        return this.repo.saveAndFlush(employee);
    }

    public long getCount() {
        return this.repo.count();
    }

    public Page<Employee> findEmployees(PageRequest pageable, EmployeeSearchFilterDto filter) {
        Specification<Employee> spec = Specification.unrestricted();

        if (filter.department() != null) {
            spec = spec.and(EmployeeSpecifications.departmentEquals(filter.department()));
        }

        if ("name".equalsIgnoreCase(filter.searchBy()) && filter.searchTerm() != null) {
            spec = spec.and(EmployeeSpecifications.fullNameContains(filter.searchTerm()));
        }

        if ("jobTitle".equalsIgnoreCase(filter.searchBy()) && filter.searchTerm() != null) {
            spec = spec.and(EmployeeSpecifications.jobTitleSearch(filter.searchTerm()));
        }

        return this.repo.findAll(spec, pageable);
    }

    private String generateEmail(String firstName, String lastName) {
        String domain = "@example.com";
        String base = firstName.toLowerCase() + "." + lastName.toLowerCase();
        String email = base + domain;
        List<Employee> existingEmployees = this.repo.findAllByEmailStartingWith(base);
        Set<String> existingEmails = existingEmployees.stream().map(e -> e.getEmail()).collect(Collectors.toSet());
        if (!existingEmails.contains(email)) {
            return email;
        }

        int suffix = 2;
        while (existingEmails.contains(base + suffix + domain)) {
            suffix++;
        }
        return base + suffix + domain;

    }
}
