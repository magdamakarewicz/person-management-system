package com.enjoythecode.personservice.factory.cteatorfromcsv;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import com.enjoythecode.personservice.api.DictionaryServiceClient;
import com.enjoythecode.personservice.model.Employee;
import com.enjoythecode.personservice.model.Person;

import java.time.LocalDate;

@Service
@AllArgsConstructor
public class EmployeeFromCsvCreator implements PersonFromCsvCreator {

    private final DictionaryServiceClient dictionaryServiceClient;

    @Override
    public String getType() {
        return "employee";
    }

    @Override
    public Person createPerson(String[] parameters) {
        return new Employee(
                dictionaryServiceClient.getDictionaryValueByDictionaryIdAndName(
                        1L, parameters[0].toLowerCase()).getId(),
                parameters[1].trim(),
                parameters[2].trim(),
                parameters[3].trim(),
                Integer.parseInt(parameters[4].trim()),
                Integer.parseInt(parameters[5].trim()),
                parameters[6].trim(),
                LocalDate.parse(parameters[7].trim()),
                dictionaryServiceClient.getDictionaryValueByDictionaryIdAndName(
                        2L, parameters[8].toLowerCase()).getId(),
                Double.parseDouble(parameters[9].trim())
        );
    }

}

