package com.lordbucket.langlearn.dto.res;

import com.lordbucket.langlearn.dto.model.UserDTO;

public record AuthResponse(String token, UserDTO user) {
}
