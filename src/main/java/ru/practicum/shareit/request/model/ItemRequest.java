package ru.practicum.shareit.request.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-item-requests.
 */
@Entity
@Table(name = "requests")
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemRequest {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	long id;

	String description;

	@ManyToOne
	@JoinColumn(name = "requestor_id", nullable = false)
	User requestor;

	@Column(nullable = false)
	LocalDateTime created;
}
