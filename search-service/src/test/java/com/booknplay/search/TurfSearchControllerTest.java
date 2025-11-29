package com.booknplay.search;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.beans.factory.annotation.Autowired;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TurfSearchControllerTest {

    @Autowired
    WebTestClient webTestClient;

    @Test
    void streamSearchReturnsResults() {
        webTestClient.get().uri("/api/search/turfs?lat=10&lng=20")
                .exchange()
                .expectStatus().isOk()
                .returnResult(String.class)
                .getResponseBody()
                .take(1)
                .blockFirst();
    }
}
