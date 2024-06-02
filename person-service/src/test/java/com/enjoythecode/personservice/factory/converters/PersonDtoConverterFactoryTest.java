package com.enjoythecode.personservice.factory.converters;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import com.enjoythecode.personservice.PersonServiceApplication;
import com.enjoythecode.personservice.api.DictionaryServiceClient;
import com.enjoythecode.personservice.dto.DictionaryValueSimpleDto;
import com.enjoythecode.personservice.dto.EmployeeDto;
import com.enjoythecode.personservice.factory.converter.EmployeeDtoConverter;
import com.enjoythecode.personservice.factory.converter.PersonDtoConverterFactory;
import com.enjoythecode.personservice.factory.converter.RetireeDtoConverter;
import com.enjoythecode.personservice.factory.converter.StudentDtoConverter;
import com.enjoythecode.personservice.model.Employee;
import com.enjoythecode.personservice.model.Person;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = PersonServiceApplication.class)
@ActiveProfiles("test")
class PersonDtoConverterFactoryTest {

    @Mock
    private ModelMapper modelMapper;

    @MockBean
    private DictionaryServiceClient dictionaryServiceClient;

    private PersonDtoConverterFactory dtoConverterFactory;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        dtoConverterFactory = new PersonDtoConverterFactory(Set.of(
                new EmployeeDtoConverter(modelMapper, dictionaryServiceClient),
                new StudentDtoConverter(modelMapper, dictionaryServiceClient),
                new RetireeDtoConverter(modelMapper, dictionaryServiceClient)
        ), dictionaryServiceClient);
    }

    @Test
    public void shouldConvertEmployeeToEmployeeDtoUsingPersonDtoConverterFactory() {
        // given
        DictionaryValueSimpleDto employee = new DictionaryValueSimpleDto(1L, "employee");
        DictionaryValueSimpleDto manager = new DictionaryValueSimpleDto(2L, "manager");
        Person expectedEmployee = new Employee(
                employee.getId(), "John", "Doe", "12345678911", 180, 70,
                "johndoe@test.com", LocalDate.of(2021, 1, 1), manager.getId(),
                40000.00
        );
        Mockito.when(dictionaryServiceClient.getDictionaryValueById(employee.getId())).thenReturn(employee);
        Mockito.when(dictionaryServiceClient.getDictionaryValueById(manager.getId())).thenReturn(manager);
        Mockito.doReturn(expectedEmployee).when(modelMapper).map(Mockito.any(), Mockito.eq(Employee.class));
        // when
        EmployeeDto employeeDto = (EmployeeDto) dtoConverterFactory.convert(expectedEmployee);
        // then
        assertEquals("employee", employeeDto.getType());
        assertEquals("John", employeeDto.getFirstName());
        assertEquals("Doe", employeeDto.getLastName());
        assertEquals("johndoe@test.com", employeeDto.getEmail());
        assertEquals(LocalDate.of(2021, 1, 1), employeeDto.getEmploymentStartDate());
        assertEquals("manager", employeeDto.getCurrentPosition());
        assertEquals(40000.00, employeeDto.getCurrentSalary());
    }

}