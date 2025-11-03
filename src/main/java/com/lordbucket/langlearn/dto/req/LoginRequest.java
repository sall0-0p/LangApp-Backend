package com.lordbucket.langlearn.dto.req;

public record LoginRequest(
        String usernameOrEmail,
        String password
) {
}
