package com.enjoythecode.personservice.factory.creatorsfromcsv;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import com.enjoythecode.personservice.PersonServiceApplication;
import com.enjoythecode.personservice.api.DictionaryServiceClient;
import com.enjoythecode.personservice.command.CreatePersonFromCsvCommand;
import com.enjoythecode.personservice.dto.DictionaryValueSimpleDto;
import com.enjoythecode.personservice.factory.cteatorfromcsv.EmployeeFromCsvCreator;
import com.enjoythecode.personservice.factory.cteatorfromcsv.PersonFromCsvFactory;
import com.enjoythecode.personservice.factory.cteatorfromcsv.RetireeFromCsvCreator;
import com.enjoythecode.personservice.factory.cteatorfromcsv.StudentFromCsvCreator;
import com.enjoythecode.personservice.model.Employee;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(classes = PersonServiceApplication.class)
@ActiveProfiles("test")
class PersonFromCsvFactoryTest {

    @MockBean
    private DictionaryServiceClient dictionaryServiceClient;

    private PersonFromCsvFactory personFromCsvFactory;

    @BeforeEach
    public void setUp() {
        personFromCsvFactory = new PersonFromCsvFactory(Set.of(
                new EmployeeFromCsvCreator(dictionaryServiceClient),
                new StudentFromCsvCreator(dictionaryServiceClient),
                new RetireeFromCsvCreator(dictionaryServiceClient)
        ));
    }

    @Test
    public void shouldCreateEmployeeFromProvidedDataUsingPersonFromCsvFactory() {
        //given
        DictionaryValueSimpleDto employee = new DictionaryValueSimpleDto(1L, "employee");
        DictionaryValueSimpleDto manager = new DictionaryValueSimpleDto(2L, "manager");
        String[] employeeData = {
                "employee", "Mia", "Smith", "78062890123", "167", "63", "miasmith@test.com", "2020-12-01",
                "manager", "95000"
        };
        Mockito.when(dictionaryServiceClient.getDictionaryValueByDictionaryIdAndName(1L, "employee"))
                .thenReturn(employee);
        Mockito.when(dictionaryServiceClient.getDictionaryValueByDictionaryIdAndName(2L, "manager"))
                .thenReturn(manager);
        CreatePersonFromCsvCommand command = new CreatePersonFromCsvCommand("employee", employeeData);
        //when
        Employee employeeFromCsv = (Employee) personFromCsvFactory.create(command);
        //then
        assertNotNull(employeeFromCsv);
        assertEquals("Mia", employeeFromCsv.getFirstName());
        assertEquals("Smith", employeeFromCsv.getLastName());
        assertEquals("78062890123", employeeFromCsv.getPesel());
        assertEquals(167, employeeFromCsv.getHeight());
        assertEquals(63, employeeFromCsv.getWeight());
        assertEquals("miasmith@test.com", employeeFromCsv.getEmail());
        assertEquals(LocalDate.of(2020, 12, 01), employeeFromCsv.getEmploymentStartDate());
        assertEquals(manager.getId(), employeeFromCsv.getCurrentPositionId());
        assertEquals(95000.00, employeeFromCsv.getCurrentSalary());
    }

}