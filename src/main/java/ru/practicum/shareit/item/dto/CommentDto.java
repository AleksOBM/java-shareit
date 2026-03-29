package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@NotNull
public class CommentDto {

	Long id;

	Long itemId;

	String authorName;

	@JsonProperty("created")
	LocalDateTime createdDate;

	@NotBlank
	String text;
}
