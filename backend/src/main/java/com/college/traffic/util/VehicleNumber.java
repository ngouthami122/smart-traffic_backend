package com.college.traffic.util;

/** Helpers for Indian vehicle numbers such as TS09EA1234. Pure Java, no Spring, easy to test. */
public final class VehicleNumber {
    /** Accepts optional spaces or hyphens and any letter case, e.g. "ts 09 ea 1234". */
    public static final String INPUT_REGEX = "(?i)^[A-Z]{2}[ -]?\\d{2}[ -]?[A-Z]{1,3}[ -]?\\d{4}$";
    private static final String CLEAN_REGEX = "^[A-Z]{2}\\d{2}[A-Z]{1,3}\\d{4}$";

    private VehicleNumber() { }

    /** Upper-cases and removes everything except letters and digits. Null becomes "". */
    public static String clean(String raw) {
        return raw == null ? "" : raw.toUpperCase().replaceAll("[^A-Z0-9]", "");
    }

    /** Returns the canonical form (TS09EA1234) or throws IllegalArgumentException (HTTP 400). */
    public static String normalize(String raw) {
        String c = clean(raw);
        if (!c.matches(CLEAN_REGEX)) {
            throw new IllegalArgumentException("Vehicle number must look like TS09EA1234");
        }
        return c;
    }
}
