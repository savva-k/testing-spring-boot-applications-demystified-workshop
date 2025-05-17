package pragmatech.digital.workshops.lab3.experiment;

import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@SpringBootTest
class SampleIT {

  @Autowired
  private ObjectMapper objectMapper;

  @Container
  @ServiceConnection
  static final PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:14.5")
    .withDatabaseName("testdb")
    .withUsername("test")
    .withPassword("test");

  @TestConfiguration
  static class ObjectMapperConfiguration {

    @Bean
    @Primary
    public Jackson2ObjectMapperBuilderCustomizer jsonCustomizer() {
      return builder -> builder.featuresToEnable(SerializationFeature.INDENT_OUTPUT);
    }
  }

  @Test
  void testObjectMapperWithIndent() throws Exception {
    var persons = List.of(
      new Person("John Doe", 30),
      new Person("Jane Doe", 25),
      new Person("Jim Doe", 35)
    );

    System.out.println(objectMapper.writeValueAsString(persons));
  }

  record Person(String name, int age) {
  }
}
