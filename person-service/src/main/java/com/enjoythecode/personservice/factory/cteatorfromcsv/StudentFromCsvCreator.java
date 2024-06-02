package com.enjoythecode.personservice.factory.cteatorfromcsv;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.enjoythecode.personservice.api.DictionaryServiceClient;
import com.enjoythecode.personservice.model.Person;
import com.enjoythecode.personservice.model.Student;

@Service
@RequiredArgsConstructor
public class StudentFromCsvCreator implements PersonFromCsvCreator {

    private final DictionaryServiceClient dictionaryServiceClient;

    @Override
    public String getType() {
        return "student";
    }

    @Override
    public Person createPerson(String[] parameters) {
        return new Student(
                dictionaryServiceClient.getDictionaryValueByDictionaryIdAndName(
                        1L, parameters[0].toLowerCase()).getId(),
                parameters[1].trim(),
                parameters[2].trim(),
                parameters[3].trim(),
                Integer.parseInt(parameters[4].trim()),
                Integer.parseInt(parameters[5].trim()),
                parameters[6].trim(),
                dictionaryServiceClient.getDictionaryValueByDictionaryIdAndName(
                        3L, parameters[7].toLowerCase()).getId(),
                Integer.parseInt(parameters[8].trim().toLowerCase()),
                dictionaryServiceClient.getDictionaryValueByDictionaryIdAndName(
                        4L, parameters[9].toLowerCase()).getId(),
                Double.parseDouble(parameters[10].trim())
        );
    }

}