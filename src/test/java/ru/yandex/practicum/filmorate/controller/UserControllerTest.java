package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void testCreateValidationSuccess() throws Exception {
        checkCreateStatusOk(userRequestBody(null, "login", "name", "email@test.com", "1995-01-01"));
        checkCreateStatusOk(userRequestBody(null, "login", null, "email@test.com", "1995-01-01"));
    }

    @Test
    void testCreateValidationEmail() throws Exception {
        checkCreateStatusFailed(userRequestBody(null, "login", null, "emailtest.com", "1995-01-01"));
        checkCreateStatusFailed(userRequestBody(null, "login", null, "", "1995-01-01"));
        checkCreateStatusFailed(userRequestBody(null, "login", null, "   ", "1995-01-01"));
        checkCreateStatusFailed(userRequestBody(null, "login", null, null, "1995-01-01"));
    }

    @Test
    void testCreateValidationLogin() throws Exception {
        checkCreateStatusFailed(userRequestBody(null, "", null, "email@test.com", "1995-01-01"));
        checkCreateStatusFailed(userRequestBody(null, "   ", null, "email@test.com", "1995-01-01"));
        checkCreateStatusFailed(userRequestBody(null, "hello kitty", null, "email@test.com", "1995-01-01"));
        checkCreateStatusFailed(userRequestBody(null, null, null, "email@test.com", "1995-01-01"));
    }

    @Test
    void testCreateValidationBirthday() throws Exception {
        String dayInFuture = LocalDate.now().plusDays(3).format(DateTimeFormatter.ISO_DATE);
        checkCreateStatusFailed(userRequestBody(null, "login", null, "email@test.com", dayInFuture));
        checkCreateStatusFailed(userRequestBody(null, "login", null, "email@test.com", ""));
        checkCreateStatusFailed(userRequestBody(null, "login", null, "email@test.com", null));
    }

    private void checkCreateStatus(String request, ResultMatcher rm) throws Exception {
        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(request))
                .andDo(print())
                .andExpect(rm);
    }

    private void checkCreateStatusOk(String request) throws Exception {
        checkCreateStatus(request, status().isOk());
    }

    private void checkCreateStatusFailed(String request) throws Exception {
        checkCreateStatus(request, status().isBadRequest());
    }

    String userRequestBody(Integer id, String login, String name, String email, String birthday) {
        List<String> props = new ArrayList<>();

        if (id != null) props.add(String.format("\"id\": %d", id));
        if (login != null) props.add(String.format("\"login\": \"%s\"", login));
        if (name != null) props.add(String.format("\"name\": \"%s\"", name));
        if (email != null) props.add(String.format("\"email\": \"%s\"", email));
        if (birthday != null) props.add(String.format("\"birthday\": \"%s\"", birthday));

        return String.format("{%s}", String.join(",", props));
    }
}