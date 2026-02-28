package com.example.demo.controller;

import com.example.demo.TestcontainersConfiguration;
import com.example.demo.filter.RequestCounterFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestcontainersConfiguration.class)
class RequestCounterControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private RequestCounterFilter requestCounterFilter;

    private RequestSpecification givenAuthenticated() {
        return given().baseUri("http://localhost").port(this.port).contentType(ContentType.JSON)
                .auth().basic("admin", "password");
    }

    @BeforeEach
    void setUp() {
        requestCounterFilter.resetRequestCount();
    }

    @Test
    void shouldReturnRequestCount() {
        givenAuthenticated()
            .when()
                .get("/request/count")
            .then()
                .statusCode(HttpStatus.OK.value())
                .body(equalTo("1"));
    }
}
