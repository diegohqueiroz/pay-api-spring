package com.pay.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pay.models.UserEntity;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
        
}