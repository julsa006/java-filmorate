package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
class FilmControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void testCreateValidationSuccess() throws Exception {
        checkCreateStatusOk(filmRequestBody(null, "name", "description", "2023-01-01", 10));
    }

    @Test
    void testCreateValidationFailedEmptyName() throws Exception {
        checkCreateStatusFailed(filmRequestBody(null, "", "description", "2023-01-01", 10));
        checkCreateStatusFailed(filmRequestBody(null, "     ", "description", "2023-01-01", 10));
        checkCreateStatusFailed(filmRequestBody(null, null, "description", "2023-01-01", 10));
    }

    @Test
    void testCreateValidationDescription() throws Exception {
        String longDescription = "A".repeat(201);
        checkCreateStatusFailed(filmRequestBody(null, "name", longDescription, "2023-01-01", 10));
    }

    @Test
    void testCreateValidationReleaseDate() throws Exception {
        checkCreateStatusOk(filmRequestBody(null, "name", "description", "1895-12-28", 10));
        checkCreateStatusFailed(filmRequestBody(null, "name", "description", "1895-12-27", 10));
        checkCreateStatusFailed(filmRequestBody(null, "name", "description", "1895-28-12", 10));
        checkCreateStatusFailed(filmRequestBody(null, "name", "description", null, 10));
    }

    @Test
    void testCreateValidationDuration() throws Exception {
        checkCreateStatusFailed(filmRequestBody(null, "name", "description", "2023-12-28", 0));
        checkCreateStatusFailed(filmRequestBody(null, "name", "description", "2023-12-28", -10));
        checkCreateStatusFailed(filmRequestBody(null, "name", "description", "2023-12-28", null));
    }

    private void checkCreateStatus(String request, ResultMatcher rm) throws Exception {
        mockMvc.perform(post("/films").contentType(MediaType.APPLICATION_JSON).content(request))
                .andDo(print())
                .andExpect(rm);
    }

    private void checkCreateStatusOk(String request) throws Exception {
        checkCreateStatus(request, status().isOk());
    }

    private void checkCreateStatusFailed(String request) throws Exception {
        checkCreateStatus(request, status().isBadRequest());
    }

    String filmRequestBody(Integer id, String name, String description, String releaseDate, Integer duration) {
        List<String> props = new ArrayList<>();

        if (id != null) props.add(String.format("\"id\": %d", id));
        if (duration != null) props.add(String.format("\"duration\": %d", duration));
        if (name != null) props.add(String.format("\"name\": \"%s\"", name));
        if (description != null) props.add(String.format("\"description\": \"%s\"", description));
        if (releaseDate != null) props.add(String.format("\"releaseDate\": \"%s\"", releaseDate));

        return String.format("{%s}", String.join(",", props));
    }

}