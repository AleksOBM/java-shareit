package user;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import ru.practicum.shareit.ShareItServer;
import ru.practicum.shareit.user.service.UserServiceImpl;
import ru.practicum.shareit.util.UtilService;

@Transactional
@Import({UserServiceImpl.class, UtilService.class})
@ContextConfiguration(classes = {ShareItServer.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest(properties = {
		"spring.datasource.driver-class-name=org.h2.Driver",
		"spring.datasource.url=jdbc:h2:mem:shareit"
})
public class UserServiceTest {

	@PersistenceContext
	private EntityManager em;

	@Autowired
	private UserServiceImpl userService;

	@Test
	public void startingTest() {
	}
}
