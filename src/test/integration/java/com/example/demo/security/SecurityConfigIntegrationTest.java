package com.example.demo.security;

import com.example.demo.TestcontainersConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;

import static io.restassured.RestAssured.given;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestcontainersConfiguration.class)
class SecurityConfigIntegrationTest {

    @LocalServerPort
    private int port;

    @Test
    void shouldReturnUnauthorized_whenAnonymousUser() {
        given().baseUri("http://localhost").port(port)
            .when()
                .get("/request/count")
            .then()
                .statusCode(HttpStatus.UNAUTHORIZED.value());
    }
}
