package ru.practicum.shareit.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class UserDto {
    @NotBlank
    private String name;

    @NotBlank @Email
    private String email;
}
