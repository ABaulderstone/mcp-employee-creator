package com.example.employee_creator.config.seeder;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.example.employee_creator.contract.ContractService;
import com.example.employee_creator.contract.dtos.CreateContractDto;
import com.example.employee_creator.department.DepartmentService;
import com.example.employee_creator.department.dtos.CreateDepartmentDto;
import com.example.employee_creator.department.entities.Department;
import com.example.employee_creator.employee.EmployeeService;
import com.example.employee_creator.employee.dtos.CreateEmployeeDto;
import com.example.employee_creator.employee.entities.Employee;
import com.github.javafaker.Faker;

@Component
@Profile("dev")
public class DevDataSeeder implements CommandLineRunner {
    private final Faker faker = new Faker(new Locale("en-AU"));
    private final EmployeeService employeeService;
    private final DepartmentService departmentService;
    private final ContractService contractService;

    public DevDataSeeder(EmployeeService employeeService, DepartmentService departmentService,
            ContractService contractService) {
        this.employeeService = employeeService;
        this.departmentService = departmentService;
        this.contractService = contractService;
    }

    @Override
    public void run(String... args) throws Exception {
        if (this.employeeService.getCount() > 0) {
            return;
        }

        List<Department> depts = seedDepartments();

        for (int i = 0; i < 300; i++) {
            String firstName = faker.name().firstName();
            String lastName = faker.name().lastName();
            LocalDate birthDate = faker.date().birthday(20, 60).toInstant().atZone(ZoneId.systemDefault())
                    .toLocalDate();
            CreateEmployeeDto dto = new CreateEmployeeDto(firstName, lastName, birthDate);
            Employee created = this.employeeService.create(dto);
            System.out.println("Seeded employee " + created.getEmail());
            int deptIdx = faker.number().numberBetween(0, depts.size());
            Department dept = depts.get(deptIdx);
            seedCareer(created, dept);
        }
    }

    private List<Department> seedDepartments() {
        if (departmentService.getCount() > 0) {
            return departmentService.findAll();
        }
        List<String> names = List.of("Engineering", "Sales", "HR");
        List<Department> depts = new ArrayList<>();

        for (String name : names) {
            Department dept = departmentService.createDepartment(new CreateDepartmentDto(
                    name,
                    name + " Department"));
            depts.add(dept);
        }
        return depts;
    }

    private void seedCareer(Employee e, Department dept) {
        LocalDate firstStart = generateFirstStart(e.getDateOfBirth());

        String[] titles = jobTitlesFor(dept);
        float baseSalary = baseSalaryFor(dept);

        int level = 0;

        CreateContractDto firstContract = new CreateContractDto(
                titles[level],
                baseSalary,
                firstStart,
                null,
                e.getId(),
                dept.getId());
        contractService.createContract(firstContract);

        LocalDate currentYear = firstStart.plusYears(1);
        int yearsInRole = 1;

        while (currentYear.isBefore(LocalDate.now()) && level < titles.length - 1) {
            boolean promoted = shouldPromote(level, yearsInRole);

            if (promoted) {
                level++;

                float multiplier = 1 + (faker.number().numberBetween(20, 31) / 100.0f);
                float newSalary = baseSalary * (float) Math.pow(multiplier, level);

                CreateContractDto dto = new CreateContractDto(
                        titles[level],
                        newSalary,
                        currentYear,
                        null,
                        e.getId(),
                        dept.getId());
                contractService.createContract(dto);

                yearsInRole = 0;
            }

            currentYear = currentYear.plusYears(1);
            yearsInRole++;
        }
    }

    private LocalDate generateFirstStart(LocalDate dateOfBirth) {
        LocalDate earliest = dateOfBirth.plusYears(18);
        LocalDate latest = LocalDate.now();
        long careerYears = ChronoUnit.YEARS.between(earliest, latest);
        int offsetYears = faker.number().numberBetween(0, (int) careerYears + 1);
        return latest.minusYears(offsetYears).withDayOfMonth(1);
    }

    private boolean shouldPromote(int level, int yearsInRole) {
        int chance = 10 + (yearsInRole - 1) * 10;
        chance = Math.max(chance - level * 10, 5);
        chance = Math.min(chance, 70);
        return faker.number().numberBetween(0, 100) < chance;
    }

    private String[] jobTitlesFor(Department dept) {
        return switch (dept.getName()) {
            case "Engineering" -> new String[] {
                    "Junior Developer", "Developer", "Senior Developer", "Lead Engineer", "Engineering Manager"
            };
            case "HR" -> new String[] {
                    "HR Assistant", "HR Specialist", "HR Manager", "Head of People"
            };
            case "Sales" -> new String[] {
                    "Sales Associate", "Account Executive", "Senior Account Exec", "Sales Manager", "Director of Sales"
            };
            default -> new String[] { "Employee" };
        };
    }

    private float baseSalaryFor(Department dept) {
        return switch (dept.getName()) {
            case "Engineering" -> 60000f;
            case "HR" -> 50000f;
            case "Sales" -> 45000f;
            default -> 40000f;
        };
    }

}
