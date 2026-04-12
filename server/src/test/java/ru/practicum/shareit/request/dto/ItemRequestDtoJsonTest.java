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
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@ContextConfiguration(classes = ShareItServer.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestDtoJsonTest {

    final JacksonTester<ItemRequestDto> requestDtoJson;

    @Test
    @SneakyThrows
    void testItemRequestDto() {
        ItemRequestDto requestDto = ItemRequestDto.builder()
                .id(1L)
                .description("this is description")
                .createdDate(LocalDateTime.parse("2027-01-01T00:00:01"))
                .requestor(UserDto.builder()
                        .id(5L)
                        .name("John")
                        .email("john.doe@mail.com")
                        .build()
                )
                .build();

        JsonContent<ItemRequestDto> result = requestDtoJson.write(requestDto);

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
    }
}