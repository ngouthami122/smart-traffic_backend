package com.college.traffic.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.jayway.jsonpath.JsonPath;
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
class ParkingApiTest {
    @Autowired MockMvc mvc;

    private int slot(String number, String type) throws Exception {
        String body = mvc.perform(post("/api/slots").contentType(MediaType.APPLICATION_JSON)
                .content("{\"slotNumber\":\"%s\",\"slotType\":\"%s\"}".formatted(number, type)))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        return JsonPath.read(body, "$.id");
    }
    private org.springframework.test.web.servlet.ResultActions entry(String plate, int slotId) throws Exception {
        return mvc.perform(post("/api/parking/entry").contentType(MediaType.APPLICATION_JSON)
                .content("{\"vehicleNumber\":\"%s\",\"slotId\":%d}".formatted(plate, slotId)));
    }

    @Test void entryThenExitChargesOneMinimumHour() throws Exception {
        int s = slot("A1", "CAR");
        entry("TS09EA1234", s).andExpect(status().isCreated()).andExpect(jsonPath("$.slot.occupied").value(true));
        mvc.perform(get("/api/parking/active")).andExpect(jsonPath("$.length()").value(1));
        mvc.perform(post("/api/parking/exit").contentType(MediaType.APPLICATION_JSON).content("{\"vehicleNumber\":\"ts09ea1234\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.charge").value(30.0)).andExpect(jsonPath("$.exitTime").isNotEmpty());
        mvc.perform(get("/api/parking/active")).andExpect(jsonPath("$.length()").value(0));
    }

    @Test void duplicateEntryAndOccupiedSlotAreRejected() throws Exception {
        int a = slot("A1", "CAR"), b = slot("A2", "CAR");
        entry("TS09EA1234", a).andExpect(status().isCreated());
        entry("TS09EA1234", b).andExpect(status().isConflict());
        entry("KA01MJ7788", a).andExpect(status().isConflict());
    }

    @Test void occupiedSlotCannotBeDeleted() throws Exception {
        int a = slot("A1", "CAR");
        entry("TS09EA1234", a).andExpect(status().isCreated());
        mvc.perform(delete("/api/slots/" + a)).andExpect(status().isConflict());
    }

    @Test void duplicateSlotNumberIsRejected() throws Exception {
        slot("A1", "CAR");
        mvc.perform(post("/api/slots").contentType(MediaType.APPLICATION_JSON).content("{\"slotNumber\":\"a1\",\"slotType\":\"BIKE\"}"))
                .andExpect(status().isConflict());
    }

    @Test void exitOfUnknownVehicleIsNotFound() throws Exception {
        mvc.perform(post("/api/parking/exit").contentType(MediaType.APPLICATION_JSON).content("{\"vehicleNumber\":\"TS09EA1234\"}"))
                .andExpect(status().isNotFound());
    }

    @Test void dashboardSummaryCountsSlots() throws Exception {
        int a = slot("A1", "CAR"); slot("A2", "CAR"); slot("B1", "BIKE");
        entry("TS09EA1234", a).andExpect(status().isCreated());
        mvc.perform(get("/api/dashboard/summary")).andExpect(jsonPath("$.totalSlots").value(3))
                .andExpect(jsonPath("$.occupiedSlots").value(1)).andExpect(jsonPath("$.availableSlots").value(2));
        mvc.perform(get("/api/reports/parking-occupancy")).andExpect(jsonPath("$.occupancyPercent").value(33.3));
    }
}
