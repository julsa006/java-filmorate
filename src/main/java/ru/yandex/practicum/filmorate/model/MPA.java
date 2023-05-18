package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Value;

import javax.validation.constraints.NotNull;

@Value
@AllArgsConstructor
public class MPA {
    @NotNull
    Integer id;

    String name;
}
