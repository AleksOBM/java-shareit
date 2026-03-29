package ru.practicum.shareit.user.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.ExtensionMethod;
import ru.practicum.shareit.util.hibernate.HibernateEqualsAndHashCode;

import java.util.Objects;

import static ru.practicum.shareit.util.hibernate.HibernateEqualsAndHashCode.*;

@Data
@Entity
@Table(name = "users")
@NoArgsConstructor
@Accessors(chain = true)
@ExtensionMethod({HibernateEqualsAndHashCode.class})
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;

	@Column(name = "user_name", nullable = false)
	String name;

	@Column(nullable = false, unique = true, length = 512)
	String email;

	//region equals and hashCode
	@Override
	public final boolean equals(Object object) {
		return this == object
				|| object != null
				&& persistentClass(this) == object.persistentClass()
				&& object instanceof User user
				&& Objects.equals(getId(), user.getId());
	}

	@Override
	public final int hashCode() {
		return persistentClass(this).hashCode();
	}
	//endregion
}
