package org.reactivestax.canada_active_life.enums;

public class SecurityConstants {
    public static final String SECRET = "TestSecrEtKeyF0rJwtHash1ng";
    public static final long EXPIRATION_TIME = 900_000; // 15 mins
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";
    public static final String LOGIN_URL = "/CanadaActiveLife/v1/login";
}
