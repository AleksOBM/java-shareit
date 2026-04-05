package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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

	@Size(max = 500)
	String text;
}
