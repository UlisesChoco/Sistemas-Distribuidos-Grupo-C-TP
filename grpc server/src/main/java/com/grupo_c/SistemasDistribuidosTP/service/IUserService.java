package com.grupo_c.SistemasDistribuidosTP.service;

import com.grupo_c.SistemasDistribuidosTP.entity.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;

public interface IUserService {
    User findByUsername(String username) throws UsernameNotFoundException;
    User findByEmail(String email);
    User findByUsernameOrEmail(String username, String email);
    List<User> findByIsActiveTrue();
    List<User> findByIsActiveFalse();
    List<User> findByNameContainingIgnoreCase(String name);
    List<User> findBySurnameContainingIgnoreCase(String surname);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);
    List<User> findByRoleName(String roleName);
    List<User> findByRoleNames(List<String> roleNames);
    List<User> findAllActiveUsersOrdered();
    long countActiveUsers();
    List<User> findAll();
}
