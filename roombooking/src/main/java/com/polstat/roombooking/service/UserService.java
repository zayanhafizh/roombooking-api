package com.polstat.roombooking.service;

import com.polstat.roombooking.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public void deleteUserById(Long id) {
        userRepository.deleteById(id);
    }
}
