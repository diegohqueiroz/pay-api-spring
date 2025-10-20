package com.pay.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pay.models.AccountEntity;

@Repository
public interface AccountRepository extends JpaRepository<AccountEntity, String> {
    
    Optional<AccountEntity> findByUserId(Long userId);
    
}