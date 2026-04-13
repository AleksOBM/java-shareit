package ru.practicum.shareit.request.model;

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
@Table(name = "requests")
@NoArgsConstructor
@Accessors(chain = true)
@ExtensionMethod({HibernateEqualsAndHashCode.class})
public class ItemRequest {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;

	String description;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "requestor_id", nullable = false)
	User requestor;

	@Column(name = "created", nullable = false)
	LocalDateTime createdDate;

	//region equals and hashCode
	@Override
	public final boolean equals(Object object) {
		return this == object
				|| object != null
				&& persistentClass(this) == object.persistentClass()
				&& object instanceof ItemRequest request
				&& Objects.equals(getId(), request.getId());
	}

	@Override
	public final int hashCode() {
		return persistentClass(this).hashCode();
	}
	//endregion
}
