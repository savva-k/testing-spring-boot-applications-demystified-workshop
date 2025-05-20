# Prompts

## Lab 3

- [X] Take over the entire code base (not the test code) at the state of labs/lab-2 to have a consistent ongoing code base. In lab-3 we will evolve it a bit.

- [X] In labs/lab-3, Develop a HTTP client with Spring's new RestCLient to fetch metadata about a book from a remote API. Next, develop a HTTP client test for it using WireMock and real stubbed JSON responses. Furthermore, add a sample HTTP call to this client that gets called during application startup to later on show the usage of ContextInitializers for stubbing.

- [X] In pragmatech.digitial.workshops.lab3.experiment add a test to showcase the difference of MockMvc and WebTestClient when it comes to thread context, data access and rollback

- [X] Please create an SVG graphic to explain the usage of WireMock as a replacement of a real remote HTTP system for testing purposes

- [ ] Exercises: Get app start working with WireMock, develop a case to handle failure of the remote system, Investigate ITs to find out how many contexts are started and get it to a reasonable minimum, implement retry with TDD

in labs/lab-3, create two exercises in src/test/java and pragmatech.digitial.workshops.lab3.exercises called Exercise1WireMockTest: Write a test for the OpenLibraryApiClient with WireMock as a unit test without the spring context, try to test the happy path with a 200 resposne and a test to see how the client responds to 500, optional: fix the http client to not throw an erorr for http 404 and rather retur null. The student can research the wiremock JUnit 5 extension or bootstraps the wiremoc kserver manually. Next develop five ApplictionOneIT to ApplicationFiveIT where you slightly change the context configuration (e.g. mockbean or profile) so that the student can identify how many app contexts are started 
For the first exercise, add a solutions inside pragmatech.digitial.workshops.lab3.solutions called Solution1WireMockTest

## Lab 4

- [ ] For labs/lab-4 in the pragmatech.digitial.workshops.lab4.experiment, add a sample bad Unit test in our book domain to showcase mutation testing, also include the relevant PIT config for it

## Houskeeping

- Create a consistent README for all labs in the root of each lab folder. Follow the same structure for each file: Learning Objectives, Hints, Exercises. For each exercise, give an introduction and tips on what they need to do and where they find the solution.

- Use uniquely postgres-alpine-x
