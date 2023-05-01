package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.bind.MethodArgumentNotValidException;
import ru.yandex.practicum.filmorate.exception.ValidationException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void testCreateValidationSuccess() throws Exception {
        assertCreateStatusOk(userRequestBody(null, "login", "name", "email@test.com", "1995-01-01"));
        assertCreateStatusOk(userRequestBody(null, "login", null, "email@test.com", "1995-01-01"));
    }

    @Test
    void testCreateValidationEmail() throws Exception {
        assertCreateStatusFailed(userRequestBody(null, "login", null, "emailtest.com", "1995-01-01"));
        assertCreateStatusFailed(userRequestBody(null, "login", null, "", "1995-01-01"));
        assertCreateStatusFailed(userRequestBody(null, "login", null, "   ", "1995-01-01"));
        assertCreateStatusFailed(userRequestBody(null, "login", null, null, "1995-01-01"));
    }

    @Test
    void testCreateValidationLogin() throws Exception {
        assertCreateStatusFailed(userRequestBody(null, "", null, "email@test.com", "1995-01-01"));
        assertCreateStatusFailed(userRequestBody(null, "   ", null, "email@test.com", "1995-01-01"));
        assertCreateStatusFailed(userRequestBody(null, "hello kitty", null, "email@test.com", "1995-01-01"));
        assertCreateStatusFailed(userRequestBody(null, null, null, "email@test.com", "1995-01-01"));
    }

    @Test
    void testCreateValidationBirthday() throws Exception {
        String dayInFuture = LocalDate.now().plusDays(3).format(DateTimeFormatter.ISO_DATE);
        assertCreateStatusFailed(userRequestBody(null, "login", null, "email@test.com", dayInFuture));
        assertCreateStatusFailed(userRequestBody(null, "login", null, "email@test.com", ""));
        assertCreateStatusFailed(userRequestBody(null, "login", null, "email@test.com", null));
    }

    private ResultActions performCreate(String request) throws Exception {
        return mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andDo(print());
    }

    private void assertCreateStatusOk(String request) throws Exception {
        performCreate(request).andExpect(status().isOk());
    }

    private void assertCreateStatusFailed(String request) throws Exception {
        performCreate(request)
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(
                        result.getResolvedException() instanceof ValidationException ||
                                result.getResolvedException() instanceof MethodArgumentNotValidException)
                );
    }

    private String userRequestBody(Integer id, String login, String name, String email, String birthday) {
        List<String> props = new ArrayList<>();

        if (id != null) props.add(String.format("\"id\": %d", id));
        if (login != null) props.add(String.format("\"login\": \"%s\"", login));
        if (name != null) props.add(String.format("\"name\": \"%s\"", name));
        if (email != null) props.add(String.format("\"email\": \"%s\"", email));
        if (birthday != null) props.add(String.format("\"birthday\": \"%s\"", birthday));

        return String.format("{%s}", String.join(",", props));
    }
}