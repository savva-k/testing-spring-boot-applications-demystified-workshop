# Prompts

## Lab 1

- in labs/lab-1, create two exercises in src/test/java and pragmatech.digitial.workshops.lab1.exercises called Exercise1UnitTest: Write two tests for the book service using Mockito to test when an existing book tries to get re-added resulting in the excpetion (use JUnit's assertThrows here) and one for the happy path, next add Exercise2JUnitExtensionTest where the student has to develop a custom JUnit jupiter extension where the extensions prints only to the console if the test took longer than 100ms to quickly catch slow tests, optional give the extension a paramrter to configure the ms threshold from the outside, explore the different ways to activate the extension

For all two exercise classes, add the solutions inside pragmatech.digitial.workshops.lab1.solutions called Solution1UnitTest and Solution2JUnitExtensionTest

## Lab 2

- [X] inside the pragmatech.digitial.workshops.lab2.experiment controller add two tests: BookControllerUnit tests, trying to show what could be tested with a plain unit test using only mockito and Junit, next create in the same package a test called BookControllerTest to showcase the usage of @WebMvcTest and MockMvc to better test with HTTP semantics: proper JSON send, ensure sent data is validated, mapping of custom exceptions, authorization and authentication rules are met. Next, in the same package, add a @WebMvcTest for the LlmBotRequestFilter to demo how a filter can be better tested with MockMvc, also make sure of the new MockMvcTester
- [X] create an SVG in slides/assets to explain Hibernate/JPA with its entity manager first/second level cache, flushing to the database to understand your inserts are not directly written, it's a write-behind cache with dirty checking

- [ ] Add Exercises: Play with MockMvc, test a protected endpoint, test the native query with a data preparation of your choice

in labs/lab-2, create two exercises in src/test/java and pragmatech.digitial.workshops.lab2.exercises called Exercise1WebMvcTest: Write two MockMvc tests to verify that only admins can delete books, create a MockMvc POST to send JSON data to verify the books are created successfully, next add Exercise2DataJpaTest where the student has test the native query of the BookRepository and use Testcontainers, also ensuring no data exists after the tests 
For all two exercise classes, add the solutions inside pragmatech.digitial.workshops.lab2.solutions called Solution1WebMvcTest and Solution2DataJpaTest

## Lab 3

- [X] Take over the entire code base (not the test code) at the state of labs/lab-2 to have a consistent ongoing code base. In lab-3 we will evolve it a bit.

- [X] In labs/lab-3, Develop a HTTP client with Spring's new RestCLient to fetch metadata about a book from a remote API. Next, develop a HTTP client test for it using WireMock and real stubbed JSON responses. Furthermore, add a sample HTTP call to this client that gets called during application startup to later on show the usage of ContextInitializers for stubbing.

- [X] In pragmatech.digitial.workshops.lab3.experiment add a test to showcase the difference of MockMvc and WebTestClient when it comes to thread context, data access and rollback

- [X] Please create an SVG graphic to explain the usage of WireMock as a replacement of a real remote HTTP system for testing purposes

- [ ] Exercises: Get app start working with WireMock, develop a case to handle failure of the remote system, Investigate ITs to find out how many contexts are started and get it to a reasonable minimum, implement retry with TDD

## Lab 4

- [ ] For labs/lab-4, Take over the entire code base (not the test code) at the state of labs/lab-3 to have a consistent ongoing code base. In lab-4 we will evolve it a bit.

- [ ] For labs/lab-4 in the pragmatech.digitial.workshops.lab4.experiment folder, create three test classes to help the audience understand the difference between @Mock, @MockBean and @MockitoBean

- [ ] For labs/lab-4 in the pragmatech.digitial.workshops.lab4.experiment, create two test classes to showcase the different API/imports between JUnit 4 and 5

- [ ] For labs/lab-4 in the pragmatech.digitial.workshops.lab4.experiment, add a sample bad Unit test in our book domain to showcase mutation testing, also include the relevant PIT config for it

- [ ] For labs/lab-4, configure the failsafe and surefire with different JUnit jupiter parallelization configs and use the forkMode for junit, add some sample tests in a pragmatech.digitial.workshops.lab3.parallel folder (normal unittest and integration test) with some logging to see on which JVM and which thread they run -> help me educate about test parallelization

## Houskeeping

- Create a consistent README for all labs in the root of each lab folder. Follow the same structure for each file: Learning Objectives, Hints, Exercises. For each exercise, give an introduction and tips on what they need to do and where they find the solution

- Use uniquely postgres-alpine-x
