package com.enjoythecode.personservice.factory.updater;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import com.enjoythecode.personservice.api.DictionaryServiceClient;
import com.enjoythecode.personservice.command.UpdateEmployeeCommand;
import com.enjoythecode.personservice.command.UpdatePersonCommand;
import com.enjoythecode.personservice.exception.InvalidTypeException;
import com.enjoythecode.personservice.model.Employee;
import com.enjoythecode.personservice.model.Person;
import com.enjoythecode.personservice.repository.EmployeeRepository;

@Service
@RequiredArgsConstructor
public class EmployeeUpdater implements PersonUpdater {

    private final DictionaryServiceClient dictionaryServiceClient;

    private final EmployeeRepository employeeRepository;

    private final ModelMapper modelMapper;

    @Override
    public String getType() {
        return "employee";
    }

    @Override
    public Person updatePerson(UpdatePersonCommand updatePersonCommand) {
        try {
            UpdateEmployeeCommand employeeCommand = modelMapper.map(updatePersonCommand, UpdateEmployeeCommand.class);
            Employee employeeForUpdate = employeeRepository.findById(employeeCommand.getId())
                    .orElseThrow(() -> new EntityNotFoundException("No entity found"));
            employeeForUpdate.setFirstName(employeeCommand.getFirstName());
            employeeForUpdate.setLastName(employeeCommand.getLastName());
            employeeForUpdate.setPesel(employeeCommand.getPesel());
            employeeForUpdate.setHeight(employeeCommand.getHeight());
            employeeForUpdate.setWeight(employeeCommand.getWeight());
            employeeForUpdate.setEmail(employeeCommand.getEmail());
            employeeForUpdate.setVersion(employeeCommand.getVersion());
            employeeForUpdate.setEmploymentStartDate(employeeCommand.getEmploymentStartDate());
            employeeForUpdate.setCurrentPositionId(dictionaryServiceClient
                    .getDictionaryValueById(employeeCommand.getCurrentPositionId()).getId());
            employeeForUpdate.setCurrentSalary(employeeCommand.getCurrentSalary());
            return employeeForUpdate;
        } catch (ClassCastException e) {
            throw new InvalidTypeException("The type in the request body does not match the entity type");
        }
    }

}
