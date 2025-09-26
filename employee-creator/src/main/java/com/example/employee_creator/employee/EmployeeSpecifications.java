package com.example.employee_creator.employee;

import org.springframework.data.jpa.domain.Specification;

import com.example.employee_creator.department.entities.Department;
import com.example.employee_creator.employee.entities.Employee;

import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;

import com.example.employee_creator.contract.entities.Contract;

public class EmployeeSpecifications {

    public static Specification<Employee> fullNameContains(String searchTerm) {
        return (root, query, cb) -> {
            if (searchTerm == null || searchTerm.isBlank()) {
                return cb.conjunction();
            }

            String[] parts = searchTerm.trim().split("\\s+");

            Expression<String> fullName = cb.concat(root.get("firstName"), cb.literal(" "));
            fullName = cb.concat(fullName, root.get("lastName"));

            String term = "%" + searchTerm.toLowerCase() + "%";

            if (parts.length >= 2) {
                return cb.like(cb.lower(fullName), term);
            }

            return cb.or(
                    cb.like(cb.lower(root.get("firstName")), term),
                    cb.like(cb.lower(root.get("lastName")), term),
                    cb.like(cb.lower(fullName), term));
        };
    }

    public static Specification<Employee> departmentEquals(String departmentName) {
        if (departmentName == null || departmentName.isBlank()) {
            return null;
        }

        return (root, query, cb) -> {
            Join<Employee, Contract> contract = root.join("contracts");
            Join<Contract, Department> department = contract.join("department");

            return cb.and(
                    cb.equal(cb.lower(department.get("name")), departmentName.toLowerCase()),
                    cb.isTrue(contract.get("isActive")));
        };
    }

    public static Specification<Employee> jobTitleSearch(String searchTerm) {
        return (root, query, cb) -> {
            if (searchTerm == null || searchTerm.isBlank()) {
                return null;
            }

            query.distinct(true);
            String term = "%" + searchTerm.toLowerCase() + "%";

            Join<Employee, Contract> contract = root.join("contracts");
            return cb.and(
                    cb.like(cb.lower(contract.get("jobTitle")), term),
                    cb.equal(contract.get("isActive"), true));
        };
    }
}
