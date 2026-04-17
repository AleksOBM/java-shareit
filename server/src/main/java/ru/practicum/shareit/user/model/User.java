package ru.practicum.shareit.user.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;

import ru.practicum.shareit.util.entity.BaseEntity;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
public class User extends BaseEntity {

	@Column(name = "user_name", nullable = false)
	private String name;

	@Column(nullable = false, unique = true, length = 512)
	private String email;

	public User setID(Long id) {
		this.id = id;
		return this;
	}
}
