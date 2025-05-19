package pragmatech.digital.workshops.lab3;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import pragmatech.digital.workshops.lab3.config.PostgresTestContainer;

@SpringBootTest
@ActiveProfiles("test")
class Lab3ApplicationIT extends PostgresTestContainer {

    @Test
    void contextLoads() {
        // This test verifies that the application context loads successfully
    }
}
