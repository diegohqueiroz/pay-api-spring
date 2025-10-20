package com.pay.mappers;

import org.springframework.stereotype.Component;

import com.pay.controllers.requests.AccountRequest;
import com.pay.controllers.responses.AccountResponse;
import com.pay.models.AccountEntity;

@Component
public class AccountMapper implements Mapper<AccountEntity, AccountRequest, AccountResponse> {

    @Override
    public AccountResponse toDTO(AccountEntity entity) {
        if (entity == null) {
            return null;
        }
        
        return new AccountResponse(
            entity.getId(),
            entity.getUser().getId(),
            entity.getUser().getName(),
            entity.getBalance()
        );
    }

    @Override
    public AccountEntity toEntity(AccountRequest dto) {
        throw new UnsupportedOperationException("Unimplemented method 'toEntity'");
    }
}