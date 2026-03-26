package ru.practicum.shareit.item.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

/**
 * TODO Sprint add-controllers.
 */
@Entity
@Table(name = "items")
@NotNull
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
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

	@ManyToOne
	@JoinColumn(name = "owner_id", nullable = false)
	User owner;

	@ManyToOne
	@JoinColumn(name = "request_id")
	ItemRequest request;
}
