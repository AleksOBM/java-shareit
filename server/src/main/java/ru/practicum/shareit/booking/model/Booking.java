package ru.practicum.shareit.booking.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.ExtensionMethod;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.util.hibernate.HibernateEqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.Objects;

import static ru.practicum.shareit.util.hibernate.HibernateEqualsAndHashCode.*;

/**
 * TODO Sprint add-bookings.
 */

@Data
@Entity
@NoArgsConstructor
@Accessors(chain = true)
@Table(name = "bookings")
@ExtensionMethod({HibernateEqualsAndHashCode.class})
public class Booking {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;

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

	//region equals and hashCode
	@Override
	public final boolean equals(Object object) {
		return this == object
				|| object != null
				&& persistentClass(this) == object.persistentClass()
				&& object instanceof Booking booking
				&& Objects.equals(getId(), booking.getId());
	}

	@Override
	public final int hashCode() {
		return persistentClass(this).hashCode();
	}
	//endregion
}
