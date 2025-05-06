package com.example.authentication.service.implement;

import com.amazonaws.services.s3.AmazonS3;
import com.example.authentication.entity.UserEntity;
import com.example.authentication.model.Users;
import com.example.authentication.repository.UserRepository;
import com.example.authentication.service.interfaces.UserService;
import com.example.authentication.utils.S3Utils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional(rollbackOn = Exception.class)
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final S3Utils s3Utils;
    private final UserRepository userRepository;
    private final AmazonS3 s3Client;

    @Value("${bucket.name}")
    public String bucketName;

    private Map<String, Object> userMap(UserEntity userEntity) {
        return new HashMap<>() {{
            put("id", userEntity.getUser_id());
            put("userName", userEntity.getUserName());
            put("address", userEntity.getAddress());
            put("birthDay", userEntity.getBirthDay());
            put("gender", userEntity.getGender());
            put("imageUrl", Optional.ofNullable(userEntity.getImageUrl()).orElse(""));
        }};
    }

    @Override
    public Users createUsers(Users user) throws Exception {
        try {
            UserEntity userEntity = new UserEntity();
            user.setCreateAt(LocalDateTime.now());
            user.setUpdateAt(LocalDateTime.now());

            if (user.getImageUrl() != null && !user.getImageUrl().isEmpty()) {
                URL objectURL = s3Utils.getS3URL(user.getImageUrl());
                user.setImageUrl(objectURL.toString());
            }

            BeanUtils.copyProperties(user, userEntity);
            userRepository.save(userEntity);
            return user;
        } catch (Exception e) {
            throw new Exception("Failed to create user: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean deleteUser(Long id) throws Exception {
        try {
            UserEntity user = userRepository.findById(id)
                    .orElseThrow(() -> new NoSuchElementException("User not found: " + id));

            String imageUrl = user.getImageUrl();
            if (imageUrl != null && !imageUrl.isEmpty()) {
                String fileName = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
                s3Client.deleteObject(bucketName, fileName);
                log.info("Deleted image from S3: {}", fileName);
            }

            userRepository.delete(user);
            return true;
        } catch (Exception e) {
            throw new Exception("Could not delete user: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Map<String, Object>> getAllUsers() {
        List<UserEntity> userEntities = userRepository.findAll();
        List<Map<String, Object>> output = new ArrayList<>();
        userEntities.forEach(user -> output.add(userMap(user)));
        return output;
    }

    @Override
    public Map<String, Object> getUserById(Long id) throws Exception {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("User not found: " + id));
        return userMap(user);
    }

    @Override
    public List<Map<String, Object>> getUserByName(String userName) throws Exception {
        try {
            List<UserEntity> userEntities = userRepository.findByUserNameContains(userName);
            List<Map<String, Object>> output = new ArrayList<>();
            userEntities.forEach(user -> output.add(userMap(user)));
            return output;
        } catch (Exception e) {
            throw new Exception("Failed to get users by name: " + userName, e);
        }
    }

    @Override
    public Map<String, Long> getUserIdByUserName(String userName) throws Exception {
        UserEntity user = userRepository.findByUserName(userName)
                .orElseThrow(() -> new NoSuchElementException("User not found: " + userName));
        Map<String, Long> output = new HashMap<>();
        output.put("id", user.getUser_id());
        return output;
    }

    @Override
    public Users updateUser(Long id, Users user) throws Exception {
        try {
            UserEntity userEntity = userRepository.findById(id)
                    .orElseThrow(() -> new NoSuchElementException("User not found: " + id));

            userEntity.setAddress(user.getAddress());
            userEntity.setBirthDay(user.getBirthDay());
            userEntity.setGender(user.getGender());
            userEntity.setUpdateAt(LocalDateTime.now());

            if (user.getImageUrl() != null && !user.getImageUrl().isEmpty()) {
                URL objectURL = s3Utils.getS3URL(user.getImageUrl());
                userEntity.setImageUrl(objectURL.toString());
            }

            userRepository.save(userEntity);
            BeanUtils.copyProperties(userEntity, user);
            return user;
        } catch (Exception e) {
            throw new Exception("Could not update user: " + e.getMessage(), e);
        }
    }
}