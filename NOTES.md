
9 AM - 10:45: Slot 1 105 minutes
10:45 - 11:05: Coffee Break
11:05 - 13:00: Slot 2 115 minutes
13:00 - 14:00 Lunch
14:00 - 15:30: Slot 3 90 minutes
15:30 - 15:50 Coffee Break
15:50 - 17:00: Slot 4 70 minutes


Slot 1 (105 Minutes): Welcome & Basics

- learn about the people in the audience
- Intro to the workshop
- Explain maven lifecycle and grouping
- Show tooling, slides access, chat, GitHub repository
- Setup dev environment for everyone
- Spring boot testing basics
- Unit testing with Spring Boot and Java: JUnit & Mockito is all you need
- Embrace external collaborators via constructor, don’t use new or static inside your code, harder to test
- Don’t overuse Mockito
- Learn JUnit 5 extensions -> 
- Refactor code that uses LocalDate.now() to use a clock or timeservice
- Springs testing pyramid

Exercises:

- Exercise write your own JUnit Jupiter extension

Slot 2 (115 Minutes): Sliced Testing

- sliced testing
- Show where unit testing is no longer sufficient -> web tests with unit tests doesn’t give you http semantics too much mocking
- Focus on webmvctest: filter, security, endpoint -> exercise test a filter and two endpoints that are seucred
- Focus on datajpatest: flaws with commit of the entity manager, show that we need to understand when the SQL is fired and need to understand the entitymanager, write a test for a native query, understand that entitys need a default constructor

Slot 3 (90 Minutes): Integration Testing
- integration testing with AtSpringBootTest
- Hit the app from outside
- Need now Testcontainers to spawn external resources
- Data cleanup is imported
- Testcontainers
- Learn about Spring test context caching
- Find flaws in three integration tests and align them to use a single context
- Wirte a HTTP test from the outside

Slot 4 (70 Minutes): Best Practices, Pitfalls, AI & Outlook
- Learn about spring boot testing pitfalls: using @springboottest everywhere, junit 4 vs 5 pitfall, not using context caching, not testing
- Best practices for testing with spring boot
- AI with testing and spring boot
- Outlook
- Ending
- Pitch my online course Testing Spring Boot Applications Masterclass and consultancy services
