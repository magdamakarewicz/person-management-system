package com.enjoythecode.userservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import com.enjoythecode.userservice.UserServiceApplication;
import com.enjoythecode.userservice.command.CreateUserCommand;
import com.enjoythecode.userservice.command.UpdateUserPasswordCommand;
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
class AppUserControllerTest {

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
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void shouldAddAppUser() throws Exception {
        //given
        CreateUserCommand createUserCommandForTest = new CreateUserCommand("admin", "Admin123");
        String jsonForTest = objectMapper.writeValueAsString(createUserCommandForTest);
        //when
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonForTest))
                .andExpect(status().isOk())
                .andReturn();
        String responseContent = result.getResponse().getContentAsString();
        AppUser appUser = objectMapper.readValue(responseContent, AppUser.class);
        Long addedUserId = appUser.getId();
        //then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/users/" + addedUserId))
                .andExpect(jsonPath("$.id").value(addedUserId))
                .andExpect(jsonPath("$.username").value("admin"))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldDeleteAppUserById() throws Exception {
        //given
        Long appUserId = appUserRepository.saveAndFlush(new AppUser("admin", "Admin123")).getId();
        //when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.delete("/api/users/" + appUserId));
        //then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("User with id " + appUserId + " deleted."));
    }

    @Test
    public void shouldGetAppUserById() throws Exception {
        //given
        Long appUserId = appUserRepository.saveAndFlush(new AppUser("admin", "Admin123")).getId();
        //when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/api/users/" + appUserId));
        //then
        resultActions
                .andExpect(jsonPath("$.id").value(appUserId))
                .andExpect(jsonPath("$.username").value("admin"))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldGetAllAppUsers() throws Exception {
        //given
        List<AppUser> appUserListForTest = List.of(
                new AppUser("admin", "Admin123"),
                new AppUser("importer", "Importer123")
        );
        appUserRepository.saveAllAndFlush(appUserListForTest);
        //when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/api/users"));
        //then
        resultActions
                .andExpect(jsonPath("$[0].id").value(notNullValue()))
                .andExpect(jsonPath("$[0].username").value("admin"))
                .andExpect(jsonPath("$[1].id").value(notNullValue()))
                .andExpect(jsonPath("$[1].username").value("importer"))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldUpdateAppUserPassword() throws Exception {
        //given
        Long appUserId = appUserRepository.saveAndFlush(new AppUser("admin", "Admin123")).getId();
        UpdateUserPasswordCommand updateUserPasswordCommandForTest = new UpdateUserPasswordCommand("adMin321");
        String jsonForTest = objectMapper.writeValueAsString(updateUserPasswordCommandForTest);
        //when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.put("/api/users/" + appUserId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonForTest));
        //then
        resultActions
                .andExpect(jsonPath("$.id").value(appUserId))
                .andExpect(jsonPath("$.username").value("admin"))
                .andExpect(jsonPath("$.roles").isEmpty())
                .andExpect(status().isOk());
    }

    @Test
    public void shouldGetAppUserWithRolesByUsername() throws Exception {
        //given
        AppUser appUser = appUserRepository.saveAndFlush(new AppUser("admin", "Admin123"));
        String username = appUser.getUsername();
        Long appUserId = appUser.getId();
        Long appRoleId = appRoleRepository.saveAndFlush(new AppRole("ROLE_ADMIN")).getId();
        Long appRole2Id = appRoleRepository.saveAndFlush(new AppRole("ROLE_USER")).getId();
        mockMvc.perform(MockMvcRequestBuilders.post("/api/users/" + appUserId + "/roles/" + appRoleId))
                .andExpect(status().isOk());
        mockMvc.perform(MockMvcRequestBuilders.post("/api/users/" + appUserId + "/roles/" + appRole2Id))
                .andExpect(status().isOk());
        //when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .get("/api/users/byUsername?username=" + username));
        //then
        resultActions
                .andExpect(jsonPath("$.id").value(appUserId))
                .andExpect(jsonPath("$.username").value(username))
                .andExpect(jsonPath("$.roles", Matchers.hasSize(2)))
                .andExpect(jsonPath("$.roles", Matchers.containsInAnyOrder("ROLE_ADMIN", "ROLE_USER")))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    public void shouldAddRoleToUser() throws Exception {
        //given
        Long appUserId = appUserRepository.saveAndFlush(new AppUser("admin", "Admin123")).getId();
        Long appRoleId = appRoleRepository.saveAndFlush(new AppRole("ROLE_ADMIN")).getId();
        //when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .post("/api/users/" + appUserId + "/roles/" + appRoleId));
        //then
        resultActions
                .andExpect(jsonPath("$.id").value(appUserId))
                .andExpect(jsonPath("$.username").value("admin"))
                .andExpect(jsonPath("$.roles").value("ROLE_ADMIN"))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldRemoveRoleFromUser() throws Exception {
        //given
        Long appUserId = appUserRepository.saveAndFlush(new AppUser("admin", "Admin123")).getId();
        Long appRoleId = appRoleRepository.saveAndFlush(new AppRole("ROLE_ADMIN")).getId();
        Long appRole2Id = appRoleRepository.saveAndFlush(new AppRole("ROLE_IMPORTER")).getId();
        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/users/" + appUserId + "/roles/" + appRoleId))
                .andExpect(jsonPath("$.id").value(appUserId))
                .andExpect(jsonPath("$.username").value("admin"))
                .andExpect(jsonPath("$.roles").value("ROLE_ADMIN"))
                .andExpect(status().isOk());
        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/users/" + appUserId + "/roles/" + appRole2Id))
                .andExpect(jsonPath("$.id").value(appUserId))
                .andExpect(jsonPath("$.username").value("admin"))
                .andExpect(jsonPath("$.roles",
                        Matchers.containsInAnyOrder("ROLE_ADMIN", "ROLE_IMPORTER")))
                .andExpect(status().isOk());
        //when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .delete("/api/users/" + appUserId + "/roles/" + appRoleId));
        //then
        resultActions
                .andExpect(jsonPath("$.id").value(appUserId))
                .andExpect(jsonPath("$.username").value("admin"))
                .andExpect(jsonPath("$.roles").value("ROLE_IMPORTER"))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldReturnNotFoundStatusForNotExistingUsername() throws Exception {
        //given
        appUserRepository.saveAndFlush(new AppUser("admin", "Admin123"));
        String username = "admin1";
        //when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .get("/api/users/byUsername?username=" + username));
        //then
        resultActions
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.timestamp", is(notNullValue())))
                .andExpect(jsonPath("$.errorCode").value("NOT_FOUND"))
                .andExpect(jsonPath("$.errorMessages", hasItem("User with username '" + username + "' not found.")));
    }

    @Test
    public void shouldReturnNotFoundStatusForAppUserWithId100() throws Exception {
        //when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/api/users/100"));
        //then
        resultActions
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.timestamp", is(notNullValue())))
                .andExpect(jsonPath("$.errorCode").value("NOT_FOUND"))
                .andExpect(jsonPath("$.errorMessages", hasItem("User with id 100 not found.")));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void shouldReturnBadRequestStatusWhenAppUserWithProvidedUsernameAlreadyExists() throws Exception {
        //given
        appUserRepository.saveAndFlush(new AppUser("admin", "Admin123"));
        CreateUserCommand createUserCommandForTest = new CreateUserCommand("admin", "ADMIN321");
        String jsonForTest = objectMapper.writeValueAsString(createUserCommandForTest);
        //when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonForTest));
        //then
        resultActions
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp", is(notNullValue())))
                .andExpect(jsonPath("$.errorCode").value("USERNAME_NOT_UNIQUE"))
                .andExpect(jsonPath("$.errorMessages", hasItem("Duplicated entry for 'username' field.")));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void shouldReturnBadRequestStatusWhenAppUserPasswordNotValid() throws Exception {
        //given
        CreateUserCommand createUserCommandForTest = new CreateUserCommand("admin", "admin");
        String jsonForTest = objectMapper.writeValueAsString(createUserCommandForTest);
        //when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonForTest));
        //then
        resultActions
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp", is(notNullValue())))
                .andExpect(jsonPath("$.errorCode").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.errorMessages", hasItem("field: password / rejectedValue: 'admin' " +
                        "/ message: Password cannot be null; should contain at least 7 digits, " +
                        "at least one upper case and one digit")));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void shouldReturnBadRequestStatusWhenAddRoleThatUserAlreadyHas() throws Exception {
        //given
        Long appUserId = appUserRepository.saveAndFlush(new AppUser("admin", "Admin123")).getId();
        AppRole appRole = appRoleRepository.saveAndFlush(new AppRole("ROLE_ADMIN"));
        Long appRoleId = appRole.getId();
        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/users/" + appUserId + "/roles/" + appRoleId))
                .andExpect(status().isOk());
        //when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .post("/api/users/" + appUserId + "/roles/" + appRoleId));
        //then
        resultActions
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp", is(notNullValue())))
                .andExpect(jsonPath("$.errorCode").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.errorMessages", hasItem("Role '" + appRole.getName() +
                        "' is already assigned to the user.")));
    }

    @Test
    public void shouldReturnNotFoundStatusWhenRemoveRoleThatIsNotAssignedToIndicatedUser() throws Exception {
        //given
        Long appUserId = appUserRepository.saveAndFlush(new AppUser("admin", "Admin123")).getId();
        AppRole appRole = appRoleRepository.saveAndFlush(new AppRole("ROLE_ADMIN"));
        Long appRoleId = appRole.getId();
        //when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .delete("/api/users/" + appUserId + "/roles/" + appRoleId));
        //then
        resultActions
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.timestamp", is(notNullValue())))
                .andExpect(jsonPath("$.errorCode").value("NOT_FOUND"))
                .andExpect(jsonPath("$.errorMessages", hasItem("User does not have the '" +
                        appRole.getName() + "' role.")));
    }

    @AfterEach
    public void tearDown() {
        appRoleRepository.deleteAllInBatch();
        appUserRepository.deleteAllInBatch();
    }

}