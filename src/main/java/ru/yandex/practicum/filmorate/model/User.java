package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDate;

@Value
@Builder(toBuilder = true)
@AllArgsConstructor
public class User {

    Integer id;

    @NotNull
    @NotBlank
    @Email
    String email;

    @NotBlank(message = "Name cannot be blank")
    String login;

    String name;

    @NotNull
    @PastOrPresent(message = "Birthday cannot be in the future")
    LocalDate birthday;
}
