# Spring Boot Challenge

Before this project is turned into a production application, the next steps are fundamental:

-   Add authentication mechanisms
-   Use persistent storage, such as a MySQL database
-   Don’t let the user of the REST API define the ID and the balance (the IDs should be generated and the balance should start at 0)
-   The transferMoney function, defined in AccountsService, should be a transaction. If we were using JPA, we could accomplish this by marking the function with the “@Transactional” annotation. In that way, it can be assured that all operations in that function will be executed in an atomic way
-   We could store all the transactions that occur
-   In the tests, we could have utils functions to generate data
