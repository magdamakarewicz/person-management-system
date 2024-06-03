package com.enjoythecode.personservice.controllers;

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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import com.enjoythecode.personservice.PersonServiceApplication;
import com.enjoythecode.personservice.api.DictionaryServiceClient;
import com.enjoythecode.personservice.command.CreateEmployeePositionCommand;
import com.enjoythecode.personservice.command.UpdateEmployeePositionEndDateCommand;
import com.enjoythecode.personservice.dto.DictionaryValueSimpleDto;
import com.enjoythecode.personservice.exception.DictionaryValueNotFoundException;
import com.enjoythecode.personservice.model.Employee;
import com.enjoythecode.personservice.model.EmployeePosition;
import com.enjoythecode.personservice.repository.EmployeePositionRepository;
import com.enjoythecode.personservice.repository.PersonRepository;

import java.time.LocalDate;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = PersonServiceApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class EmployeePositionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private EmployeePositionRepository employeePositionRepository;

    @MockBean
    private DictionaryServiceClient dictionaryServiceClient;

    @BeforeEach
    public void setUp() {
        employeePositionRepository.deleteAllInBatch();
        personRepository.deleteAllInBatch();
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void shouldReturnOkStatusWhenAddCorrectNewPositionToEmployee() throws Exception {
        //given
        DictionaryValueSimpleDto employee = new DictionaryValueSimpleDto(1L, "employee");
        DictionaryValueSimpleDto currentPosition = new DictionaryValueSimpleDto(2L, "manager");
        DictionaryValueSimpleDto newPosition = new DictionaryValueSimpleDto(3L, "director");
        Mockito.when(dictionaryServiceClient.getDictionaryValueById(currentPosition.getId())).thenReturn(currentPosition);
        Mockito.when(dictionaryServiceClient.getDictionaryValueByDictionaryIdAndName(2L,
                currentPosition.getName())).thenReturn(currentPosition);
        Mockito.when(dictionaryServiceClient.getDictionaryValueById(newPosition.getId())).thenReturn(newPosition);
        Mockito.when(dictionaryServiceClient.getDictionaryValueByDictionaryIdAndName(2L,
                newPosition.getName())).thenReturn(newPosition);
        Employee employeeForSave = personRepository.saveAndFlush(
                new Employee(employee.getId(), "John", "Doe", "12345678911", 180, 70,
                        "john.doe@test.com", LocalDate.of(2021, 1, 1), currentPosition.getId(),
                        40000.00));
        Long employeeId = employeeForSave.getId();
        CreateEmployeePositionCommand employeePositionForTest = new CreateEmployeePositionCommand(
                newPosition.getId(), LocalDate.of(2021, 2, 1), 50000.00
        );
        String jsonForTest = objectMapper.writeValueAsString(employeePositionForTest);
        //when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .post("/api/employees/" + employeeId + "/positions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonForTest));
        //then
        resultActions
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        {
                            "personSimpleDto":{"firstName":"John","lastName":"Doe"},
                            "position":"director",
                            "startDate":"2021-02-01",
                            "endDate":null,
                            "salary":50000.0
                        }
                                    """));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void shouldReturnBadRequestWhenAddNewPositionToEmployeeWithPositionWithoutEndDate() throws Exception {
        //given
        DictionaryValueSimpleDto employee = new DictionaryValueSimpleDto(1L, "employee");
        DictionaryValueSimpleDto currentPosition = new DictionaryValueSimpleDto(2L, "manager");
        DictionaryValueSimpleDto newPosition = new DictionaryValueSimpleDto(3L, "director");
        Mockito.when(dictionaryServiceClient.getDictionaryValueById(currentPosition.getId())).thenReturn(currentPosition);
        Mockito.when(dictionaryServiceClient.getDictionaryValueByDictionaryIdAndName(2L,
                currentPosition.getName())).thenReturn(currentPosition);
        Mockito.when(dictionaryServiceClient.getDictionaryValueById(newPosition.getId())).thenReturn(newPosition);
        Mockito.when(dictionaryServiceClient.getDictionaryValueByDictionaryIdAndName(2L,
                newPosition.getName())).thenReturn(newPosition);
        Employee employeeForTest = personRepository.saveAndFlush(
                new Employee(employee.getId(), "John", "Doe", "12345678911", 180, 70,
                        "john.doe@test.com", LocalDate.of(2021, 1, 1), currentPosition.getId(),
                        40000.00)
        );
        Long employeeId = employeeForTest.getId();
        employeePositionRepository.saveAndFlush(
                new EmployeePosition(employeeForTest, newPosition.getId(), LocalDate.of(2021, 2, 1),
                        null, 50000.00)
        );
        CreateEmployeePositionCommand employeePositionForTest = new CreateEmployeePositionCommand(
                newPosition.getId(), LocalDate.of(2021, 2, 1), 60000.00
        );
        String jsonForTest = objectMapper.writeValueAsString(employeePositionForTest);
        //when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .post("/api/employees/" + employeeId + "/positions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonForTest));
        //then
        resultActions
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp", is(notNullValue())))
                .andExpect(jsonPath("$.errorCode").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.errorMessages", hasItem("Not all existing employee's positions have " +
                        "an end date. To add new position all previous must be over.")));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void shouldReturnBadRequestWhenAddNewPositionWithStartDateBeforeEmploymentStartDate() throws Exception {
        //given
        DictionaryValueSimpleDto employee = new DictionaryValueSimpleDto(1L, "employee");
        DictionaryValueSimpleDto currentPosition = new DictionaryValueSimpleDto(2L, "manager");
        DictionaryValueSimpleDto newPosition = new DictionaryValueSimpleDto(3L, "director");
        Mockito.when(dictionaryServiceClient.getDictionaryValueById(employee.getId())).thenReturn(employee);
        Mockito.when(dictionaryServiceClient.getDictionaryValueById(currentPosition.getId())).thenReturn(currentPosition);
        Mockito.when(dictionaryServiceClient.getDictionaryValueByDictionaryIdAndName(2L,
                currentPosition.getName())).thenReturn(currentPosition);
        Mockito.when(dictionaryServiceClient.getDictionaryValueById(newPosition.getId())).thenReturn(newPosition);
        Mockito.when(dictionaryServiceClient.getDictionaryValueByDictionaryIdAndName(2L,
                newPosition.getName())).thenReturn(newPosition);
        Employee employeeForTest = personRepository.saveAndFlush(
                new Employee(employee.getId(), "John", "Doe", "12345678911", 180, 70,
                        "john.doe@test.com", LocalDate.of(2021, 1, 1), currentPosition.getId(),
                        40000.00)
        );
        Long employeeId = employeeForTest.getId();
        CreateEmployeePositionCommand employeePositionForTest = new CreateEmployeePositionCommand(
                newPosition.getId(), LocalDate.of(2020, 12, 1), 50000.00
        );
        String jsonForTest = objectMapper.writeValueAsString(employeePositionForTest);
        //when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .post("/api/employees/" + employeeId + "/positions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonForTest));
        //then
        resultActions
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp", is(notNullValue())))
                .andExpect(jsonPath("$.errorCode").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.errorMessages", hasItem("Start date of the new position cannot be " +
                        "before employee's employment start date: 2021-01-01")));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void shouldReturnBadRequestWhenAddNewPositionIfNewPositionOverlapsExistingOne() throws Exception {
        //given
        DictionaryValueSimpleDto employee = new DictionaryValueSimpleDto(1L, "employee");
        DictionaryValueSimpleDto currentPosition = new DictionaryValueSimpleDto(2L, "manager");
        DictionaryValueSimpleDto newPosition = new DictionaryValueSimpleDto(3L, "director");
        Mockito.when(dictionaryServiceClient.getDictionaryValueById(employee.getId())).thenReturn(employee);
        Mockito.when(dictionaryServiceClient.getDictionaryValueById(currentPosition.getId())).thenReturn(currentPosition);
        Mockito.when(dictionaryServiceClient.getDictionaryValueByDictionaryIdAndName(2L,
                currentPosition.getName())).thenReturn(currentPosition);
        Mockito.when(dictionaryServiceClient.getDictionaryValueById(newPosition.getId())).thenReturn(newPosition);
        Mockito.when(dictionaryServiceClient.getDictionaryValueByDictionaryIdAndName(2L,
                newPosition.getName())).thenReturn(newPosition);
        Employee employeeForTest = personRepository.saveAndFlush(
                new Employee(employee.getId(), "John", "Doe", "12345678911", 180, 70,
                        "john.doe@test.com", LocalDate.of(2021, 1, 1), currentPosition.getId(),
                        40000.00)
        );
        Long employeeId = employeeForTest.getId();
        employeePositionRepository.saveAndFlush(
                new EmployeePosition(employeeForTest, newPosition.getId(), LocalDate.of(2021, 2, 1),
                        LocalDate.of(2022, 2, 1), 50000.00)
        );
        CreateEmployeePositionCommand employeePositionForTest = new CreateEmployeePositionCommand(
                newPosition.getId(), LocalDate.of(2021, 12, 1), 50000.00
        );
        String jsonForTest = objectMapper.writeValueAsString(employeePositionForTest);
        //when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .post("/api/employees/" + employeeId + "/positions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonForTest));
        //then
        resultActions
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp", is(notNullValue())))
                .andExpect(jsonPath("$.errorCode").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.errorMessages",
                        hasItem("New position overlaps with an existing one.")));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void shouldReturnNotFoundWhenAddPositionThatNotExist() throws Exception {
        //given
        DictionaryValueSimpleDto employee = new DictionaryValueSimpleDto(1L, "employee");
        DictionaryValueSimpleDto currentPosition = new DictionaryValueSimpleDto(2L, "manager");
        Long newPositionId = 3L;
        Mockito.when(dictionaryServiceClient.getDictionaryValueById(employee.getId())).thenReturn(employee);
        Mockito.when(dictionaryServiceClient.getDictionaryValueById(currentPosition.getId())).thenReturn(currentPosition);
        Mockito.when(dictionaryServiceClient.getDictionaryValueByDictionaryIdAndName(2L,
                currentPosition.getName())).thenReturn(currentPosition);
        Mockito.when(dictionaryServiceClient.getDictionaryValueById(newPositionId))
                .thenThrow(new DictionaryValueNotFoundException("Dictionary value with id '" + newPositionId + "' not found."));
        Employee employeeForTest = personRepository.saveAndFlush(
                new Employee(employee.getId(), "John", "Doe", "12345678911", 180, 70,
                        "john.doe@test.com", LocalDate.of(2021, 1, 1), currentPosition.getId(),
                        40000.00)
        );
        Long employeeId = employeeForTest.getId();
        CreateEmployeePositionCommand employeePositionForTest = new CreateEmployeePositionCommand(
                newPositionId, LocalDate.of(2021, 12, 1), 50000.00
        );
        String jsonForTest = objectMapper.writeValueAsString(employeePositionForTest);
        //when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .post("/api/employees/" + employeeId + "/positions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonForTest));
        //then
        resultActions
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.timestamp", is(notNullValue())))
                .andExpect(jsonPath("$.errorCode").value("NOT_FOUND"))
                .andExpect(jsonPath("$.errorMessages", hasItem(
                        "Dictionary value with id '" + newPositionId + "' not found."
                )));
    }

    @Test
    @WithMockUser
    public void shouldReturnOkStatusWhenAddCorrectEndDateAfterStartDateForCurrentPosition() throws Exception {
        //given
        DictionaryValueSimpleDto employee = new DictionaryValueSimpleDto(1L, "employee");
        DictionaryValueSimpleDto currentPosition = new DictionaryValueSimpleDto(2L, "manager");
        DictionaryValueSimpleDto newPosition = new DictionaryValueSimpleDto(3L, "director");
        Mockito.when(dictionaryServiceClient.getDictionaryValueById(employee.getId())).thenReturn(employee);
        Mockito.when(dictionaryServiceClient.getDictionaryValueById(currentPosition.getId())).thenReturn(currentPosition);
        Mockito.when(dictionaryServiceClient.getDictionaryValueById(newPosition.getId())).thenReturn(newPosition);
        Employee employeeForTest = personRepository.saveAndFlush(
                new Employee(employee.getId(), "John", "Doe", "12345678911", 180, 70,
                        "john.doe@test.com", LocalDate.of(2021, 1, 1),
                        currentPosition.getId(), 40000.00));
        Long employeeId = employeeForTest.getId();
        EmployeePosition employeePosition = employeePositionRepository.saveAndFlush(
                new EmployeePosition(employeeForTest, newPosition.getId(), LocalDate.of(2021, 2, 1),
                        null, 50000.00)
        );
        Long positionId = employeePosition.getId();
        UpdateEmployeePositionEndDateCommand updatePositionCommand = new UpdateEmployeePositionEndDateCommand(
                LocalDate.of(2022, 12, 1)
        );
        String jsonForTest = objectMapper.writeValueAsString(updatePositionCommand);
        //when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .patch("/api/employees/" + employeeId + "/positions/" + positionId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonForTest));
        //then
        resultActions
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        {
                            "position":"director",
                            "startDate":"2021-02-01",
                            "endDate":"2022-12-01",
                            "salary":50000.0
                        }
                                    """));
    }

    @Test
    @WithMockUser
    public void shouldReturnBadRequestWhenAddEmptyEndDateForPositionWhenUpdate() throws Exception {
        //given
        DictionaryValueSimpleDto employee = new DictionaryValueSimpleDto(1L, "employee");
        DictionaryValueSimpleDto currentPosition = new DictionaryValueSimpleDto(2L, "manager");
        DictionaryValueSimpleDto newPosition = new DictionaryValueSimpleDto(3L, "director");
        Mockito.when(dictionaryServiceClient.getDictionaryValueById(employee.getId())).thenReturn(employee);
        Mockito.when(dictionaryServiceClient.getDictionaryValueById(currentPosition.getId())).thenReturn(currentPosition);
        Mockito.when(dictionaryServiceClient.getDictionaryValueById(newPosition.getId())).thenReturn(newPosition);
        Employee employeeForTest = personRepository.saveAndFlush(
                new Employee(employee.getId(), "John", "Doe", "12345678911", 180, 70,
                        "john.doe@test.com", LocalDate.of(2021, 1, 1),
                        currentPosition.getId(), 40000.00)
        );
        Long employeeId = employeeForTest.getId();
        EmployeePosition employeePosition = employeePositionRepository.saveAndFlush(
                new EmployeePosition(employeeForTest, newPosition.getId(), LocalDate.of(2021, 2, 1),
                        null, 50000.00)
        );
        Long positionId = employeePosition.getId();
        UpdateEmployeePositionEndDateCommand updatePositionCommand = new UpdateEmployeePositionEndDateCommand(null);
        String jsonForTest = objectMapper.writeValueAsString(updatePositionCommand);
        //when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .patch("/api/employees/" + employeeId + "/positions/" + positionId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonForTest));
        //then
        resultActions
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp", is(notNullValue())))
                .andExpect(jsonPath("$.errorCode").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.errorMessages",
                        hasItem("field: endDate / rejectedValue: 'null' / message: Cannot be null")));
    }

    @Test
    @WithMockUser
    public void shouldReturnListOfPositionsWhenGetAllEmployeePositionsByEmployeeId() throws Exception {
        //given
        DictionaryValueSimpleDto employee = new DictionaryValueSimpleDto(1L, "employee");
        DictionaryValueSimpleDto manager = new DictionaryValueSimpleDto(2L, "manager");
        DictionaryValueSimpleDto director = new DictionaryValueSimpleDto(3L, "director");
        Mockito.when(dictionaryServiceClient.getDictionaryValueById(employee.getId())).thenReturn(employee);
        Mockito.when(dictionaryServiceClient.getDictionaryValueById(manager.getId())).thenReturn(manager);
        Mockito.when(dictionaryServiceClient.getDictionaryValueById(director.getId())).thenReturn(director);
        Employee employeeForTest = personRepository.saveAndFlush(
                new Employee(employee.getId(), "John", "Doe",
                        "12345678911", 180, 70, "john.doe@test.com",
                        LocalDate.of(2015, 1, 1), manager.getId(), 40000.00)
        );
        Long employeeId = employeeForTest.getId();
        employeePositionRepository.saveAndFlush(
                new EmployeePosition(employeeForTest, manager.getId(), LocalDate.of(2021, 2, 1),
                        LocalDate.of(2023, 9, 15), 50000.00)
        );
        employeePositionRepository.saveAndFlush(
                new EmployeePosition(employeeForTest, director.getId(), LocalDate.of(2023, 9, 16),
                        null, 70000.00)
        );
        //when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .get("/api/employees/" + employeeId + "/positions"));
        //then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].position").value("manager"))
                .andExpect(jsonPath("$[0].startDate").value("2021-02-01"))
                .andExpect(jsonPath("$[0].endDate").value("2023-09-15"))
                .andExpect(jsonPath("$[0].salary").value("50000.0"))
                .andExpect(jsonPath("$[1].position").value("director"))
                .andExpect(jsonPath("$[1].startDate").value("2023-09-16"))
                .andExpect(jsonPath("$[1].endDate").isEmpty())
                .andExpect(jsonPath("$[1].salary").value("70000.0"));
    }

    @Test
    @WithMockUser
    public void shouldReturnBadRequestWhenPositionNotBelongsToEmployeeWithProvidedId() throws Exception {
        //given
        DictionaryValueSimpleDto employee = new DictionaryValueSimpleDto(1L, "employee");
        DictionaryValueSimpleDto manager = new DictionaryValueSimpleDto(2L, "manager");
        Mockito.when(dictionaryServiceClient.getDictionaryValueById(employee.getId())).thenReturn(employee);
        Mockito.when(dictionaryServiceClient.getDictionaryValueById(manager.getId())).thenReturn(manager);
        Employee employee1 = personRepository.saveAndFlush(
                new Employee(employee.getId(), "John", "Doe",
                        "12345678911", 180, 70, "john.doe@test.com",
                        LocalDate.of(2021, 1, 1), manager.getId(), 40000.00)
        );
        Long employee1Id = employee1.getId();
        Employee employee2 = personRepository.saveAndFlush(
                new Employee(employee.getId(), "Tom", "Black",
                        "12345678912", 180, 70, "tom.black@test.com",
                        LocalDate.of(2021, 1, 1), manager.getId(), 45000.00)
        );
        EmployeePosition employeePosition = employeePositionRepository.saveAndFlush(
                new EmployeePosition(employee2, manager.getId(), LocalDate.of(2022, 2, 1),
                        null, 70000.00)
        );
        Long employeePositionId = employeePosition.getId();
        //when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .get("/api/employees/" + employee1Id + "/positions/" + employeePositionId));
        //then
        resultActions
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp", is(notNullValue())))
                .andExpect(jsonPath("$.errorCode").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.errorMessages",
                        hasItem("Position with id " + employeePositionId
                                + " does not belong to the employee with id " + employee1Id)));
    }

    @Test
    @WithMockUser
    public void shouldDeleteEmployeePositionById() throws Exception {
        //given
        DictionaryValueSimpleDto employee = new DictionaryValueSimpleDto(1L, "employee");
        DictionaryValueSimpleDto manager = new DictionaryValueSimpleDto(2L, "manager");
        Mockito.when(dictionaryServiceClient.getDictionaryValueById(employee.getId())).thenReturn(employee);
        Mockito.when(dictionaryServiceClient.getDictionaryValueById(manager.getId())).thenReturn(manager);
        Employee employeeForTest = personRepository.saveAndFlush(
                new Employee(employee.getId(), "John", "Doe",
                        "12345678911", 180, 70, "john.doe@test.com",
                        LocalDate.of(2021, 1, 1), manager.getId(), 40000.00)
        );
        Long employeeId = employeeForTest.getId();
        EmployeePosition employeePosition = employeePositionRepository.saveAndFlush(
                new EmployeePosition(employeeForTest, manager.getId(), LocalDate.of(2022, 2, 1),
                        null, 70000.00)
        );
        Long employeePositionId = employeePosition.getId();
        //when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .delete("/api/employees/" + employeeId + "/positions/" + employeePositionId));
        //then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status")
                        .value("Position with id " + employeePositionId
                                + " deleted from employee with id " + employeeId));
    }

    @Test
    @WithMockUser
    public void shouldReturnNotFoundStatusForNotExistingIdWhenGetAllByEmployeeId() throws Exception {
        //given
        int employeeId = 10;
        //when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .get("/api/employees/" + employeeId + "/positions"));
        //then
        resultActions
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.timestamp", is(notNullValue())))
                .andExpect(jsonPath("$.errorCode").value("NOT_FOUND"))
                .andExpect(jsonPath("$.errorMessages",
                        hasItem("Employee with id " + employeeId + " not found.")));
    }

    @AfterEach
    public void tearDown() {
        employeePositionRepository.deleteAllInBatch();
        personRepository.deleteAllInBatch();
    }

}