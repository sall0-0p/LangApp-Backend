package com.lordbucket.langlearn.dto.req;

public record RegisterRequest(
        String username,
        String email,
        String password
) {
}
