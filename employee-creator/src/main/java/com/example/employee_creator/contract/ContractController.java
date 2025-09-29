package com.example.employee_creator.contract;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.employee_creator.contract.dtos.ContractDto;
import com.example.employee_creator.contract.dtos.CreateContractDto;
import com.example.employee_creator.contract.dtos.UpdateContractDto;
import com.example.employee_creator.contract.entities.Contract;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/contracts")
@Tag(name = "Contracts", description = "Contract management endpoints")
public class ContractController {

    private final ContractService contractService;

    public ContractController(ContractService contractService) {
        this.contractService = contractService;
    }

    @PostMapping()
    public ResponseEntity<ContractDto> postMethodName(@Valid @RequestBody CreateContractDto data) {
        Contract createdContract = this.contractService.createContract(data);
        ContractDto dto = ContractDto.fromEntity(createdContract);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);

    }

    @PatchMapping("/{id}")
    public ResponseEntity<ContractDto> updateContractById(@PathVariable Long id, UpdateContractDto data)
            throws BadRequestException {
        Contract updatedContract = this.contractService.updateById(id, data)
                .orElseThrow(() -> new BadRequestException("Could not find contract with id " + id));
        return ResponseEntity.ok(ContractDto.fromEntity(updatedContract));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteContractById(@PathVariable Long id) throws BadRequestException {
        boolean isDeleted = this.contractService.deleteById(id);
        if (!isDeleted) {
            throw new BadRequestException("Could not find contract with id " + id);
        }

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
