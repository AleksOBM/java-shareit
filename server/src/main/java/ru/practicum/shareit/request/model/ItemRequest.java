package ru.practicum.shareit.request.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.util.entity.BaseEntity;

import java.time.LocalDateTime;

@Entity
@Table(name = "requests")
@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
public class ItemRequest extends BaseEntity {

	String description;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "requestor_id", nullable = false)
	User requestor;

	@Column(name = "created", nullable = false)
	LocalDateTime createdDate;

	public ItemRequest setID(Long id) {
		this.id = id;
		return this;
	}
}
