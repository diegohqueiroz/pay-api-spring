package com.pay.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pay.controllers.responses.AccountResponse;
import com.pay.mappers.AccountMapper;
import com.pay.repositories.AccountRepository;

@Service
public class AccountService {
    private final AccountMapper mapper;
    private final AccountRepository accountRepository;
    
    @Autowired
    public AccountService(AccountMapper mapper, AccountRepository accountRepository) {
        this.mapper = mapper;
        this.accountRepository = accountRepository;
    }
 
    public AccountResponse getById(Long userId){
        return mapper.toDTO(accountRepository.findByUserId(userId).get());
    }
    
}
