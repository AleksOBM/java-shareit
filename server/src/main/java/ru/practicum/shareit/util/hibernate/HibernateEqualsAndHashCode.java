package ru.practicum.shareit.util.hibernate;

import lombok.experimental.UtilityClass;
import org.hibernate.proxy.HibernateProxy;

@UtilityClass
public class HibernateEqualsAndHashCode {
	public Class<?> persistentClass(Object object) {
		return object instanceof HibernateProxy proxy ?
				proxy.getHibernateLazyInitializer().getPersistentClass()
				: object.getClass();
	}
}
