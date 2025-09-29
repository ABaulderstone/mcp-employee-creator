package com.example.employee_creator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@SpringBootApplication
@EnableJpaAuditing
@OpenAPIDefinition(info = @Info(title = "Employee Service API", version = "1.0", description = "API for managing employees and contracts"))
public class EmployeeCreatorApplication {

	public static void main(String[] args) {
		SpringApplication.run(EmployeeCreatorApplication.class, args);
	}

}
