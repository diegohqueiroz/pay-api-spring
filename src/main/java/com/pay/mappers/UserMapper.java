package com.pay.mappers;

import org.springframework.stereotype.Component;

import com.pay.controllers.requests.UserRequest;
import com.pay.controllers.responses.UserResponse;
import com.pay.models.UserEntity;
import com.pay.models.enums.UserType;

@Component
public class UserMapper implements Mapper<UserEntity, UserRequest, UserResponse> {

    @Override
    public UserResponse toDTO(UserEntity entity) {
        if (entity == null) {
            return null;
        }
        UserType userType = UserType.toEnum(entity.getType());
        
        return new UserResponse(
            entity.getId(),
            entity.getName(),
            entity.getEmail(),
            entity.getDocument(),
            userType
        );
    }

    @Override
    public UserEntity toEntity(UserRequest request) {
        if (request == null) {
            return null;
        }
        
        UserEntity entity = new UserEntity();
        entity.setName(request.getName());
        entity.setEmail(request.getEmail());
        entity.setType(request.getTypeCode());
        return entity;
    }
}