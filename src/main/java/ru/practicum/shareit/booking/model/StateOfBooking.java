package ru.practicum.shareit.booking.model;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

public enum StateOfBooking {
	UNDEFINED, ALL, CURRENT, PAST, FUTURE, WAITING, REJECTED;

	@Getter
	private String invalidValue;

	public static StateOfBooking of(String text) {
		if (text == null) {
			return ALL;
		}
		String state = Arrays.stream(StateOfBooking.values())
				.map(StateOfBooking::name)
				.filter(value -> text.toUpperCase().equals(value))
				.findAny().orElse(null);

		if (state == null) {
			StateOfBooking so = UNDEFINED;
			so.invalidValue = text;
			return so;
		}
		return StateOfBooking.valueOf(state);
	}

	public static List<String> getValidValues() {
		return Arrays.stream(values()).filter(v -> v.ordinal() > 0)
				.map(StateOfBooking::name)
				.toList();
	}
}
