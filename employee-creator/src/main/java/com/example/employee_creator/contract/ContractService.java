package com.example.employee_creator.contract;

import org.springframework.stereotype.Service;

import com.example.employee_creator.contract.dtos.CreateContractDto;
import com.example.employee_creator.contract.entities.Contract;
import com.example.employee_creator.department.DepartmentService;
import com.example.employee_creator.department.entities.Department;
import com.example.employee_creator.employee.EmployeeService;
import com.example.employee_creator.employee.entities.Employee;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class ContractService {
    private final ContractRepository repo;
    private final EmployeeService employeeService;
    private final DepartmentService departmentService;

    public ContractService(ContractRepository repo, EmployeeService employeeService,
            DepartmentService departmentService) {
        this.repo = repo;
        this.employeeService = employeeService;
        this.departmentService = departmentService;
    }

    public Contract createContract(CreateContractDto data) {
        Department department = this.departmentService.findById(data.departmentId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid department"));
        Employee employee = this.employeeService.findById(data.employeeId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid Employee"));

        this.repo.findTopByEmployeeOrderByStartDateDesc(employee).ifPresent(c -> {
            if (c.getEndDate() == null || c.getEndDate().isAfter(data.startDate())) {
                c.setEndDate(data.startDate().minusDays(1));
                c.setActive(false);
                this.repo.save(c);
            }
        });

        Contract newContract = new Contract();
        newContract.setEmployee(employee);
        newContract.setDepartment(department);
        newContract.setStartDate(data.startDate());
        newContract.setJobTitle(data.jobTitle());
        newContract.setSalary(data.salary());
        newContract.setActive(true);
        if (data.endDate() != null) {
            newContract.setEndDate(data.endDate());
        }
        this.repo.saveAndFlush(newContract);
        return newContract;
    }

    public long getCount() {
        return this.repo.count();
    }

}
