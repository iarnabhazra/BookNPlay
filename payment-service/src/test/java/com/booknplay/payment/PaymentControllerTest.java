package com.booknplay.payment;

import com.booknplay.payment.domain.PaymentRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PaymentControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    PaymentRepository repository;

    @Test
    void createAndListPayments() throws Exception {
        String body = "{" +
                "\"bookingRef\":\"B123\"," +
                "\"baseAmount\":100," +
                "\"demandFactor\":10," +
                "\"slot\":\"" + Instant.now().toString() + "\"}";
        mockMvc.perform(post("/api/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isOk());

        var mvcResult = mockMvc.perform(get("/api/payments"))
                .andExpect(status().isOk())
                .andReturn();
        String json = mvcResult.getResponse().getContentAsString();
        Assertions.assertTrue(json.contains("B123"));
    }
}
