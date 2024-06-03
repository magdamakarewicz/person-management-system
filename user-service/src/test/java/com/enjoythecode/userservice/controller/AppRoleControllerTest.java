package com.enjoythecode.userservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import com.enjoythecode.userservice.UserServiceApplication;
import com.enjoythecode.userservice.command.CreateRoleCommand;
import com.enjoythecode.userservice.command.UpdateRoleCommand;
import com.enjoythecode.userservice.model.AppRole;
import com.enjoythecode.userservice.model.AppUser;
import com.enjoythecode.userservice.repository.AppRoleRepository;
import com.enjoythecode.userservice.repository.AppUserRepository;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = UserServiceApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AppRoleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private AppRoleRepository appRoleRepository;

    @BeforeEach
    public void setUp() {
        appRoleRepository.deleteAllInBatch();
        appUserRepository.deleteAllInBatch();
    }

    @Test
    public void shouldAddAppRole() throws Exception {
        //given
        CreateRoleCommand createRoleCommandForTest = new CreateRoleCommand("ROLE_ADMIN");
        String jsonForTest = objectMapper.writeValueAsString(createRoleCommandForTest);
        //when
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/roles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonForTest))
                .andExpect(status().isOk())
                .andReturn();
        String responseContent = result.getResponse().getContentAsString();
        AppRole appRole = objectMapper.readValue(responseContent, AppRole.class);
        Long addedRoleId = appRole.getId();
        //then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/roles/" + addedRoleId))
                .andExpect(jsonPath("$.id").value(addedRoleId))
                .andExpect(jsonPath("$.name").value("ROLE_ADMIN"))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldDeleteAppRoleById() throws Exception {
        //given
        Long appRoleId = appRoleRepository.saveAndFlush(new AppRole("ROLE_ADMIN")).getId();
        //when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.delete("/api/roles/" + appRoleId));
        //then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("Role with id " + appRoleId + " deleted."));
    }

    @Test
    public void shouldGetAppRoleById() throws Exception {
        //given
        Long appRoleId = appRoleRepository.saveAndFlush(new AppRole("ROLE_ADMIN")).getId();
        //when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/api/roles/" + appRoleId));
        //then
        resultActions
                .andExpect(jsonPath("$.id").value(appRoleId))
                .andExpect(jsonPath("$.name").value("ROLE_ADMIN"))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldGetAllAppRoles() throws Exception {
        //given
        List<AppRole> appRoleListForTest = List.of(
                new AppRole("ROLE_ADMIN"),
                new AppRole("ROLE_IMPORTER")
        );
        appRoleRepository.saveAllAndFlush(appRoleListForTest);
        //when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/api/roles"));
        //then
        resultActions
                .andExpect(jsonPath("$[0].id").value(notNullValue()))
                .andExpect(jsonPath("$[0].name").value("ROLE_ADMIN"))
                .andExpect(jsonPath("$[1].id").value(notNullValue()))
                .andExpect(jsonPath("$[1].name").value("ROLE_IMPORTER"))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldUpdateAppRoleName() throws Exception {
        //given
        Long appRoleId = appRoleRepository.saveAndFlush(new AppRole("ROLE_DAMIN")).getId();
        UpdateRoleCommand updateRoleCommandForTest = new UpdateRoleCommand("ROLE_ADMIN");
        String jsonForTest = objectMapper.writeValueAsString(updateRoleCommandForTest);
        //when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.put("/api/roles/" + appRoleId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonForTest));
        //then
        resultActions
                .andExpect(jsonPath("$.id").value(appRoleId))
                .andExpect(jsonPath("$.name").value("ROLE_ADMIN"))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldGetUsersAssignedToIndicatedAppRole() throws Exception {
        //given
        Long appRoleId = appRoleRepository.saveAndFlush(new AppRole("ROLE_USER")).getId();
        Long userId = appUserRepository.saveAndFlush(new AppUser("admin", "admin")).getId();
        Long user2Id = appUserRepository.saveAndFlush(new AppUser("importer", "importer")).getId();
        mockMvc.perform(MockMvcRequestBuilders.post("/api/users/" + userId + "/roles/" + appRoleId))
                .andExpect(status().isOk());
        mockMvc.perform(MockMvcRequestBuilders.post("/api/users/" + user2Id + "/roles/" + appRoleId))
                .andExpect(status().isOk());
        //when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .get("/api/roles/" + appRoleId + "/users"));
        //then
        resultActions
                .andExpect(jsonPath("$[0].id").value(notNullValue()))
                .andExpect(jsonPath("$[0].username").value("admin"))
                .andExpect(jsonPath("$[1].id").value(notNullValue()))
                .andExpect(jsonPath("$[1].username").value("importer"))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldReturnNotFoundStatusForAppRoleWithId100() throws Exception {
        //when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/api/roles/100"));
        //then
        resultActions
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.timestamp", is(notNullValue())))
                .andExpect(jsonPath("$.errorCode").value("NOT_FOUND"))
                .andExpect(jsonPath("$.errorMessages", hasItem("Role with id 100 not found.")));
    }

    @Test
    public void shouldReturnBadRequestStatusWhenAppRoleWithProvidedNameAlreadyExists() throws Exception {
        //given
        appRoleRepository.saveAndFlush(new AppRole("ROLE_ADMIN"));
        CreateRoleCommand createRoleCommandForTest = new CreateRoleCommand("ROLE_ADMIN");
        String jsonForTest = objectMapper.writeValueAsString(createRoleCommandForTest);
        //when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post("/api/roles")
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
        appRoleRepository.deleteAllInBatch();
        appUserRepository.deleteAllInBatch();
    }

}