package com.college.traffic.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.jayway.jsonpath.JsonPath;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ChallanApiTest {
    @Autowired MockMvc mvc;

    private String json(String plate, String date, String status) {
        return "{\"vehicleNumber\":\"%s\",\"violationType\":\"NO_HELMET\",\"violationDate\":\"%s\",\"fineAmount\":1000,\"paymentStatus\":\"%s\"}"
                .formatted(plate, date, status);
    }
    private String yesterday() { return LocalDate.now().minusDays(1).toString(); }
    private String create(String plate, String status) throws Exception {
        return mvc.perform(post("/api/challans").contentType(MediaType.APPLICATION_JSON).content(json(plate, yesterday(), status)))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
    }

    @Test void createNormalizesPlateAndDefaultsToPending() throws Exception {
        mvc.perform(post("/api/challans").contentType(MediaType.APPLICATION_JSON).content(json("ts 09 ea 1234", yesterday(), "PENDING")))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.vehicleNumber").value("TS09EA1234"))
                .andExpect(jsonPath("$.paymentStatus").value("PENDING")).andExpect(jsonPath("$.id").exists());
    }

    @Test void invalidPlateReturnsFieldError() throws Exception {
        mvc.perform(post("/api/challans").contentType(MediaType.APPLICATION_JSON).content(json("BAD", yesterday(), "PENDING")))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.fieldErrors.vehicleNumber").exists());
    }

    @Test void futureDateIsRejected() throws Exception {
        mvc.perform(post("/api/challans").contentType(MediaType.APPLICATION_JSON).content(json("TS09EA1234", LocalDate.now().plusDays(2).toString(), "PENDING")))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.fieldErrors.violationDate").exists());
    }

    @Test void searchAndFilter() throws Exception {
        create("TS09EA1234", "PENDING");
        create("KA01MJ7788", "PAID");
        mvc.perform(get("/api/challans").param("search", "ka01")).andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(1));
        mvc.perform(get("/api/challans").param("status", "PAID")).andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].vehicleNumber").value("KA01MJ7788"));
        mvc.perform(get("/api/challans")).andExpect(jsonPath("$.length()").value(2));
    }

    @Test void updatingToPaidSetsPaidDate() throws Exception {
        Integer id = JsonPath.read(create("TS09EA1234", "PENDING"), "$.id");
        mvc.perform(put("/api/challans/" + id).contentType(MediaType.APPLICATION_JSON).content(json("TS09EA1234", yesterday(), "PAID")))
                .andExpect(status().isOk()).andExpect(jsonPath("$.paymentStatus").value("PAID")).andExpect(jsonPath("$.paidDate").isNotEmpty());
    }

    @Test void deleteThenNotFound() throws Exception {
        Integer id = JsonPath.read(create("TS09EA1234", "PENDING"), "$.id");
        mvc.perform(delete("/api/challans/" + id)).andExpect(status().isNoContent());
        mvc.perform(get("/api/challans/" + id)).andExpect(status().isNotFound());
    }
}
