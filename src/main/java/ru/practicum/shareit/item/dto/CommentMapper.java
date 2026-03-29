package ru.practicum.shareit.item.dto;

import org.springframework.lang.NonNull;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

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

	public static Comment toComment(@NonNull CommentDto dto, @NonNull Item item,  @NonNull User author) {
		return new Comment()
				.setId(dto.getId())
				.setItem(item)
				.setAuthor(author)
				.setCreatedDate(dto.getCreatedDate() == null ? LocalDateTime.now() : dto.getCreatedDate())
				.setText(dto.getText() == null ? null : dto.getText().trim());
	}
}
