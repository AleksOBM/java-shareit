package ru.practicum.shareit.request.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.ExtensionMethod;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.util.hibernate.HibernateEqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-item-requests.
 */
@Data
@Entity
@Table(name = "requests")
@NoArgsConstructor
@Accessors(chain = true)
@ExtensionMethod({HibernateEqualsAndHashCode.class})
public class ItemRequest {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	long id;

	String description;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "requestor_id", nullable = false)
	User requestor;

	@Column(nullable = false)
	LocalDateTime created;
}
