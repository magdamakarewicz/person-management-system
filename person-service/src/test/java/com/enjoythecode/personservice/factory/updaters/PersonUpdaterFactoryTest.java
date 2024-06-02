package com.enjoythecode.personservice.factory.updaters;

import com.enjoythecode.personservice.PersonServiceApplication;
import com.enjoythecode.personservice.api.DictionaryServiceClient;
import com.enjoythecode.personservice.command.UpdateEmployeeCommand;
import com.enjoythecode.personservice.command.UpdatePersonCommand;
import com.enjoythecode.personservice.dto.DictionaryValueSimpleDto;
import com.enjoythecode.personservice.factory.updater.EmployeeUpdater;
import com.enjoythecode.personservice.factory.updater.PersonUpdaterFactory;
import com.enjoythecode.personservice.factory.updater.RetireeUpdater;
import com.enjoythecode.personservice.factory.updater.StudentUpdater;
import com.enjoythecode.personservice.model.Employee;
import com.enjoythecode.personservice.repository.EmployeeRepository;
import com.enjoythecode.personservice.repository.RetireeRepository;
import com.enjoythecode.personservice.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = PersonServiceApplication.class)
@ActiveProfiles("test")
class PersonUpdaterFactoryTest {

    @MockBean
    private DictionaryServiceClient dictionaryServiceClient;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private RetireeRepository retireeRepository;

    private PersonUpdaterFactory updaterFactory;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        updaterFactory = new PersonUpdaterFactory(Set.of(
                new EmployeeUpdater(dictionaryServiceClient, employeeRepository, new ModelMapper()),
                new StudentUpdater(dictionaryServiceClient, studentRepository, new ModelMapper()),
                new RetireeUpdater(retireeRepository, new ModelMapper())
        ), dictionaryServiceClient);
    }

    @Test
    public void shouldUpdateEmployeeDataUsingPersonUpdaterFactory() {
        //given
        DictionaryValueSimpleDto employee = new DictionaryValueSimpleDto(1L, "employee");
        DictionaryValueSimpleDto director = new DictionaryValueSimpleDto(2L, "director");
        UpdatePersonCommand updateCommand = new UpdateEmployeeCommand(
                1L, employee.getName(), "John", "Doe", "12345678911", 180, 70,
                "johndoe@test.com", 0L, LocalDate.of(2021, 1, 1),
                director.getId(), 100000.00
        );
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(new Employee()));
        Mockito.when(dictionaryServiceClient
                .getDictionaryValueByDictionaryIdAndName(1L, employee.getName())).thenReturn(employee);
        Mockito.when(dictionaryServiceClient.getDictionaryValueById(director.getId())).thenReturn(director);
        //when
        Employee employeeForUpdate = (Employee) updaterFactory.update(updateCommand);
        //then
        assertNotNull(employeeForUpdate);
        assertEquals("John", employeeForUpdate.getFirstName());
        assertEquals("Doe", employeeForUpdate.getLastName());
        assertEquals("12345678911", employeeForUpdate.getPesel());
        assertEquals(180, employeeForUpdate.getHeight());
        assertEquals(70, employeeForUpdate.getWeight());
        assertEquals("johndoe@test.com", employeeForUpdate.getEmail());
        assertEquals(LocalDate.of(2021, 1, 1), employeeForUpdate.getEmploymentStartDate());
        assertEquals(director.getId(), employeeForUpdate.getCurrentPositionId());
        assertEquals(100000.00, employeeForUpdate.getCurrentSalary());
    }

}