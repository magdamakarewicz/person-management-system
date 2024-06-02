package com.enjoythecode.personservice.factory.updater;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import com.enjoythecode.personservice.api.DictionaryServiceClient;
import com.enjoythecode.personservice.command.UpdatePersonCommand;
import com.enjoythecode.personservice.command.UpdateStudentCommand;
import com.enjoythecode.personservice.exception.InvalidTypeException;
import com.enjoythecode.personservice.model.Person;
import com.enjoythecode.personservice.model.Student;
import com.enjoythecode.personservice.repository.StudentRepository;

@Service
@RequiredArgsConstructor
public class StudentUpdater implements PersonUpdater {

    private final DictionaryServiceClient dictionaryServiceClient;

    private final StudentRepository studentRepository;

    private final ModelMapper modelMapper;

    @Override
    public String getType() {
        return "student";
    }

    @Override
    public Person updatePerson(UpdatePersonCommand updatePersonCommand) {
        try {
            UpdateStudentCommand studentCommand = modelMapper.map(updatePersonCommand, UpdateStudentCommand.class);
            Student studentForUpdate = studentRepository.findById(studentCommand.getId())
                    .orElseThrow(() -> new EntityNotFoundException("No entity found"));
            studentForUpdate.setFirstName(studentCommand.getFirstName());
            studentForUpdate.setLastName(studentCommand.getLastName());
            studentForUpdate.setPesel(studentCommand.getPesel());
            studentForUpdate.setHeight(studentCommand.getHeight());
            studentForUpdate.setWeight(studentCommand.getWeight());
            studentForUpdate.setEmail(studentCommand.getEmail());
            studentForUpdate.setVersion(studentCommand.getVersion());
            studentForUpdate.setUniversityNameId(dictionaryServiceClient
                    .getDictionaryValueById(studentCommand.getUniversityNameId()).getId());
            studentForUpdate.setEnrollmentYear(studentCommand.getEnrollmentYear());
            studentForUpdate.setFieldOfStudyId(dictionaryServiceClient
                    .getDictionaryValueById(studentCommand.getFieldOfStudyId()).getId());
            studentForUpdate.setScholarship(studentCommand.getScholarship());
            return studentForUpdate;
        } catch (ClassCastException e) {
            throw new InvalidTypeException("The type in the request body does not match the entity type");
        }
    }

}