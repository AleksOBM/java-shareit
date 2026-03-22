package ru.practicum.shareit.booking;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDate;

/**
 * TODO Sprint add-bookings.
 */
@Entity
@Table(name = "booking")
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Booking {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	long id;

	@Column(name = "start_date", nullable = false)
	LocalDate start;

	@Column(name = "end_date", nullable = false)
	LocalDate end;

	@ManyToOne
	@JoinColumn(name = "item_id", nullable = false)
	Item item;

	@ManyToOne
	@JoinColumn(name = "booker_id", nullable = false)
	User booker;

	@Enumerated(EnumType.STRING)
	BookingStatus status;
}
