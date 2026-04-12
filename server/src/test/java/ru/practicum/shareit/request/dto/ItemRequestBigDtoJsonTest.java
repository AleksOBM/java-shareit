package ru.practicum.shareit.request.dto;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.test.context.ContextConfiguration;
import ru.practicum.shareit.ShareItServer;
import ru.practicum.shareit.item.dto.ItemLowDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@ContextConfiguration(classes = ShareItServer.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestBigDtoJsonTest {

	final JacksonTester<ItemRequestBigDto> requestBigDtoJson;

	@Test
	@SneakyThrows
	void testItemRequestBigDto() {
		ItemRequestBigDto requestBigDto = ItemRequestBigDto.builder()
				.id(1L)
				.description("this is description")
				.createdDate(LocalDateTime.parse("2027-01-01T00:00:01"))
				.requestor(UserDto.builder()
						.id(5L)
						.name("John")
						.email("john.doe@mail.com")
						.build()
				)
				.itemDtos(List.of(
						new ItemLowDto(7L, "first item dto", 14L),
						new ItemLowDto(8L, "second item dto", 15L)
				))
				.build();

		JsonContent<ItemRequestBigDto> result = requestBigDtoJson.write(requestBigDto);

		// region ItemRequestBigDto
		assertThat(result)
				.extractingJsonPathNumberValue("$.id")
				.isEqualTo(1);
		assertThat(result)
				.extractingJsonPathStringValue("$.description")
				.isEqualTo("this is description");
		assertThat(result)
				.extractingJsonPathStringValue("$.created")
				.isEqualTo("2027-01-01T00:00:01");
		assertThat(result)
				.extractingJsonPathNumberValue("$.requestor.id")
				.isEqualTo(5);
		assertThat(result)
				.extractingJsonPathStringValue("$.requestor.name")
				.isEqualTo("John");
		assertThat(result)
				.extractingJsonPathStringValue("$.requestor.email")
				.isEqualTo("john.doe@mail.com");
		// endregion ItemRequestBigDto

		// region items[0]
		assertThat(result)
				.extractingJsonPathNumberValue("$.items[0].id")
				.isEqualTo(7);
		assertThat(result)
				.extractingJsonPathStringValue("$.items[0].name")
				.isEqualTo("first item dto");
		assertThat(result)
				.extractingJsonPathNumberValue("$.items[0].ownerId")
				.isEqualTo(14);
		// endregion items[0]

		// region items[1]
		assertThat(result)
				.extractingJsonPathNumberValue("$.items[1].id")
				.isEqualTo(8);
		assertThat(result)
				.extractingJsonPathStringValue("$.items[1].name")
				.isEqualTo("second item dto");
		assertThat(result)
				.extractingJsonPathNumberValue("$.items[1].ownerId")
				.isEqualTo(15);
		// endregion items[1]
	}
}
