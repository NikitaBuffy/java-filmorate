package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Past;
import java.time.LocalDate;

@Getter
@Setter
@EqualsAndHashCode
@ToString
public class User {
    private int id;
    @Email
    @NotBlank
    private String email;
    @NotBlank
    private String login;
    private String name;
    @Past
    private LocalDate birthday;

    /* Пришлось не использовать @Data и создать конструктор вручную, так как из-за этого в UserController не работал сеттер
    * name, потому что у него модификатор final, а в другом случае не работали конструкторы в UserValidationTests.
    * Если добавить к name @NonNull, то не проходили заготовленные тесты в Postman. */

    public User(String email, String login, String name, LocalDate birthday) {
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = birthday;
    }
}