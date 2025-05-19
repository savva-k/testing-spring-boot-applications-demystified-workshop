# Prompts

## Lab-2

- inside the pragmatech.digitial.workshops.lab2.experiment controller add two tests: BookControllerUnit tests, trying to show what could be tested with a plain unit test using only mockito and Junit, next create in the same package a test called BookControllerTest to showcase the usage of @WebMvcTest and MockMvc to better test with HTTP semantics: proper JSON send, ensure sent data is validated, mapping of custom exceptions, authorization and authentication rules are met. Next, in the same package, add a @WebMvcTest for the LlmBotRequestFilter to demo how a filter can be better tested with MockMvc, also make sure of the new MockMvcTester

## Lab-3

- Take over the entire code base at the state of labs/lab-2 to have a consistent ongoing code base. In lab-3 we will evolve it a bit.

- Develop a HTTP client with Spring's new RestCLient to fetch metadata about a book from a remote API. Next, develop a HTTP client test for it using WireMock and real stubbed JSON responses. Furthermore, add a sample HTTP call to this client that gets called during application startup to later on show the usage of ContextInitializers for stubbing.

- Exercises: Get app start working with WireMock, develop a case to handle failure of the remote system, Investigate ITs to find out how many contexts are started and get it to a reasonable minimum
