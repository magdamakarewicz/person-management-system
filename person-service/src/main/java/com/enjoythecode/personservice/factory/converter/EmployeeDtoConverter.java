package com.enjoythecode.personservice.factory.converter;

import com.enjoythecode.personservice.api.DictionaryServiceClient;
import com.enjoythecode.personservice.dto.EmployeeDto;
import com.enjoythecode.personservice.dto.PersonDto;
import com.enjoythecode.personservice.model.Employee;
import com.enjoythecode.personservice.model.Person;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmployeeDtoConverter implements PersonDtoConverter {

    private final ModelMapper modelMapper;

    private final DictionaryServiceClient dictionaryServiceClient;

    @Override
    public String getType() {
        return "employee";
    }

    @Override
    public PersonDto convert(Person person) {
        EmployeeDto employeeDto = new EmployeeDto();
        Employee employee = modelMapper.map(person, Employee.class);
        employeeDto.setId(employee.getId());
        employeeDto.setType(dictionaryServiceClient
                .getDictionaryValueById(employee.getTypeId()).getName());
        employeeDto.setFirstName(employee.getFirstName());
        employeeDto.setLastName(employee.getLastName());
        employeeDto.setEmail(employee.getEmail());
        employeeDto.setVersion(employee.getVersion());
        employeeDto.setEmploymentStartDate(employee.getEmploymentStartDate());
        employeeDto.setCurrentPosition(dictionaryServiceClient
                .getDictionaryValueById(employee.getCurrentPositionId()).getName());
        employeeDto.setCurrentSalary(employee.getCurrentSalary());
        return employeeDto;
    }

}
