package ru.practicum.shareit.item.dto;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.test.context.ContextConfiguration;
import ru.practicum.shareit.ShareItServer;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@ContextConfiguration(classes = ShareItServer.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemBigDtoJsonTest {

	final JacksonTester<ItemBigDto> itemBigDtoJson;

	@Test
	@SneakyThrows
	void testItemBigDto() {
		ItemBigDto itemBigDto = ItemBigDto.builder()
				.id(1L)
				.name("this is item name")
				.description("this is description")
				.available(true)
				.requestId(123L) // request
				.lastBookingDate(LocalDateTime.parse("2027-01-01T00:00:01")) // lastBooking
				.nextBookingDate(LocalDateTime.parse("2027-01-02T01:02:01")) // nextBooking
				.comments(List.of(
						CommentDto.builder()
								.id(6L)
								.itemId(1L)
								.authorName("this is author name 1")
								.createdDate(LocalDateTime.parse("2027-01-01T03:20:01")) // created
								.text("this is text 1")
								.build(),
						CommentDto.builder()
								.id(7L)
								.itemId(1L)
								.authorName("this is author name 2")
								.createdDate(LocalDateTime.parse("2027-03-01T07:21:01")) // created
								.text("this is text 2")
								.build()
				))
				.build();

		JsonContent<ItemBigDto> result = itemBigDtoJson.write(itemBigDto);

		// region itemBigDto
		assertThat(result)
				.extractingJsonPathNumberValue("$.id")
				.isEqualTo(1);
		assertThat(result)
				.extractingJsonPathStringValue("$.name")
				.isEqualTo("this is item name");
		assertThat(result)
				.extractingJsonPathStringValue("$.description")
				.isEqualTo("this is description");
		assertThat(result)
				.extractingJsonPathBooleanValue("$.available")
				.isEqualTo(true);
		assertThat(result)
				.extractingJsonPathNumberValue("$.request")
				.isEqualTo(123);
		assertThat(result)
				.extractingJsonPathStringValue("$.lastBooking")
				.isEqualTo("2027-01-01T00:00:01");
		assertThat(result)
				.extractingJsonPathStringValue("$.nextBooking")
				.isEqualTo("2027-01-02T01:02:01");
		// endregion itemBigDto

		// region comments[0]
		assertThat(result)
				.extractingJsonPathNumberValue("$.comments[0].id")
				.isEqualTo(6);
		assertThat(result)
				.extractingJsonPathNumberValue("$.comments[0].itemId")
				.isEqualTo(1);
		assertThat(result)
				.extractingJsonPathStringValue("$.comments[0].authorName")
				.isEqualTo("this is author name 1");
		assertThat(result)
				.extractingJsonPathStringValue("$.comments[0].created")
				.isEqualTo("2027-01-01T03:20:01");
		assertThat(result)
				.extractingJsonPathStringValue("$.comments[0].text")
				.isEqualTo("this is text 1");
		// endregion comments[0]

		// region comments[1]
		assertThat(result)
				.extractingJsonPathNumberValue("$.comments[1].id")
				.isEqualTo(7);
		assertThat(result)
				.extractingJsonPathNumberValue("$.comments[1].itemId")
				.isEqualTo(1);
		assertThat(result)
				.extractingJsonPathStringValue("$.comments[1].authorName")
				.isEqualTo("this is author name 2");
		assertThat(result)
				.extractingJsonPathStringValue("$.comments[1].created")
				.isEqualTo("2027-03-01T07:21:01");
		assertThat(result)
				.extractingJsonPathStringValue("$.comments[1].text")
				.isEqualTo("this is text 2");
		// endregion comments[1]
	}
}
