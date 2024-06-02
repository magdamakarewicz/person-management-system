package com.enjoythecode.personservice.controllers;

import com.enjoythecode.personservice.PersonServiceApplication;
import com.enjoythecode.personservice.api.DictionaryServiceClient;
import com.enjoythecode.personservice.command.CreateEmployeeCommand;
import com.enjoythecode.personservice.command.CreatePersonCommand;
import com.enjoythecode.personservice.command.UpdateEmployeeCommand;
import com.enjoythecode.personservice.command.UpdatePersonCommand;
import com.enjoythecode.personservice.dto.DictionarySimpleDto;
import com.enjoythecode.personservice.dto.DictionaryValueSimpleDto;
import com.enjoythecode.personservice.model.Employee;
import com.enjoythecode.personservice.model.Student;
import com.enjoythecode.personservice.repository.PersonRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest(classes = PersonServiceApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PersonControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PersonRepository personRepository;

    @MockBean
    private DictionaryServiceClient dictionaryServiceClient;

    @BeforeEach
    public void setUp() {
        personRepository.deleteAllInBatch();
    }

    @Test
    public void shouldGetPersonByFirstName() throws Exception {
        //given
        DictionaryValueSimpleDto employee = new DictionaryValueSimpleDto(1L, "employee");
        DictionaryValueSimpleDto manager = new DictionaryValueSimpleDto(2L, "manager");
        Mockito.when(dictionaryServiceClient.getDictionaryValueById(employee.getId())).thenReturn(employee);
        Mockito.when(dictionaryServiceClient.getDictionaryValueById(manager.getId())).thenReturn(manager);
        personRepository.saveAndFlush(
                new Employee(employee.getId(), "John", "Doe", "12345678911", 180, 70,
                        "john.doe@test.com", LocalDate.of(2021, 1, 1), manager.getId(),
                        40000.00));
        //when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .get("/api/people?firstName=John"));
        //then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].firstName", is("John")));
    }

    @Test
    public void shouldGetAllPeopleByEmployeeType() throws Exception {
        //given
        DictionaryValueSimpleDto employee = new DictionaryValueSimpleDto(1L, "employee");
        DictionaryValueSimpleDto manager = new DictionaryValueSimpleDto(2L, "manager");
        Mockito.when(dictionaryServiceClient.getDictionaryValueById(employee.getId())).thenReturn(employee);
        Mockito.when(dictionaryServiceClient.getDictionaryValueById(manager.getId())).thenReturn(manager);
        personRepository.saveAndFlush(
                new Employee(employee.getId(), "John", "Doe", "12345678911", 180, 70,
                        "john.doe@test.com", LocalDate.of(2021, 1, 1), manager.getId(),
                        40000.00)
        );
        personRepository.saveAndFlush(
                new Employee(employee.getId(), "Adam", "Wick", "12345678912", 170, 80,
                        "adam.wick@test.com", LocalDate.of(2021, 2, 2), manager.getId(),
                        50000.00));
        //when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .get("/api/people?typeId=" + employee.getId()));
        //then
        resultActions
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        [
                          {
                            "type": "employee",
                            "firstName": "John",
                            "lastName": "Doe",
                            "email": "john.doe@test.com",
                            "version": 0,
                            "employmentStartDate": "2021-01-01",
                            "currentPosition": "manager",
                            "currentSalary": 40000.00
                          },
                          {
                            "type": "employee",
                            "firstName": "Adam",
                            "lastName": "Wick",
                            "email": "adam.wick@test.com",
                            "version": 0,
                            "employmentStartDate": "2021-02-02",
                            "currentPosition": "manager",
                            "currentSalary": 50000.00
                          }
                        ]
                        """));
    }

    @Test
    public void shouldGetSinglePersonByEmployeeTypeAndName() throws Exception {
        //given
        DictionaryValueSimpleDto employee = new DictionaryValueSimpleDto(1L, "employee");
        DictionaryValueSimpleDto manager = new DictionaryValueSimpleDto(2L, "manager");
        DictionaryValueSimpleDto student = new DictionaryValueSimpleDto(3L, "student");
        DictionaryValueSimpleDto university = new DictionaryValueSimpleDto(4L, "test university");
        DictionaryValueSimpleDto fieldOfStudy = new DictionaryValueSimpleDto(5L, "test field of study");
        Mockito.when(dictionaryServiceClient.getDictionaryValueById(employee.getId())).thenReturn(employee);
        Mockito.when(dictionaryServiceClient.getDictionaryValueById(manager.getId())).thenReturn(manager);
        Mockito.when(dictionaryServiceClient.getDictionaryValueById(student.getId())).thenReturn(student);
        Mockito.when(dictionaryServiceClient.getDictionaryValueById(university.getId())).thenReturn(university);
        Mockito.when(dictionaryServiceClient.getDictionaryValueById(fieldOfStudy.getId())).thenReturn(fieldOfStudy);
        personRepository.saveAndFlush(
                new Employee(employee.getId(), "John", "Doe", "12345678911", 180, 70,
                        "john.doe@test.com", LocalDate.of(2021, 1, 1), manager.getId(),
                        40000.00)
        );
        personRepository.saveAndFlush(
                new Student(student.getId(), "John", "Doe", "12345678912", 180, 70,
                        "john.doe@test.com", university.getId(), 2, fieldOfStudy.getId(),
                        5000.00)
        );
        //when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .get("/api/people?firstName=John&typeId=" + employee.getId()));
        //then
        resultActions
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        [
                          {
                            "type": "employee",
                            "firstName": "John",
                            "lastName": "Doe",
                            "email": "john.doe@test.com",
                            "version": 0,
                            "employmentStartDate": "2021-01-01",
                            "currentPosition": "manager",
                            "currentSalary": 40000.00
                          }
                        ]
                        """));
    }

    @Test
    public void shouldGetSinglePersonByLastNameAndWeightRange() throws Exception {
        //given
        DictionaryValueSimpleDto employee = new DictionaryValueSimpleDto(1L, "employee");
        DictionaryValueSimpleDto manager = new DictionaryValueSimpleDto(2L, "manager");
        Mockito.when(dictionaryServiceClient.getDictionaryValueById(employee.getId())).thenReturn(employee);
        Mockito.when(dictionaryServiceClient.getDictionaryValueById(manager.getId())).thenReturn(manager);
        personRepository.saveAndFlush(
                new Employee(employee.getId(), "John", "Doe", "12345678911", 180, 70,
                        "john.doe@test.com", LocalDate.of(2021, 1, 1), manager.getId(),
                        40000.00)
        );
        personRepository.saveAndFlush(
                new Employee(employee.getId(), "Tom", "Doe", "12345678912", 170, 60,
                        "tom.doe@test.com", LocalDate.of(2021, 1, 1), manager.getId(),
                        50000.00)
        );
        //when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .get("/api/people?lastName=Doe&weight=from55,to65"));
        //then
        resultActions
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        [
                          {
                            "type": "employee",
                            "firstName": "Tom",
                            "lastName": "Doe",
                            "email": "tom.doe@test.com",
                            "version": 0,
                            "employmentStartDate": "2021-01-01",
                            "currentPosition": "manager",
                            "currentSalary": 50000.00
                          }
                        ]
                        """));
    }

    @Test
    public void shouldGetSinglePersonByStudentTypeAndLastNameAndSex() throws Exception {
        //given
        DictionaryValueSimpleDto employee = new DictionaryValueSimpleDto(1L, "employee");
        DictionaryValueSimpleDto manager = new DictionaryValueSimpleDto(2L, "manager");
        DictionaryValueSimpleDto student = new DictionaryValueSimpleDto(3L, "student");
        DictionaryValueSimpleDto university = new DictionaryValueSimpleDto(4L, "test university");
        DictionaryValueSimpleDto fieldOfStudy = new DictionaryValueSimpleDto(5L, "test field of study");
        Mockito.when(dictionaryServiceClient.getDictionaryValueById(employee.getId())).thenReturn(employee);
        Mockito.when(dictionaryServiceClient.getDictionaryValueById(student.getId())).thenReturn(student);
        Mockito.when(dictionaryServiceClient.getDictionaryValueById(manager.getId())).thenReturn(manager);
        Mockito.when(dictionaryServiceClient.getDictionaryValueById(university.getId())).thenReturn(university);
        Mockito.when(dictionaryServiceClient.getDictionaryValueById(fieldOfStudy.getId())).thenReturn(fieldOfStudy);
        personRepository.saveAndFlush(
                new Employee(employee.getId(), "Anna", "Doe", "12345678921", 165, 55,
                        "anna.doe@test.com", LocalDate.of(2021, 1, 1), manager.getId(),
                        40000.00)
        );
        personRepository.saveAndFlush(
                new Student(student.getId(), "John", "Doe", "12345678912", 170, 60,
                        "john.doe@test.com", university.getId(), 2, fieldOfStudy.getId(),
                        50000.00)
        );
        personRepository.saveAndFlush(
                new Student(student.getId(), "Mia", "Doe", "1234567822", 160, 50,
                        "mia.doe@test.com", university.getId(), 2, fieldOfStudy.getId(),
                        1000.00)
        );
        //when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .get("/api/people?typeId=" + student.getId() + "&lastName=Doe&sex=w"));
        //then
        resultActions
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        [
                          {
                            "type": "student",
                            "firstName": "Mia",
                            "lastName": "Doe",
                            "email": "mia.doe@test.com",
                            "version": 0,
                            "universityName": "test university",
                            "enrollmentYear": 2,
                            "fieldOfStudy": "test field of study",
                            "scholarship": 1000.00
                          }
                        ]
                        """));
    }

    @Test
    public void shouldGetSingleEmployeeByPositionAndEmploymentStartDateRange() throws Exception {
        //given
        DictionaryValueSimpleDto employee = new DictionaryValueSimpleDto(1L, "employee");
        DictionaryValueSimpleDto manager = new DictionaryValueSimpleDto(2L, "manager");
        Mockito.when(dictionaryServiceClient.getDictionaryValueById(employee.getId())).thenReturn(employee);
        Mockito.when(dictionaryServiceClient.getDictionaryValueById(manager.getId())).thenReturn(manager);
        personRepository.saveAndFlush(
                new Employee(employee.getId(), "John", "Doe", "12345678911", 180, 70,
                        "john.doe@test.com", LocalDate.of(2021, 1, 1), manager.getId(),
                        40000.00)
        );
        personRepository.saveAndFlush(
                new Employee(employee.getId(), "Tom", "Doe", "12345678912", 170, 60,
                        "tom.doe@test.com", LocalDate.of(2022, 1, 1), manager.getId(),
                        50000.00)
        );
        //when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .get("/api/people?typeId=" + employee.getId() + "&positionId= " + manager.getId() +
                        "&employmentStartDate=from2021-01-01,to2021-12-31"));
        //then
        resultActions
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        [
                         {
                            "type": "employee",
                            "firstName": "John",
                            "lastName": "Doe",
                            "email": "john.doe@test.com",
                            "version": 0,
                            "employmentStartDate": "2021-01-01",
                            "currentPosition": "manager",
                            "currentSalary": 40000.00
                          }
                        ]
                        """));
    }

    @Test
    public void shouldGetSingleEmployeeByPositionAndFirstName() throws Exception {
        //given
        DictionaryValueSimpleDto employee = new DictionaryValueSimpleDto(1L, "employee");
        DictionaryValueSimpleDto manager = new DictionaryValueSimpleDto(2L, "manager");
        Mockito.when(dictionaryServiceClient.getDictionaryValueById(employee.getId())).thenReturn(employee);
        Mockito.when(dictionaryServiceClient.getDictionaryValueById(manager.getId())).thenReturn(manager);
        personRepository.saveAndFlush(
                new Employee(employee.getId(), "John", "Doe", "12345678911", 180, 70,
                        "john.doe@test.com", LocalDate.of(2021, 1, 1), manager.getId(),
                        40000.00)
        );
        personRepository.saveAndFlush(
                new Employee(employee.getId(), "Tom", "Doe", "12345678912", 170, 60,
                        "tom.doe@test.com", LocalDate.of(2022, 1, 1), manager.getId(),
                        50000.00)
        );
        //when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .get("/api/people?typeId=" + employee.getId() + "&firstName=Tom&positionId=" + manager.getId()));
        //then
        resultActions
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        [
                         {
                            "type": "employee",
                            "firstName": "Tom",
                            "lastName": "Doe",
                            "email": "tom.doe@test.com",
                            "version": 0,
                            "employmentStartDate": "2022-01-01",
                            "currentPosition": "manager",
                            "currentSalary": 50000.00
                          }
                        ]
                        """));
    }

    @Test
    public void shouldUpdateEmployeeLastNameAndPositionAndSalary() throws Exception {
        //given
        DictionaryValueSimpleDto employee = new DictionaryValueSimpleDto(1L, "employee");
        DictionaryValueSimpleDto manager = new DictionaryValueSimpleDto(2L, "manager");
        DictionaryValueSimpleDto director = new DictionaryValueSimpleDto(3L, "director");
        Mockito.when(dictionaryServiceClient.getDictionaryValueById(employee.getId())).thenReturn(employee);
        Mockito.when(dictionaryServiceClient
                .getDictionaryValueByDictionaryIdAndName(1L, employee.getName())).thenReturn(employee);
        Mockito.when(dictionaryServiceClient.getDictionaryValueById(manager.getId())).thenReturn(manager);
        Mockito.when(dictionaryServiceClient.getDictionaryValueById(director.getId())).thenReturn(director);
        Employee employeeToEdit = personRepository.saveAndFlush(
                new Employee(employee.getId(), "John", "Doe", "12345678911", 180, 70,
                        "john.doe@test.com", LocalDate.of(2021, 1, 1), manager.getId(),
                        40000.00)
        );
        Long id = employeeToEdit.getId();
        UpdatePersonCommand updateEmployeeCommandForTest = new UpdateEmployeeCommand(
                id, employee.getName(), "Jonathan", "Doe", "12345678911", 180, 70,
                "john.doe@test.com", 0L, LocalDate.of(2021, 1, 1),
                director.getId(), 60000.00
        );
        String jsonForTest = objectMapper.writeValueAsString(updateEmployeeCommandForTest);
        //when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .put("/api/people/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonForTest));
        //then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.type", is("employee")))
                .andExpect(jsonPath("$.firstName", is("Jonathan")))
                .andExpect(jsonPath("$.lastName", is("Doe")))
                .andExpect(jsonPath("$.email", is("john.doe@test.com")))
                .andExpect(jsonPath("$.version", is(1)))
                .andExpect(jsonPath("$.employmentStartDate", is("2021-01-01")))
                .andExpect(jsonPath("$.currentPosition", is("director")))
                .andExpect(jsonPath("$.currentSalary", is(60000.00)));
    }

    @Test
    public void shouldReturnConflictStatusForOptimisticLockingExceptionWhenUpdateOutOfDateVersion() throws Exception {
        //given
        DictionaryValueSimpleDto employee = new DictionaryValueSimpleDto(1L, "employee");
        DictionaryValueSimpleDto manager = new DictionaryValueSimpleDto(2L, "manager");
        DictionaryValueSimpleDto director = new DictionaryValueSimpleDto(3L, "director");
        Mockito.when(dictionaryServiceClient.getDictionaryValueById(employee.getId())).thenReturn(employee);
        Mockito.when(dictionaryServiceClient
                .getDictionaryValueByDictionaryIdAndName(1L, employee.getName())).thenReturn(employee);
        Mockito.when(dictionaryServiceClient.getDictionaryValueById(manager.getId())).thenReturn(manager);
        Mockito.when(dictionaryServiceClient.getDictionaryValueById(director.getId())).thenReturn(director);
        Employee employeeToEdit = personRepository.saveAndFlush(
                new Employee(employee.getId(), "John", "Doe", "12345678911", 180, 70,
                        "john.doe@test.com", LocalDate.of(2021, 1, 1), manager.getId(),
                        40000.00));
        Long id = employeeToEdit.getId();
        UpdatePersonCommand updateEmployeeCommandFoTest1 = new UpdateEmployeeCommand(
                id, employee.getName(), "Jonathan", "Doe", "12345678911", 180, 70,
                "john.doe@test.com", 0L, LocalDate.of(2021, 1, 1),
                manager.getId(), 50000.00
        );
        UpdatePersonCommand updateEmployeeCommandFoTest2 = new UpdateEmployeeCommand(
                id, employee.getName(), "Jonathan", "Doe", "12345678911", 180, 70,
                "john.doe@test.com", 0L, LocalDate.of(2021, 1, 1),
                director.getId(), 60000.00
        );
        //first update attempt
        String jsonForTest1 = objectMapper.writeValueAsString(updateEmployeeCommandFoTest1);
        mockMvc.perform(MockMvcRequestBuilders
                .put("/api/people/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonForTest1))
                .andExpect(status().isOk());
        //when
        //second update attempt
        String jsonForTest2 = objectMapper.writeValueAsString(updateEmployeeCommandFoTest2);
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .put("/api/people/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonForTest2));
        //then
        resultActions
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.timestamp", is(notNullValue())))
                .andExpect(jsonPath("$.errorCode").value("CONFLICT"))
                .andExpect(jsonPath("$.errorMessages",
                        hasItem("Row was updated or deleted by another transaction (or unsaved-value mapping was " +
                                "incorrect). Current version of entity: 1")));
    }

    @Test
    public void shouldReturnNotFoundStatusForNotExistingIdWhenUpdate() throws Exception {
        //given
        DictionaryValueSimpleDto employee = new DictionaryValueSimpleDto(1L, "employee");
        DictionaryValueSimpleDto manager = new DictionaryValueSimpleDto(2L, "manager");
        Mockito.when(dictionaryServiceClient
                .getDictionaryValueByDictionaryIdAndName(1L, employee.getName())).thenReturn(employee);
        Mockito.when(dictionaryServiceClient.getDictionaryValueById(manager.getId())).thenReturn(manager);
        UpdatePersonCommand updateEmployeeCommandForTest = new UpdateEmployeeCommand(
                10L, employee.getName(), "John", "Doe", "12345678911", 180, 70,
                "john.doe@test.com", 0L, LocalDate.of(2021, 1, 1),
                manager.getId(), 50000.00
        );
        //when
        String jsonForTest = objectMapper.writeValueAsString(updateEmployeeCommandForTest);
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .put("/api/people/" + 10)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonForTest));
        //then
        resultActions
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.timestamp", is(notNullValue())))
                .andExpect(jsonPath("$.errorCode").value("NOT_FOUND"))
                .andExpect(jsonPath("$.errorMessages",
                        hasItem("Person with id " + updateEmployeeCommandForTest.getId() + " not found!")));
    }

    @Test
    public void shouldReturnConstraintViolationExceptionForTheSamePesel() throws Exception {
        //given
        DictionaryValueSimpleDto employee = new DictionaryValueSimpleDto(1L, "employee");
        DictionaryValueSimpleDto manager = new DictionaryValueSimpleDto(2L, "manager");
        Mockito.when(dictionaryServiceClient.getDictionaryValueById(employee.getId())).thenReturn(employee);
        Mockito.when(dictionaryServiceClient
                .getDictionaryValueByDictionaryIdAndName(1L, employee.getName())).thenReturn(employee);
        Mockito.when(dictionaryServiceClient.getDictionaryValueById(manager.getId())).thenReturn(manager);
        Employee employee1 = personRepository.saveAndFlush(
                new Employee(employee.getId(), "John", "Doe", "12345678911", 180, 70,
                        "john.doe@test.com", LocalDate.of(2021, 1, 1), manager.getId(),
                        40000.00)
        );
        Employee employee2 = personRepository.saveAndFlush(
                new Employee(employee.getId(), "Tom", "Doe", "12345678912", 170, 60,
                        "tom.doe@test.com", LocalDate.of(2021, 1, 1), manager.getId(),
                        50000.00)
        );
        String pesel1 = employee1.getPesel();
        Long id2 = employee2.getId();
        UpdatePersonCommand updateEmployeeCommandForTest = new UpdateEmployeeCommand(
                id2, employee.getName(), "Tom", "Doe", pesel1, 170, 60,
                "tom.doe@test.com", 0L, LocalDate.of(2021, 1, 1),
                manager.getId(), 50000.00);
        //when
        String jsonForTest = objectMapper.writeValueAsString(updateEmployeeCommandForTest);
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .put("/api/people/" + id2)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonForTest));
        //then
        resultActions
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp", is(notNullValue())))
                .andExpect(jsonPath("$.errorCode").value("PESEL_NOT_UNIQUE"))
                .andExpect(jsonPath("$.errorMessages", hasItem("Duplicated entry for 'pesel' field.")));
    }

    @Test
    public void shouldReturnBadRequestForNotValidPeselAndHeightWhenUpdate() throws Exception {
        //given
        DictionaryValueSimpleDto employee = new DictionaryValueSimpleDto(1L, "employee");
        DictionaryValueSimpleDto manager = new DictionaryValueSimpleDto(2L, "manager");
        Mockito.when(dictionaryServiceClient
                .getDictionaryValueByDictionaryIdAndName(1L, employee.getName())).thenReturn(employee);
        Mockito.when(dictionaryServiceClient.getDictionaryValueById(manager.getId())).thenReturn(manager);
        Employee employeeToUpdate = personRepository.saveAndFlush(
                new Employee(employee.getId(), "John", "Doe", "12345678911", 180, 70,
                        "john.doe@test.com", LocalDate.of(2021, 1, 1), manager.getId(),
                        40000.00)
        );
        Long employeeId = employeeToUpdate.getId();
        UpdatePersonCommand updateEmployeeCommandForTest = new UpdateEmployeeCommand(
                employeeId, employee.getName(), "John", "Doe", "1234567911", 0, 70,
                "john.doe@test.com", 0L, LocalDate.of(2021, 1, 1),
                manager.getId(), 50000.00
        );
        //when
        String jsonForTest = objectMapper.writeValueAsString(updateEmployeeCommandForTest);
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .put("/api/people/" + employeeId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonForTest));
        //then
        resultActions
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp", is(notNullValue())))
                .andExpect(jsonPath("$.errorCode").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.errorMessages", hasItem("field: pesel / rejectedValue: '1234567911' " +
                        "/ message: Pesel cannot be null; should contain 11 digits")))
                .andExpect(jsonPath("$.errorMessages", hasItem("field: height / rejectedValue: '0' " +
                        "/ message: Cannot be null; must be positive")));
    }

    @Test
    public void shouldPaginate10ResultsIntoTwoPagesWith5Results() throws Exception {
        //given
        DictionaryValueSimpleDto employee = new DictionaryValueSimpleDto(1L, "employee");
        DictionaryValueSimpleDto manager = new DictionaryValueSimpleDto(2L, "manager");
        Mockito.when(dictionaryServiceClient.getDictionaryValueById(employee.getId())).thenReturn(employee);
        Mockito.when(dictionaryServiceClient.getDictionaryValueById(manager.getId())).thenReturn(manager);
        for (int i = 0; i < 10; i++) {
            Employee employeeForTest = new Employee(employee.getId(), "Test" + i, "TestTest" + i,
                    "1234567891" + i, 170, 70, "test" + i + "@test.com",
                    LocalDate.of(2021, 1, 1), manager.getId(), 40000.00);
            personRepository.saveAndFlush(employeeForTest);
        }
        //when
        int pageNumber = 1;
        int pageSize = 5;
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .get("/api/people?page=" + pageNumber + "&size=" + pageSize));
        //then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(pageSize)))
                .andExpect(jsonPath("$[0].firstName", is("Test5")));
    }

    @Test
    public void shouldReturnBadRequestStatusForEmptyFileWhenImport() throws Exception {
        //given
        String fileContent = "";
        MockMultipartFile file = new MockMultipartFile(
                "file", "test-peopleToImport.csv", "text/csv", fileContent.getBytes()
        );
        //when
        MvcResult asyncResult = mockMvc.perform(MockMvcRequestBuilders
                .multipart("/api/people/import")
                .file(file))
                .andExpect(request().asyncStarted())
                .andReturn();
        asyncResult.getAsyncResult();
        //then
        mockMvc.perform(asyncDispatch(asyncResult))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp", is(notNullValue())))
                .andExpect(jsonPath("$.errorCode").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.errorMessages",
                        hasItem("File is empty or does not exist.")));
    }

    @Test
    public void shouldReturnBadRequestStatusForInvalidFileContentWhenImport() throws Exception {
        //given
        String fileContent = "Invalid CSV Data " +
                "\n Invalid CSV Data";
        MockMultipartFile file = new MockMultipartFile(
                "file", "test-peopleToImport.csv", "text/csv", fileContent.getBytes()
        );
        //when
        MvcResult asyncResult = mockMvc.perform(MockMvcRequestBuilders
                .multipart("/api/people/import")
                .file(file))
                .andExpect(request().asyncStarted())
                .andReturn();
        asyncResult.getAsyncResult();
        //then
        mockMvc.perform(asyncDispatch(asyncResult))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp", is(notNullValue())))
                .andExpect(jsonPath("$.errorCode").value("BAD_REQUEST"))
                .andExpect(content().string(containsString("Error during data import. Invalid file content")));
    }

    @Test
    public void shouldAddEmployee() throws Exception {
        //given
        DictionaryValueSimpleDto employee = new DictionaryValueSimpleDto(1L, "employee");
        DictionaryValueSimpleDto manager = new DictionaryValueSimpleDto(2L, "manager");
        Mockito.when(dictionaryServiceClient
                .getDictionaryValueByDictionaryIdAndName(1L, employee.getName())).thenReturn(employee);
        Mockito.when(dictionaryServiceClient.getDictionaryValueById(employee.getId())).thenReturn(employee);
        Mockito.when(dictionaryServiceClient.getDictionaryValueById(manager.getId())).thenReturn(manager);
        CreatePersonCommand createEmployeeCommandForTest = new CreateEmployeeCommand(
                employee.getName(), "John", "Doe", "12345678910", 180, 70,
                "john.doe@test.com", LocalDate.of(2021, 1, 1),
                manager.getId(), 40000.00
        );
        String jsonForTest = objectMapper.writeValueAsString(createEmployeeCommandForTest);
        //when
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                .post("/api/people")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonForTest))
                .andExpect(status().isOk())
                .andReturn();
        String responseContent = result.getResponse().getContentAsString();
        Employee employeeToAdd = objectMapper.readValue(responseContent, Employee.class);
        Long employeeId = employeeToAdd.getId();
        //then
        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/people/" + employeeId))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        {
                          "type": "employee",
                          "firstName": "John",
                          "lastName": "Doe",
                          "email": "john.doe@test.com",
                          "version": 0,
                          "employmentStartDate": "2021-01-01",
                          "currentPosition": "manager",
                          "currentSalary": 40000.00
                        },
                        """));
    }

    @Test
    public void shouldAddNewPersonType() throws Exception {
        //given
        String newType = "guest";
        DictionarySimpleDto typeDictionary = new DictionarySimpleDto(1L, "type");
        Mockito.when(dictionaryServiceClient.addValueToTypeDictionary(newType)).thenReturn(typeDictionary);
        //when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .post("/api/people/type?name=" + newType));
        //then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status")
                        .value("New person type 'guest' added to 'type' dictionary."));
    }

    @Test
    public void shouldDeletePersonById() throws Exception {
        //given
        DictionaryValueSimpleDto employee = new DictionaryValueSimpleDto(1L, "employee");
        DictionaryValueSimpleDto manager = new DictionaryValueSimpleDto(2L, "manager");
        Mockito.when(dictionaryServiceClient.getDictionaryValueById(employee.getId())).thenReturn(employee);
        Mockito.when(dictionaryServiceClient.getDictionaryValueById(manager.getId())).thenReturn(manager);
        Employee employeeToDelete = personRepository.saveAndFlush(
                new Employee(employee.getId(), "John", "Doe",
                        "12345678911", 180, 70, "john.doe@test.com",
                        LocalDate.of(2021, 1, 1), manager.getId(), 40000.00)
        );
        Long personId = employeeToDelete.getId();
        //when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .delete("/api/people/" + personId));
        //then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status")
                        .value("Person with id " + personId + " deleted"));
    }

    @AfterEach
    public void tearDown() {
        personRepository.deleteAllInBatch();
    }

}