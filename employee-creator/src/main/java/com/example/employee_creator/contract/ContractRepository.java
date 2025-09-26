package com.example.employee_creator.contract;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.employee_creator.contract.entities.Contract;
import com.example.employee_creator.employee.entities.Employee;

public interface ContractRepository extends JpaRepository<Contract, Long> {

    Optional<Contract> findTopByEmployeeOrderByStartDateDesc(Employee employee);
}
