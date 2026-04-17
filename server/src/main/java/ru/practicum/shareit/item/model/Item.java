package ru.practicum.shareit.item.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.util.entity.BaseEntity;

@Entity
@Table(name = "items")
@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
public class Item extends BaseEntity {

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

	public Item setID(Long id) {
		this.id = id;
		return this;
	}
}
