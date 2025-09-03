package com.grupo_c.SistemasDistribuidosTP.serviceImp;

import com.grupo_c.SistemasDistribuidosTP.configuration.security.JwtUtils;
import com.grupo_c.SistemasDistribuidosTP.entity.Role;
import com.grupo_c.SistemasDistribuidosTP.entity.User;
import com.grupo_c.SistemasDistribuidosTP.exception.*;
import com.grupo_c.SistemasDistribuidosTP.mapper.UserMapper;
import com.grupo_c.SistemasDistribuidosTP.repository.IUserRepository;
import com.grupo_c.SistemasDistribuidosTP.service.IRoleService;
import com.grupo_c.SistemasDistribuidosTP.service.IUserService;
import com.grupo_c.SistemasDistribuidosTP.service.UserServiceClass;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class UserServiceImpl implements IUserService {
    private final IUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(
            IUserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtUtils jwtUtils,
            IRoleService roleService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User findByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username).orElseThrow(() ->
                new UsernameNotFoundException("User with specified username does not exist.")
        );
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    @Override
    public User findByUsernameOrEmail(String username, String email) {
        return userRepository.findByUsernameOrEmail(username, email).orElse(null);
    }

    @Override
    public List<User> findByIsActiveTrue() {
        return userRepository.findByIsActiveTrue();
    }

    @Override
    public List<User> findByIsActiveFalse() {
        return userRepository.findByIsActiveFalse();
    }

    @Override
    public List<User> findByNameContainingIgnoreCase(String name) {
        return userRepository.findByNameContainingIgnoreCase(name);
    }

    @Override
    public List<User> findBySurnameContainingIgnoreCase(String surname) {
        return userRepository.findBySurnameContainingIgnoreCase(surname);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public boolean existsByPhoneNumber(String phoneNumber) {
        return userRepository.existsByPhoneNumber(phoneNumber);
    }

    @Override
    public boolean existsByEmailAndUsernameNot(String email, String username) {
        return userRepository.existsByEmailAndUsernameNot(email, username);
    }

    @Override
    public boolean existsByPhoneNumberAndUsernameNot(String phoneNumber, String username) {
        return userRepository.existsByPhoneNumberAndUsernameNot(phoneNumber, username);
    }

    @Override
    public List<User> findByRoleName(String roleName) {
        return userRepository.findByRoleName(roleName);
    }

    @Override
    public List<User> findByRoleNames(List<String> roleNames) {
        return userRepository.findByRoleNames(roleNames);
    }

    @Override
    public List<User> findAllActiveUsersOrdered() {
        return userRepository.findAllActiveUsersOrdered();
    }

    @Override
    public long countActiveUsers() {
        return userRepository.countActiveUsers();
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public User save(User userEntity) {
        return userRepository.save(userEntity);
    }

    @Override
    public void isUserValid(User userEntity, String password) throws UsernameNotFoundException, InvalidPasswordException, UserNotActiveException {
        if(userEntity == null) throw new UsernameNotFoundException("User with specified username does not exist.");
        if(!passwordEncoder.matches(password, userEntity.getPassword())) throw new InvalidPasswordException("Invalid password.");
        if(!userEntity.getIsActive()) throw new UserNotActiveException("This user has been marked as inactive.");
    }

    @Override
    public void createUser(UserServiceClass.UserWithRolesDTO userWithRolesDTO, List<Role> rolesFromDB) throws UsernameAlreadyExistsException, EmailAlreadyExistsException, PhoneNumberAlreadyExistsException {
        if(existsByUsername(userWithRolesDTO.getUsername())) throw new UsernameAlreadyExistsException("Registration failed. Username already taken.");
        if(existsByEmail(userWithRolesDTO.getEmail())) throw new EmailAlreadyExistsException("Registration failed. Email already taken.");
        if(existsByPhoneNumber(userWithRolesDTO.getPhoneNumber())) throw new PhoneNumberAlreadyExistsException("Registration failed. Phone number already taken.");

        User userEntity = new User();
        String generatedPassword = UUID.randomUUID().toString();
        //todo: REMOVER ESTE PRINT EN LA VERSION FINAL. simplemente lo dejo para saber que password esta generando para facilitar pruebas
        System.out.println("Creating new user with this password: "+generatedPassword);
        UserMapper.userWithRolesDTOToUser(userWithRolesDTO, userEntity, rolesFromDB);
        userEntity.setPassword(passwordEncoder.encode(generatedPassword));
        save(userEntity);
    }

    @Override
    public void modifyUser(User userEntity, UserServiceClass.UserWithRolesDTO userWithRolesDTO, List<Role> rolesFromDB) throws EmailAlreadyExistsException, PhoneNumberAlreadyExistsException {
        if(existsByEmailAndUsernameNot(userEntity.getEmail(), userEntity.getUsername())) if(existsByEmail(userWithRolesDTO.getEmail())) throw new EmailAlreadyExistsException("Modification failed. Email already taken.");
        if(existsByPhoneNumberAndUsernameNot(userWithRolesDTO.getPhoneNumber(), userEntity.getUsername())) throw new PhoneNumberAlreadyExistsException("Modification failed. Phone number already taken.");

        UserMapper.userWithRolesDTOToUser(userWithRolesDTO, userEntity, rolesFromDB);
        userEntity.setModificationDate(LocalDateTime.now());
        save(userEntity);
    }

    @Override
    public void deleteUser(User userEntity) {
        userEntity.setIsActive(false);
        save(userEntity);
    }
}
