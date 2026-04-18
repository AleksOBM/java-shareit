package ru.practicum.shareit.booking.model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.util.entity.BaseEntity;

import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
public class Booking extends BaseEntity {

	@Column(name = "start_date", nullable = false)
	LocalDateTime start;

	@Column(name = "end_date", nullable = false)
	LocalDateTime end;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "item_id", nullable = false)
	Item item;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "booker_id", nullable = false)
	User booker;

	@Enumerated(EnumType.STRING)
	BookingStatus status;

	public Booking setID(Long id) {
		this.id = id;
		return this;
	}
}
