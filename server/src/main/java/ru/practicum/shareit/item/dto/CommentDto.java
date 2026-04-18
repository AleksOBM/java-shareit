package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CommentDto {

	Long id;

	Long itemId;

	String authorName;

	@JsonProperty("created")
	LocalDateTime createdDate;

	String text;
}
