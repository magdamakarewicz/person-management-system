package com.enjoythecode.personservice.factory.creator;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import com.enjoythecode.personservice.api.DictionaryServiceClient;
import com.enjoythecode.personservice.command.CreateEmployeeCommand;
import com.enjoythecode.personservice.command.CreatePersonCommand;
import com.enjoythecode.personservice.model.Employee;
import com.enjoythecode.personservice.model.Person;

@Service
@RequiredArgsConstructor
public class EmployeeCreator implements PersonCreator {

    private final DictionaryServiceClient dictionaryServiceClient;

    private final ModelMapper modelMapper;

    @Override
    public String getType() {
        return "employee";
    }

    @Override
    public Person createPerson(CreatePersonCommand createPersonCommand) {
        CreateEmployeeCommand employeeCommand = modelMapper.map(createPersonCommand, CreateEmployeeCommand.class);
        return new Employee(
                dictionaryServiceClient
                        .getDictionaryValueByDictionaryIdAndName(1L, createPersonCommand.getType()).getId(),
                employeeCommand.getFirstName(),
                employeeCommand.getLastName(),
                employeeCommand.getPesel(),
                employeeCommand.getHeight(),
                employeeCommand.getWeight(),
                employeeCommand.getEmail(),
                employeeCommand.getEmploymentStartDate(),
                dictionaryServiceClient.getDictionaryValueById(employeeCommand.getCurrentPositionId()).getId(),
                employeeCommand.getCurrentSalary()
        );
    }

}
