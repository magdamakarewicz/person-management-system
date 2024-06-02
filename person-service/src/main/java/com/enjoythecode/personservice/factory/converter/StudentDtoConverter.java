package com.enjoythecode.personservice.factory.converter;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import com.enjoythecode.personservice.api.DictionaryServiceClient;
import com.enjoythecode.personservice.dto.PersonDto;
import com.enjoythecode.personservice.dto.StudentDto;
import com.enjoythecode.personservice.model.Person;
import com.enjoythecode.personservice.model.Student;

@Service
@RequiredArgsConstructor
public class StudentDtoConverter implements PersonDtoConverter {

    private final ModelMapper modelMapper;

    private final DictionaryServiceClient dictionaryServiceClient;

    @Override
    public String getType() {
        return "student";
    }

    @Override
    public PersonDto convert(Person person) {
        StudentDto studentDto = new StudentDto();
        Student student = modelMapper.map(person, Student.class);
        studentDto.setId(student.getId());
        studentDto.setType(dictionaryServiceClient
                .getDictionaryValueById(student.getTypeId()).getName());
        studentDto.setFirstName(student.getFirstName());
        studentDto.setLastName(student.getLastName());
        studentDto.setEmail(student.getEmail());
        studentDto.setVersion(student.getVersion());
        studentDto.setUniversityName(dictionaryServiceClient
                .getDictionaryValueById(student.getUniversityNameId()).getName());
        studentDto.setEnrollmentYear(student.getEnrollmentYear());
        studentDto.setFieldOfStudy(dictionaryServiceClient
                .getDictionaryValueById(student.getFieldOfStudyId()).getName());
        studentDto.setScholarship(student.getScholarship());
        return studentDto;
    }

}