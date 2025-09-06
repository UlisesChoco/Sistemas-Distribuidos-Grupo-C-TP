package com.grupo_c.SistemasDistribuidosTP.service;

import com.grupo_c.SistemasDistribuidosTP.entity.Role;
import com.grupo_c.SistemasDistribuidosTP.entity.User;
import com.grupo_c.SistemasDistribuidosTP.exception.*;
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
    boolean existsByEmailAndUsernameNot(String email, String username);
    boolean existsByPhoneNumberAndUsernameNot(String phoneNumber, String username);
    List<User> findByRoleName(String roleName);
    List<User> findByRoleNames(List<String> roleNames);
    List<User> findAllActiveUsersOrdered();
    long countActiveUsers();
    List<User> findAll();
    User save(User userEntity);
    void isUserValid(User userEntity, String password) throws UsernameNotFoundException, InvalidPasswordException, UserNotActiveException;
    void createUser(UserServiceClass.UserWithRolesDTO userWithRolesDTO, List<Role> rolesFromDB) throws UsernameAlreadyExistsException, EmailAlreadyExistsException, PhoneNumberAlreadyExistsException;
    void modifyUser(User userEntity, UserServiceClass.UserWithRolesDTO userWithRolesDTO, List<Role> rolesFromDB) throws EmailAlreadyExistsException, PhoneNumberAlreadyExistsException;
    void deleteUser(User userEntity);
}
