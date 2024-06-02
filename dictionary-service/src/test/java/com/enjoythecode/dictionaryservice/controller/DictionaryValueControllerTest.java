package com.enjoythecode.dictionaryservice.controller;

import com.enjoythecode.dictionaryservice.DictionaryServiceApplication;
import com.enjoythecode.dictionaryservice.command.CreateDictionaryValueCommand;
import com.enjoythecode.dictionaryservice.command.UpdateDictionaryValueCommand;
import com.enjoythecode.dictionaryservice.model.Dictionary;
import com.enjoythecode.dictionaryservice.model.DictionaryValue;
import com.enjoythecode.dictionaryservice.repository.DictionaryRepository;
import com.enjoythecode.dictionaryservice.repository.DictionaryValueRepository;
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

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = DictionaryServiceApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class DictionaryValueControllerTest {

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
    public void shouldAddDictionaryValue() throws Exception {
        //given
        CreateDictionaryValueCommand createDictionaryValueCommandForTest = new CreateDictionaryValueCommand("student");
        String jsonForTest = objectMapper.writeValueAsString(createDictionaryValueCommandForTest);
        //when
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/dictionaryvalues")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonForTest))
                .andExpect(status().isOk())
                .andReturn();
        String responseContent = result.getResponse().getContentAsString();
        DictionaryValue dictionaryValue = objectMapper.readValue(responseContent, DictionaryValue.class);
        Long addedDictionaryValueId = dictionaryValue.getId();
        //then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/dictionaryvalues/" + addedDictionaryValueId))
                .andExpect(jsonPath("$.id").value(addedDictionaryValueId))
                .andExpect(jsonPath("$.name").value("student"))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldDeleteDictionaryValueById() throws Exception {
        //given
        Long dictionaryValueId = dictionaryValueRepository.saveAndFlush(new DictionaryValue("student")).getId();
        //when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.delete("/api/dictionaryvalues/" +
                dictionaryValueId));
        //then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("Dictionary value with id " +
                        dictionaryValueId + " deleted."));
    }

    @Test
    public void shouldGetDictionaryValueById() throws Exception {
        //given
        Long dictionaryValueId = dictionaryValueRepository.saveAndFlush(new DictionaryValue("student")).getId();
        //when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/api/dictionaryvalues/" +
                dictionaryValueId));
        //then
        resultActions
                .andExpect(jsonPath("$.id").value(dictionaryValueId))
                .andExpect(jsonPath("$.name").value("student"))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldGetAllDictionaryValues() throws Exception {
        //given
        List<DictionaryValue> dictionaryValuesListForTest = List.of(
                new DictionaryValue("student"),
                new DictionaryValue("employee")
        );
        dictionaryValueRepository.saveAllAndFlush(dictionaryValuesListForTest);
        //when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/api/dictionaryvalues"));
        //then
        resultActions
                .andExpect(jsonPath("$[0].id").value(notNullValue()))
                .andExpect(jsonPath("$[0].name").value("student"))
                .andExpect(jsonPath("$[1].id").value(notNullValue()))
                .andExpect(jsonPath("$[1].name").value("employee"))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldUpdateDictionaryValueName() throws Exception {
        //given
        Long dictionaryValueId = dictionaryValueRepository.saveAndFlush(new DictionaryValue("student")).getId();
        UpdateDictionaryValueCommand updateDictionaryValueCommandForTest = new UpdateDictionaryValueCommand(
                dictionaryValueId, "employee");
        String jsonForTest = objectMapper.writeValueAsString(updateDictionaryValueCommandForTest);
        //when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.put("/api/dictionaryvalues/" + dictionaryValueId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonForTest));
        //then
        resultActions
                .andExpect(jsonPath("$.id").value(dictionaryValueId))
                .andExpect(jsonPath("$.name").value("employee"))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldDeleteUnassignedDictionaryValues() throws Exception {
        //given
        Long dictionaryId = dictionaryRepository.saveAndFlush(new Dictionary("type")).getId();
        Long valueId = dictionaryValueRepository.saveAndFlush(new DictionaryValue("student")).getId();
        dictionaryValueRepository.saveAndFlush(new DictionaryValue("employee"));
        dictionaryValueRepository.saveAndFlush(new DictionaryValue("retiree"));
        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/dictionaries/" + dictionaryId + "/values/" + valueId))
                .andExpect(status().isOk());
        //when
        mockMvc.perform(MockMvcRequestBuilders.get("/api/dictionaryvalues"))
                .andExpect(jsonPath("$", Matchers.hasSize(3)));
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .delete("/api/dictionaryvalues/unassigned"));
        //then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("Unassigned dictionary values deleted."));
        mockMvc.perform(MockMvcRequestBuilders.get("/api/dictionaryvalues"))
                .andExpect(jsonPath("$", Matchers.hasSize(1)));
    }

    @Test
    public void shouldReturnNotFoundStatusForDictionaryValueWithId100() throws Exception {
        //when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/api/dictionaryvalues/100"));
        //then
        resultActions
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.timestamp", is(notNullValue())))
                .andExpect(jsonPath("$.errorCode").value("NOT_FOUND"))
                .andExpect(jsonPath("$.errorMessages", hasItem("Dictionary value with id 100 not found.")));
    }

    @Test
    public void shouldReturnNotFoundStatusWhenGetDictionaryValueThatNotExistInDictionary() throws Exception {
        //given
        Long dictionaryId = dictionaryRepository.saveAndFlush(new Dictionary("type")).getId();
        String valueName = dictionaryValueRepository.saveAndFlush(new DictionaryValue("student")).getName();
        //when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .get("/api/dictionaryvalues/" + dictionaryId + "/value?name=" + valueName));
        //then
        resultActions
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.timestamp", is(notNullValue())))
                .andExpect(jsonPath("$.errorCode").value("NOT_FOUND"))
                .andExpect(jsonPath("$.errorMessages", hasItem("Dictionary value '" + valueName +
                        "' not found in the dictionary with id " + dictionaryId)));
    }

    @Test
    public void shouldReturnNotFoundStatusWhenUpdateDictionaryValueThatNotExist() throws Exception {
        //given
        Long dictionaryValueId = dictionaryValueRepository.saveAndFlush(new DictionaryValue("student")).getId();
        UpdateDictionaryValueCommand updateDictionaryValueCommandForTest = new UpdateDictionaryValueCommand(
                dictionaryValueId + 1, "employee");
        String jsonForTest = objectMapper.writeValueAsString(updateDictionaryValueCommandForTest);
        //when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .put("/api/dictionaryvalues/" + (dictionaryValueId + 1))
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonForTest));
        //then
        resultActions
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.timestamp", is(notNullValue())))
                .andExpect(jsonPath("$.errorCode").value("NOT_FOUND"))
                .andExpect(jsonPath("$.errorMessages", hasItem("Dictionary value with id " +
                        (dictionaryValueId + 1) + " not found.")));
    }

    @AfterEach
    public void tearDown() {
        dictionaryValueRepository.deleteAllInBatch();
        dictionaryRepository.deleteAllInBatch();
    }

}