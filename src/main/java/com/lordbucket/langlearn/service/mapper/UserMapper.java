package com.lordbucket.langlearn.service.mapper;

import com.lordbucket.langlearn.dto.model.UserDTO;
import com.lordbucket.langlearn.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public UserDTO toDTO(User user) {
        return new UserDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail()
        );
    }
}
