package com.enjoythecode.personservice.services;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import com.enjoythecode.personservice.PersonServiceApplication;
import com.enjoythecode.personservice.api.DictionaryServiceClient;
import com.enjoythecode.personservice.dto.DictionaryValueSimpleDto;
import com.enjoythecode.personservice.repository.PersonRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = PersonServiceApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AsyncDataImportFromCsvServiceTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PersonRepository personRepository;

    @MockBean
    private DictionaryServiceClient dictionaryServiceClient;

    @BeforeEach
    public void setUp() {
        personRepository.deleteAllInBatch();
    }

    @Test
    public void shouldPerformAsyncImportAndAllowImportStatusCheckWhileImportIsInProgress() throws Exception {
        // given
        String fileContent = "type,first_name,last_name,pesel,height,weight,email,param1,param2,param3,param4" +
                "\nemployee,John,Doe,12345678911,180,70,johndoe@test.com,2021-01-01,manager,40000";
        MockMultipartFile file = new MockMultipartFile(
                "file", "test-peopleToImport.csv", "text/csv", fileContent.getBytes()
        );
        DictionaryValueSimpleDto employee = new DictionaryValueSimpleDto(1L, "employee");
        DictionaryValueSimpleDto manager = new DictionaryValueSimpleDto(2L, "manager");
        Mockito.when(dictionaryServiceClient
                .getDictionaryValueByDictionaryIdAndName(1L, employee.getName())).thenReturn(employee);
        Mockito.when(dictionaryServiceClient
                .getDictionaryValueByDictionaryIdAndName(2L, manager.getName())).thenReturn(manager);
        //when
        MvcResult asyncResult = mockMvc.perform(MockMvcRequestBuilders
                .multipart("/api/people/import")
                .file(file))
                .andExpect(status().isOk())
                .andExpect(request().asyncStarted())
                .andReturn();
        //then - check import status while importing
        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/people/import/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("Import is in progress."))
                .andExpect(jsonPath("$.startTime").isNotEmpty())
                .andExpect(jsonPath("$.endTime").isEmpty())
                .andExpect(jsonPath("$.processedRows").value(0));
        //when -  manually perform an async dispatch
        mockMvc.perform(asyncDispatch(asyncResult))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("Data import has started. " +
                        "Check status endpoint /api/people/import/status for progress."));
        //then - check import status after import
        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/people/import/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("Import completed."))
                .andExpect(jsonPath("$.startTime").isNotEmpty())
                .andExpect(jsonPath("$.endTime").isNotEmpty())
                .andExpect(jsonPath("$.processedRows").value(1));
    }

    @AfterEach
    public void tearDown() {
        personRepository.deleteAllInBatch();
    }

}