# Prompts

## Lab 2

- fix H2 mode sample query in test and X-Frame options issue

- inside the pragmatech.digitial.workshops.lab2.experiment controller add two tests: BookControllerUnit tests, trying to show what could be tested with a plain unit test using only mockito and Junit, next create in the same package a test called BookControllerTest to showcase the usage of @WebMvcTest and MockMvc to better test with HTTP semantics: proper JSON send, ensure sent data is validated, mapping of custom exceptions, authorization and authentication rules are met. Next, in the same package, add a @WebMvcTest for the LlmBotRequestFilter to demo how a filter can be better tested with MockMvc, also make sure of the new MockMvcTester

## Lab 3

- Take over the entire code base at the state of labs/lab-2 to have a consistent ongoing code base. In lab-3 we will evolve it a bit.

- Develop a HTTP client with Spring's new RestCLient to fetch metadata about a book from a remote API. Next, develop a HTTP client test for it using WireMock and real stubbed JSON responses. Furthermore, add a sample HTTP call to this client that gets called during application startup to later on show the usage of ContextInitializers for stubbing.
- In pragmatech.digitial.workshops.lab3.experiment add a test to showcase the difference of MockMvc and TestRestTemplate when it comes to thread context, data access and rollback

- Please create an SVG graphic to explain the usage of WireMock as a replacement of a real remote HTTP system for testing purposes

- Exercises: Get app start working with WireMock, develop a case to handle failure of the remote system, Investigate ITs to find out how many contexts are started and get it to a reasonable minimum

## Lab 4

- In the pragmatech.digitial.workshops.lab3.experiment folder, create three test classes to help the audience understand the difference between @Mock, @MockBean and @MockitoBean
- In the pragmatech.digitial.workshops.lab3.experiment folder, create two test classes to showcase the different API/imports between JUnit 4 and 5
- In the pragmatech.digitial.workshops.lab3.experiment folder, add a sample bad Unit test in our book domain to showcase mutation testing, also include the relevant PIT config for it
- in lab4, configure the failsafe and surefire with different JUnit jupiter parallelization configs and use the forkMode for junit, add some sample tests in a pragmatech.digitial.workshops.lab3.parallel folder (normal unittest and integration test) with some logging to see on which JVM and which thread they run -> help me educate about test parallelization

## Houskeeping

- Create a consistent README for all labs in the root of each lab folder. Follow the same structure for each file: Learning Objectives, Hints, Exercises. For each exercise, give an introduction and tips on what they need to do and where they find the solution
