package ru.practicum.shareit.item.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Comment {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;

	@ManyToOne
	@JoinColumn(name = "item_id")
	Item item;

	@ManyToOne
	@JoinColumn(name = "author_id")
	User author;

	LocalDateTime createdDate;

	@Column(name = "comment_text")
	String text;

	public static Comment from(CommentDto commentDto, Item item, User autor) {
		return new Comment(
				commentDto.getId(),
				item,
				autor,
				commentDto.getCreatedDate(),
				commentDto.getText()
		);
	}
}
