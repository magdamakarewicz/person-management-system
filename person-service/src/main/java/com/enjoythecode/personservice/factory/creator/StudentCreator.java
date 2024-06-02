package com.enjoythecode.personservice.factory.creator;

import com.enjoythecode.personservice.api.DictionaryServiceClient;
import com.enjoythecode.personservice.command.CreatePersonCommand;
import com.enjoythecode.personservice.command.CreateStudentCommand;
import com.enjoythecode.personservice.model.Person;
import com.enjoythecode.personservice.model.Student;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StudentCreator implements PersonCreator {

    private final DictionaryServiceClient dictionaryServiceClient;

    private final ModelMapper modelMapper;

    @Override
    public String getType() {
        return "student";
    }

    @Override
    public Person createPerson(CreatePersonCommand createPersonCommand) {
        CreateStudentCommand studentCommand = modelMapper.map(createPersonCommand, CreateStudentCommand.class);
        return new Student(
                dictionaryServiceClient
                        .getDictionaryValueByDictionaryIdAndName(1L, createPersonCommand.getType()).getId(),
                studentCommand.getFirstName(),
                studentCommand.getLastName(),
                studentCommand.getPesel(),
                studentCommand.getHeight(),
                studentCommand.getWeight(),
                studentCommand.getEmail(),
                dictionaryServiceClient.getDictionaryValueById(studentCommand.getUniversityNameId()).getId(),
                studentCommand.getEnrollmentYear(),
                dictionaryServiceClient.getDictionaryValueById(studentCommand.getFieldOfStudyId()).getId(),
                studentCommand.getScholarship()
        );
    }

}
