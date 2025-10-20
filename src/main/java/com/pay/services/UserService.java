package com.pay.services;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pay.controllers.requests.UserRequest;
import com.pay.controllers.responses.UserResponse;
import com.pay.mappers.UserMapper;
import com.pay.models.AccountEntity;
import com.pay.models.UserEntity;
import com.pay.repositories.AccountRepository;
import com.pay.repositories.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class UserService {
    private final UserMapper mapper;
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;

    @Autowired
    public UserService(UserMapper mapper, UserRepository userRepository, AccountRepository accountRepository) {
        this.mapper = mapper;
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
    }


    public List<UserResponse> getAll(){
        return mapper.toDTOList(userRepository.findAll());
    }

    public UserResponse getById(Long id){
        return mapper.toDTO(userRepository.findById(id).get());
    }

    @Transactional
    public Long create(UserRequest request){
        UserEntity entity = mapper.toEntity(request);
        userRepository.save(entity);
        AccountEntity accountEntity  = new AccountEntity();
        accountEntity.setUser(entity);
        accountEntity.setBalance(BigDecimal.ZERO);
        accountRepository.save(accountEntity);
        return entity.getId();
    }

    @Transactional
    public void update(UserRequest request, Long id){
        UserEntity entity = userRepository.getById(id);
        entity.setName(request.getName());
        entity.setEmail(request.getEmail());
        entity.setType(request.getTypeCode());
        userRepository.save(entity);
    }

    @Transactional
    public void delete(Long id){
        UserEntity user = userRepository.getById(id);
        userRepository.delete(user);
    } 
    
}
