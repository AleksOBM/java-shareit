package ru.practicum.shareit.item.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.ExtensionMethod;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.util.hibernate.HibernateEqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.Objects;

import static ru.practicum.shareit.util.hibernate.HibernateEqualsAndHashCode.*;

@Data
@Entity
@NoArgsConstructor
@Accessors(chain = true)
@Table(name = "comments")
@ExtensionMethod({HibernateEqualsAndHashCode.class})
public class Comment {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "item_id")
	Item item;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "author_id")
	User author;

	LocalDateTime createdDate;

	@Column(name = "comment_text", length = 500)
	String text;

	//region equals and hashCode
	@Override
	public final boolean equals(Object object) {
		return this == object
				|| object != null
				&& persistentClass(this) == object.persistentClass()
				&& object instanceof Comment comment
				&& Objects.equals(getId(), comment.getId());
	}

	@Override
	public final int hashCode() {
		return persistentClass(this).hashCode();
	}
	//endregion
}
