package ru.practicum.shareit.item.dto;

import org.springframework.lang.NonNull;
import ru.practicum.shareit.item.model.Comment;

public class CommentMapper {

	public static CommentDto toDto(@NonNull Comment comment) {
		return CommentDto.builder()
				.id(comment.getId())
				.itemId(comment.getItem().getId())
				.authorName(comment.getAuthor().getName())
				.createdDate(comment.getCreatedDate())
				.text(comment.getText())
				.build();
	}
}
