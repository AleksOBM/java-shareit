package ru.practicum.shareit.item.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.util.entity.BaseEntity;
import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
public class Comment extends BaseEntity {

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "item_id")
	Item item;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "author_id")
	User author;

	LocalDateTime createdDate;

	@Column(name = "comment_text", length = 500)
	String text;

	public Comment setID(Long id) {
		this.id = id;
		return this;
	}
}
