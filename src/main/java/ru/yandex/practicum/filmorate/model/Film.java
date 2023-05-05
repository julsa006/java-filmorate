package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Value;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Value
@Builder(toBuilder = true)
public class Film {
    Integer id;

    @NotBlank(message = "Name cannot be blank")
    String name;

    @Size(max = 200, message
            = "Description must be no more than 200 characters")
    String description;

    @NotNull
    LocalDate releaseDate;

    @NotNull
    @Positive(message
            = "Duration must be positive")
    Integer duration;
}
