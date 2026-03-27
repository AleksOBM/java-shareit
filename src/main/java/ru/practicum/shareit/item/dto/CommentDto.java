package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

import org.springframework.lang.NonNull;
import ru.practicum.shareit.item.model.Comment;

import java.time.LocalDateTime;

@Data
@Builder
public class CommentDto {

	Long id;

	Long itemId;

	String authorName;

	@JsonProperty("created")
	LocalDateTime createdDate;

	@NotBlank
	String text;

	public static CommentDto from(@NonNull Comment comment) {
		return CommentDto.builder()
				.id(comment.getId())
				.itemId(comment.getItem().getId())
				.authorName(comment.getAuthor().getName())
				.createdDate(comment.getCreatedDate())
				.text(comment.getText())
				.build();
	}
}
