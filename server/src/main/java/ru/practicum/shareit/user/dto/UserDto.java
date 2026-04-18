package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(makeFinal = true)
public class UserDto {

	Long id;

	String name;

	String email;
}
