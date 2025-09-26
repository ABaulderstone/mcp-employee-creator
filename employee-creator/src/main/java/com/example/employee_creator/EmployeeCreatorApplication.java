package com.example.employee_creator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class EmployeeCreatorApplication {

	public static void main(String[] args) {
		SpringApplication.run(EmployeeCreatorApplication.class, args);
	}

}
