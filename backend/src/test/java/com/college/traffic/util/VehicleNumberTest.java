package com.college.traffic.util;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class VehicleNumberTest {
    @Test void normalizesCaseSpacesAndHyphens() {
        assertEquals("TS09EA1234", VehicleNumber.normalize("ts 09 ea 1234"));
        assertEquals("KA01MJ7788", VehicleNumber.normalize("ka-01-mj-7788"));
        assertEquals("DL08CAF5030", VehicleNumber.normalize("DL8CAF5030".replace("DL8", "DL08")));
    }
    @Test void rejectsBadNumbers() {
        assertThrows(IllegalArgumentException.class, () -> VehicleNumber.normalize("12ABCD"));
        assertThrows(IllegalArgumentException.class, () -> VehicleNumber.normalize(""));
        assertThrows(IllegalArgumentException.class, () -> VehicleNumber.normalize(null));
    }
    @Test void cleanHandlesNull() { assertEquals("", VehicleNumber.clean(null)); }
}
