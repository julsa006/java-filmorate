package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.bind.MethodArgumentNotValidException;
import ru.yandex.practicum.filmorate.exception.ValidationException;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void testCreateValidationSuccess() throws Exception {
        assertCreateStatusOk(filmRequestBody(null, "name", "description", "2023-01-01", 10));
    }

    @Test
    void testCreateValidationFailedEmptyName() throws Exception {
        assertCreateInvalidArguments(filmRequestBody(null, "", "description", "2023-01-01", 10));
        assertCreateInvalidArguments(filmRequestBody(null, "     ", "description", "2023-01-01", 10));
        assertCreateInvalidArguments(filmRequestBody(null, null, "description", "2023-01-01", 10));
    }

    @Test
    void testCreateValidationDescription() throws Exception {
        String longDescription = "A".repeat(201);
        assertCreateInvalidArguments(filmRequestBody(null, "name", longDescription, "2023-01-01", 10));
    }

    @Test
    void testCreateValidationReleaseDate() throws Exception {
        assertCreateStatusOk(filmRequestBody(null, "name", "description", "1895-12-28", 10));
        assertCreateInvalidArguments(filmRequestBody(null, "name", "description", "1895-12-27", 10));
        assertCreateInvalidArguments(filmRequestBody(null, "name", "description", null, 10));
    }

    @Test
    void testCreateValidationDuration() throws Exception {
        assertCreateInvalidArguments(filmRequestBody(null, "name", "description", "2023-12-28", 0));
        assertCreateInvalidArguments(filmRequestBody(null, "name", "description", "2023-12-28", -10));
        assertCreateInvalidArguments(filmRequestBody(null, "name", "description", "2023-12-28", null));
    }

    private ResultActions performCreate(String request) throws Exception {
        return mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andDo(print());
    }

    private void assertCreateStatusOk(String request) throws Exception {
        performCreate(request).andExpect(status().isOk());
    }

    private void assertCreateInvalidArguments(String request) throws Exception {
        performCreate(request)
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(
                        result.getResolvedException() instanceof ValidationException ||
                                result.getResolvedException() instanceof MethodArgumentNotValidException)
                );
    }

    private String filmRequestBody(Integer id, String name, String description, String releaseDate, Integer duration) {
        List<String> props = new ArrayList<>();

        if (id != null) props.add(String.format("\"id\": %d", id));
        if (duration != null) props.add(String.format("\"duration\": %d", duration));
        if (name != null) props.add(String.format("\"name\": \"%s\"", name));
        if (description != null) props.add(String.format("\"description\": \"%s\"", description));
        if (releaseDate != null) props.add(String.format("\"releaseDate\": \"%s\"", releaseDate));
        props.add(String.format("\"mpa\": %s", "{ \"id\": 1}"));
        return String.format("{%s}", String.join(",", props));
    }

}