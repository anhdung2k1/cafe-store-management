package com.example.authentication.repository;

import com.example.authentication.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    @Query("SELECT u FROM UserEntity u WHERE LOWER(u.userName) LIKE LOWER(CONCAT('%', :userName, '%'))")
    List<UserEntity> findByUserNameContains(@Param("userName") String userName);

    @Query("SELECT u FROM UserEntity u WHERE u.userName = :userName")
    Optional<UserEntity> findByUserName(@Param("userName") String userName);
}