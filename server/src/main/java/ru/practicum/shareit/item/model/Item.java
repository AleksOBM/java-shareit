package ru.practicum.shareit.item.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.ExtensionMethod;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.util.hibernate.HibernateEqualsAndHashCode;

import java.util.Objects;

import static ru.practicum.shareit.util.hibernate.HibernateEqualsAndHashCode.*;

@Data
@Entity
@NoArgsConstructor
@Accessors(chain = true)
@Table(name = "items")
@ExtensionMethod({HibernateEqualsAndHashCode.class})
public class Item {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;

	@Column(name = "item_name", nullable = false, length = 100)
	String name;

	@Column
	String description;

	/// статус о том, доступна или нет вещь для аренды
	@Column(nullable = false)
	boolean available;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "owner_id", nullable = false)
	User owner;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "request_id")
	ItemRequest request;

	//region equals and hashCode
	@Override
	public final boolean equals(Object object) {
		return this == object
				|| object != null
				&& persistentClass(this) == object.persistentClass()
				&& object instanceof Item item
				&& Objects.equals(getId(), item.getId());
	}

	@Override
	public final int hashCode() {
		return persistentClass(this).hashCode();
	}
	//endregion
}
