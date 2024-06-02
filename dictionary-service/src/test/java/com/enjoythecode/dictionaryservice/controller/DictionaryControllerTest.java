package com.enjoythecode.dictionaryservice.controller;

import com.enjoythecode.dictionaryservice.DictionaryServiceApplication;
import com.enjoythecode.dictionaryservice.command.CreateDictionaryCommand;
import com.enjoythecode.dictionaryservice.command.UpdateDictionaryCommand;
import com.enjoythecode.dictionaryservice.model.Dictionary;
import com.enjoythecode.dictionaryservice.model.DictionaryValue;
import com.enjoythecode.dictionaryservice.repository.DictionaryRepository;
import com.enjoythecode.dictionaryservice.repository.DictionaryValueRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = DictionaryServiceApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class DictionaryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DictionaryRepository dictionaryRepository;

    @Autowired
    private DictionaryValueRepository dictionaryValueRepository;

    @BeforeEach
    public void setUp() {
        dictionaryValueRepository.deleteAllInBatch();
        dictionaryRepository.deleteAllInBatch();
    }

    @Test
    public void shouldAddDictionary() throws Exception {
        //given
        CreateDictionaryCommand createDictionaryCommandForTest = new CreateDictionaryCommand("type");
        String jsonForTest = objectMapper.writeValueAsString(createDictionaryCommandForTest);
        //when
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/dictionaries")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonForTest))
                .andExpect(status().isOk())
                .andReturn();
        String responseContent = result.getResponse().getContentAsString();
        Dictionary dictionary = objectMapper.readValue(responseContent, Dictionary.class);
        Long addedDictionaryId = dictionary.getId();
        //then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/dictionaries/" + addedDictionaryId))
                .andExpect(jsonPath("$.id").value(addedDictionaryId))
                .andExpect(jsonPath("$.name").value("type"))
                .andExpect(jsonPath("$.dictionaryValues").doesNotExist())
                .andExpect(status().isOk());
    }

    @Test
    public void shouldDeleteDictionaryById() throws Exception {
        //given
        Long dictionaryId = dictionaryRepository.saveAndFlush(new Dictionary("type")).getId();
        //when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .delete("/api/dictionaries/" + dictionaryId));
        //then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status")
                        .value("Dictionary with id " + dictionaryId + " deleted."));
    }

    @Test
    public void shouldGetDictionaryById() throws Exception {
        //given
        Long dictionaryId = dictionaryRepository.saveAndFlush(new Dictionary("type")).getId();
        //when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/api/dictionaries/" + dictionaryId));
        //then
        resultActions
                .andExpect(jsonPath("$.id").value(dictionaryId))
                .andExpect(jsonPath("$.name").value("type"))
                .andExpect(jsonPath("$.dictionaryValues").doesNotExist())
                .andExpect(status().isOk());
    }

    @Test
    public void shouldGetAllDictionaries() throws Exception {
        //given
        List<Dictionary> dictionariesListForTest = List.of(
                new Dictionary("type"),
                new Dictionary("position")
        );
        dictionaryRepository.saveAllAndFlush(dictionariesListForTest);
        //when
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/dictionaries"))
                .andExpect(status().isOk())
                .andReturn();
        //then
        String jsonResponse = result.getResponse().getContentAsString();
        List<Dictionary> dictionaries = objectMapper.readValue(jsonResponse, new TypeReference<>() {});

        assertThat(dictionaries)
                .extracting(Dictionary::getName)
                .containsExactlyInAnyOrder("type", "position");
    }

    @Test
    public void shouldUpdateDictionaryName() throws Exception {
        //given
        Dictionary dictionary = dictionaryRepository.saveAndFlush(new Dictionary("types"));
        Long dictionaryId = dictionary.getId();
        UpdateDictionaryCommand updateDictionaryCommandForTest = new UpdateDictionaryCommand(dictionaryId, "type");
        String jsonForTest = objectMapper.writeValueAsString(updateDictionaryCommandForTest);
        //when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.put("/api/dictionaries/" + dictionaryId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonForTest));
        //then
        resultActions
                .andExpect(jsonPath("$.id").value(dictionaryId))
                .andExpect(jsonPath("$.name").value("type"))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldAddDictionaryValueToDictionary() throws Exception {
        //given
        Long dictionaryId = dictionaryRepository.saveAndFlush(new Dictionary("type")).getId();
        Long dictionaryValueId = dictionaryValueRepository.saveAndFlush(new DictionaryValue("student")).getId();
        //when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .post("/api/dictionaries/" + dictionaryId + "/values/" + dictionaryValueId));
        //then
        resultActions
                .andExpect(jsonPath("$.id").value(dictionaryId))
                .andExpect(jsonPath("$.name").value("type"))
                .andExpect(jsonPath("$.dictionaryValues").value("student"))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldRemoveDictionaryValueFromDictionary() throws Exception {
        //given
        Long dictionaryId = dictionaryRepository.saveAndFlush(new Dictionary("type")).getId();
        Long valueId = dictionaryValueRepository.saveAndFlush(new DictionaryValue("student")).getId();
        Long value2Id = dictionaryValueRepository.saveAndFlush(new DictionaryValue("employee")).getId();
        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/dictionaries/" + dictionaryId + "/values/" + valueId))
                .andExpect(jsonPath("$.id").value(dictionaryId))
                .andExpect(jsonPath("$.name").value("type"))
                .andExpect(jsonPath("$.dictionaryValues").value("student"))
                .andExpect(status().isOk());
        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/dictionaries/" + dictionaryId + "/values/" + value2Id))
                .andExpect(jsonPath("$.id").value(dictionaryId))
                .andExpect(jsonPath("$.name").value("type"))
                .andExpect(jsonPath("$.dictionaryValues",
                        Matchers.containsInAnyOrder("student", "employee")))
                .andExpect(status().isOk());
        //when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .delete("/api/dictionaries/" + dictionaryId + "/values/" + valueId));
        //then
        resultActions
                .andExpect(jsonPath("$.id").value(dictionaryId))
                .andExpect(jsonPath("$.name").value("type"))
                .andExpect(jsonPath("$.dictionaryValues").value("employee"))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldDeleteAllDictionaryValuesFromDictionary() throws Exception {
        //given
        Long dictionaryId = dictionaryRepository.saveAndFlush(new Dictionary("type")).getId();
        Long valueId = dictionaryValueRepository.saveAndFlush(new DictionaryValue("student")).getId();
        Long value2Id = dictionaryValueRepository.saveAndFlush(new DictionaryValue("employee")).getId();
        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/dictionaries/" + dictionaryId + "/values/" + valueId))
                .andExpect(status().isOk());
        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/dictionaries/" + dictionaryId + "/values/" + value2Id))
                .andExpect(jsonPath("$.id").value(dictionaryId))
                .andExpect(jsonPath("$.name").value("type"))
                .andExpect(jsonPath("$.dictionaryValues",
                        Matchers.containsInAnyOrder("student", "employee")))
                .andExpect(status().isOk());
        //when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .delete("/api/dictionaries/" + dictionaryId + "/values"));
        //then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status")
                        .value("Values from dictionary with id " + dictionaryId + " deleted."));
        mockMvc.perform(MockMvcRequestBuilders.get("/api/dictionaries/" + dictionaryId))
                .andExpect(jsonPath("$.id").value(dictionaryId))
                .andExpect(jsonPath("$.name").value("type"))
                .andExpect(jsonPath("$.dictionaryValues").doesNotExist())
                .andExpect(status().isOk());
    }

    @Test
    public void shouldGetAllDictionaryValuesFromDictionary() throws Exception {
        //given
        Long dictionaryId = dictionaryRepository.saveAndFlush(new Dictionary("type")).getId();
        Long valueId = dictionaryValueRepository.saveAndFlush(new DictionaryValue("student")).getId();
        Long value2Id = dictionaryValueRepository.saveAndFlush(new DictionaryValue("employee")).getId();
        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/dictionaries/" + dictionaryId + "/values/" + valueId))
                .andExpect(status().isOk());
        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/dictionaries/" + dictionaryId + "/values/" + value2Id))
                .andExpect(status().isOk());
        //when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .get("/api/dictionaries/" + dictionaryId + "/values"));
        //then
        resultActions
                .andExpect(jsonPath("$[0].id").value(valueId))
                .andExpect(jsonPath("$[0].name").value("student"))
                .andExpect(jsonPath("$[1].id").value(value2Id))
                .andExpect(jsonPath("$[1].name").value("employee"))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldReturnNotFoundStatusForDictionaryWithId100() throws Exception {
        //when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/api/dictionaries/100"));
        //then
        resultActions
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.timestamp", is(notNullValue())))
                .andExpect(jsonPath("$.errorCode").value("NOT_FOUND"))
                .andExpect(jsonPath("$.errorMessages", hasItem("Dictionary with id 100 not found.")));
    }

    @Test
    public void shouldReturnBadRequestStatusWhenDeleteDictionaryWhichContainsValues() throws Exception {
        //given
        Long dictionaryId = dictionaryRepository.saveAndFlush(new Dictionary("type")).getId();
        Long valueId = dictionaryValueRepository.saveAndFlush(new DictionaryValue("student")).getId();
        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/dictionaries/" + dictionaryId + "/values/" + valueId))
                .andExpect(status().isOk());
        //when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .delete("/api/dictionaries/" + dictionaryId));
        //then
        resultActions
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp", is(notNullValue())))
                .andExpect(jsonPath("$.errorCode").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.errorMessages",
                        hasItem("A dictionary with values cannot be deleted. Delete dictionary values first.")));
    }

    @Test
    public void shouldReturnNotFoundStatusWhenAddDictionaryValueThatNotExist() throws Exception {
        //given
        Long dictionaryId = dictionaryRepository.saveAndFlush(new Dictionary("type")).getId();
        //when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .post("/api/dictionaries/" + dictionaryId + "/values/100"));
        //then
        resultActions
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.timestamp", is(notNullValue())))
                .andExpect(jsonPath("$.errorCode").value("NOT_FOUND"))
                .andExpect(jsonPath("$.errorMessages", hasItem("Dictionary value with id 100 not found.")));
    }

    @Test
    public void shouldReturnNotFoundStatusWhenUpdateDictionaryNameThatNotExist() throws Exception {
        //given
        Long dictionaryId = dictionaryRepository.saveAndFlush(new Dictionary("types")).getId();
        UpdateDictionaryCommand updateDictionaryCommandForTest =
                new UpdateDictionaryCommand(dictionaryId + 1, "type");
        String jsonForTest = objectMapper.writeValueAsString(updateDictionaryCommandForTest);
        //when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.put("/api/dictionaries/" + (dictionaryId + 1))
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonForTest));
        //then
        resultActions
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.timestamp", is(notNullValue())))
                .andExpect(jsonPath("$.errorCode").value("NOT_FOUND"))
                .andExpect(jsonPath("$.errorMessages", hasItem("Dictionary with id " + (dictionaryId + 1) +
                        " not found.")));
    }

    @Test
    public void shouldReturnBadRequestStatusWhenDeleteValuesFromEmptyDictionary() throws Exception {
        //given
        Long dictionaryId = dictionaryRepository.saveAndFlush(new Dictionary("type")).getId();
        //when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .delete("/api/dictionaries/" + dictionaryId + "/values"));
        //then
        resultActions
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp", is(notNullValue())))
                .andExpect(jsonPath("$.errorCode").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.errorMessages", hasItem("Dictionary is already empty.")));
    }

    @Test
    public void shouldReturnBadRequestStatusWhenAddValueThatAlreadyExistsInAnotherDictionary() throws Exception {
        //given
        Dictionary dictionary = dictionaryRepository.saveAndFlush(new Dictionary("role"));
        Long dictionaryId = dictionary.getId();
        DictionaryValue value = dictionaryValueRepository.saveAndFlush(new DictionaryValue("tester"));
        Long valueId = value.getId();
        Long dictionary2Id = dictionaryRepository.saveAndFlush(new Dictionary("position")).getId();
        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/dictionaries/" + dictionaryId + "/values/" + valueId))
                .andExpect(status().isOk());
        //when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .post("/api/dictionaries/" + dictionary2Id + "/values/" + valueId));
        //then
        resultActions
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp", is(notNullValue())))
                .andExpect(jsonPath("$.errorCode").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.errorMessages", hasItem("Value '" + value.getName() +
                        "' already exists in '" + dictionary.getName() + "' dictionary.")));
    }

    @Test
    public void shouldReturnBadRequestStatusWhenAddValueWithNameThatAlreadyExistsInTheDictionary() throws Exception {
        //given
        Long dictionaryId = dictionaryRepository.saveAndFlush(new Dictionary("type")).getId();
        Long valueId = dictionaryValueRepository.saveAndFlush(new DictionaryValue("student")).getId();
        Long value2Id = dictionaryValueRepository.saveAndFlush(new DictionaryValue("student")).getId();
        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/dictionaries/" + dictionaryId + "/values/" + valueId))
                .andExpect(status().isOk());
        //when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .post("/api/dictionaries/" + dictionaryId + "/values/" + value2Id));
        //then
        resultActions
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp", is(notNullValue())))
                .andExpect(jsonPath("$.errorCode").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.errorMessages", hasItem("Value with the same name already exists " +
                        "in the dictionary.")));
    }

    @Test
    public void shouldReturnNotFoundStatusWhenRemoveValueThatIsNotAssignedToIndicatedDictionary() throws Exception {
        //given
        Long dictionaryId = dictionaryRepository.saveAndFlush(new Dictionary("type")).getId();
        Long valueId = dictionaryValueRepository.saveAndFlush(new DictionaryValue("student")).getId();
        DictionaryValue value2 = dictionaryValueRepository.saveAndFlush(new DictionaryValue("employee"));
        Long value2Id = value2.getId();
        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/dictionaries/" + dictionaryId + "/values/" + valueId))
                .andExpect(status().isOk());
        //when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .delete("/api/dictionaries/" + dictionaryId + "/values/" + value2Id));
        //then
        resultActions
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.timestamp", is(notNullValue())))
                .andExpect(jsonPath("$.errorCode").value("NOT_FOUND"))
                .andExpect(jsonPath("$.errorMessages", hasItem("Dictionary does not contain the '" +
                        value2 + "' value.")));
    }

    @Test
    public void shouldReturnBadRequestStatusWhenDictionaryWithProvidedNameAlreadyExists() throws Exception {
        //given
        dictionaryRepository.saveAndFlush(new Dictionary("type"));
        CreateDictionaryCommand createDictionaryCommandForTest = new CreateDictionaryCommand("type");
        String jsonForTest = objectMapper.writeValueAsString(createDictionaryCommandForTest);
        //when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post("/api/dictionaries")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonForTest));
        //then
        resultActions
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp", is(notNullValue())))
                .andExpect(jsonPath("$.errorCode").value("NAME_NOT_UNIQUE"))
                .andExpect(jsonPath("$.errorMessages", hasItem("Duplicated entry for 'name' field.")));
    }

    @AfterEach
    public void tearDown() {
        dictionaryValueRepository.deleteAllInBatch();
        dictionaryRepository.deleteAllInBatch();
    }

}