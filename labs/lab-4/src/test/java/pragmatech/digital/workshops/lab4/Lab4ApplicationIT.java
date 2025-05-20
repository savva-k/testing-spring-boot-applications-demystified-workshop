package pragmatech.digital.workshops.lab4;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import(LocalDevTestcontainerConfig.class)
class Lab4ApplicationIT {

    @Test
    void contextLoads() {
    }
}
