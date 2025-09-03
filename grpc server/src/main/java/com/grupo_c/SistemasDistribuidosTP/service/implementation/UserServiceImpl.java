package com.grupo_c.SistemasDistribuidosTP.service.implementation;

import com.grupo_c.SistemasDistribuidosTP.configuration.security.JwtUtils;
import com.grupo_c.SistemasDistribuidosTP.entity.Role;
import com.grupo_c.SistemasDistribuidosTP.entity.User;
import com.grupo_c.SistemasDistribuidosTP.repository.IUserRepository;
import com.grupo_c.SistemasDistribuidosTP.service.IRoleService;
import com.grupo_c.SistemasDistribuidosTP.service.IUserService;
import com.grupo_c.SistemasDistribuidosTP.service.grpc_generated.UserServiceClass;
import com.grupo_c.SistemasDistribuidosTP.service.grpc_generated.UserServiceGrpc;
import io.grpc.stub.StreamObserver;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserServiceImpl extends UserServiceGrpc.UserServiceImplBase implements IUserService {
    private final IUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final IRoleService roleService;

    public UserServiceImpl(
            IUserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtUtils jwtUtils,
            IRoleService roleService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
        this.roleService = roleService;
    }

    @Override
    public User findByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username).orElseThrow(() ->
                new UsernameNotFoundException("User of username '" + username + "' does not exists.")
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
    public void login(
            UserServiceClass.LoginRequest request,
            StreamObserver<UserServiceClass.UserWithTokenDTO> responseObserver
    ) {
        String username = request.getUsername();
        String password = request.getPassword();


        User userEntity = findByUsername(username);

        if (userEntity == null)
            throw new UsernameNotFoundException("Couldn't find user of username '" + username + "'.");

        if (!passwordEncoder.matches(password, userEntity.getPassword()))
            throw new BadCredentialsException("Password does not match.");

        if (!userEntity.getIsActive())
            throw new UsernameNotFoundException("Couldn't log in. User of username '"+username+"' has been marked as inactive.");

        //dado que manejamos autenticacion con jwt, creamos uno para cada usuario que se loguea en el sistema
        String accessToken = jwtUtils.createToken(userEntity, 1800000L); // token valido por media hora

        Set<UserServiceClass.RoleDTO> userRoles = new HashSet<>();
        for(String role : userEntity.getRolesAsStrings())
            userRoles.add(UserServiceClass.RoleDTO.newBuilder().setName(role).build());

        UserServiceClass.UserWithRolesDTO userWithRolesDTO = UserServiceClass.UserWithRolesDTO
                .newBuilder()
                .setUsername(userEntity.getUsername())
                .setName(userEntity.getName())
                .setSurname(userEntity.getSurname())
                .setPhoneNumber(userEntity.getPhoneNumber())
                .setEmail(userEntity.getEmail())
                .setIsActive(userEntity.getIsActive())
                .addAllRoles(userRoles)
                .build();

        UserServiceClass.UserWithTokenDTO userWithTokenDTO = UserServiceClass.UserWithTokenDTO
                .newBuilder()
                .setUserWithRolesDTO(userWithRolesDTO)
                .setToken(accessToken)
                .build();

        responseObserver.onNext(userWithTokenDTO);
        responseObserver.onCompleted();
    }

    @Override
    public void createUser(
            UserServiceClass.UserWithRolesDTO request,
            StreamObserver<UserServiceClass.Response> responseObserver
    ) {
        if(!isUserValid(request, responseObserver)) return;

        Set<Role> userRoles = new HashSet<>();
        List<Role> rolesFromDB = roleService.findAll();

        for(UserServiceClass.RoleDTO roleDTO : request.getRolesList()) {
            for (Role roleEntity : rolesFromDB) {
                if (roleEntity.getName().equals(roleDTO.getName())) {
                    userRoles.add(roleEntity);
                    break;
                }
            }
        }

        User userEntity = new User();
        userEntity.setUsername(request.getUsername());
        userEntity.setName(request.getName());
        userEntity.setSurname(request.getSurname());
        userEntity.setPhoneNumber(request.getPhoneNumber());
        userEntity.setPassword(passwordEncoder.encode("1234")); // en realidad deberia generar una password aleatoria
        userEntity.setEmail(request.getEmail());
        userEntity.setIsActive(request.getIsActive());
        userEntity.setRoles(userRoles);

        userRepository.save(userEntity);

        UserServiceClass.Response response = UserServiceClass.Response
                .newBuilder()
                .setMessage("Registration succeeded.")
                .setSucceeded(true)
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void modifyUser(
            UserServiceClass.UserWithRolesDTO request,
            StreamObserver<UserServiceClass.Response> responseObserver
    ) {
        User userEntity = userRepository.findByUsername(request.getUsername()).orElse(null);

        if(userDoesNotExist(request, responseObserver, userEntity)) return;

        assert userEntity != null;
        userEntity.setUsername(request.getUsername());
        userEntity.setName(request.getName());
        userEntity.setSurname(request.getSurname());
        userEntity.setPhoneNumber(request.getPhoneNumber());
        userEntity.setEmail(request.getEmail());
        userEntity.setIsActive(request.getIsActive());
        userEntity.setModificationDate(LocalDateTime.now());

        userRepository.save(userEntity);

        UserServiceClass.Response response = UserServiceClass.Response
                .newBuilder()
                .setMessage("Modification succeeded.")
                .setSucceeded(true)
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void deleteUser(
            UserServiceClass.UserRequest request,
            StreamObserver<UserServiceClass.Response> responseObserver
    ) {
        User userEntity = userRepository.findByUsername(request.getUsername()).orElse(null);

        if(userDoesNotExist(request, responseObserver, userEntity)) return;

        assert userEntity != null;
        userEntity.setIsActive(false);

        userRepository.save(userEntity);

        UserServiceClass.Response response = UserServiceClass.Response
                .newBuilder()
                .setMessage("Deletion succeeded.")
                .setSucceeded(true)
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void getUserList(
            UserServiceClass.Empty request,
            StreamObserver<UserServiceClass.UserListResponse> responseObserver
    ) {
        List<User> usersEntities = userRepository.findAll();
        List<UserServiceClass.RoleDTO> rolesDTO = new ArrayList<>();
        List<UserServiceClass.UserWithRolesDTO> usersWithRolesDTO = new ArrayList<>();
        for(User user : usersEntities) {
            for(Role role : user.getRoles()) {
                UserServiceClass.RoleDTO roleDTO = UserServiceClass.RoleDTO
                        .newBuilder()
                        .setName(role.getName())
                        .build();
                rolesDTO.add(roleDTO);
            }
            UserServiceClass.UserWithRolesDTO userWithRolesDTO = UserServiceClass.UserWithRolesDTO
                    .newBuilder()
                    .setUsername(user.getUsername())
                    .setName(user.getName())
                    .setSurname(user.getSurname())
                    .setPhoneNumber(user.getPhoneNumber())
                    .setEmail(user.getEmail())
                    .setIsActive(user.getIsActive())
                    .addAllRoles(rolesDTO)
                    .build();
            usersWithRolesDTO.add(userWithRolesDTO);
            rolesDTO.clear();
        }

        UserServiceClass.UserListResponse userListResponse = UserServiceClass.UserListResponse
                .newBuilder()
                .addAllUsers(usersWithRolesDTO)
                .build();
        responseObserver.onNext(userListResponse);
        responseObserver.onCompleted();
    }

    @Override
    public void getUser(
            UserServiceClass.UserRequest request,
            StreamObserver<UserServiceClass.UserWithRolesDTO> responseObserver
    ) {
        User userEntity = userRepository.findByUsername(request.getUsername()).orElse(null);

        if(userEntity == null) {
            responseObserver.onNext(null);
            responseObserver.onCompleted();
            return;
        }

        List<UserServiceClass.RoleDTO> rolesDTO = new ArrayList<>();
        for (Role role : userEntity.getRoles()) {
            UserServiceClass.RoleDTO roleDTO = UserServiceClass.RoleDTO
                    .newBuilder()
                    .setName(role.getName())
                    .build();
            rolesDTO.add(roleDTO);
        }

        UserServiceClass.UserWithRolesDTO userWithRolesDTO = UserServiceClass.UserWithRolesDTO
                .newBuilder()
                .setUsername(userEntity.getUsername())
                .setName(userEntity.getName())
                .setSurname(userEntity.getSurname())
                .setPhoneNumber(userEntity.getPhoneNumber())
                .setEmail(userEntity.getEmail())
                .setIsActive(userEntity.getIsActive())
                .addAllRoles(rolesDTO)
                .build();
        responseObserver.onNext(userWithRolesDTO);
        responseObserver.onCompleted();
    }

    private boolean isUserValid(
            UserServiceClass.UserWithRolesDTO request,
            StreamObserver<UserServiceClass.Response> responseObserver
    ) {
        boolean usernameIsAlreadyTaken = userRepository.existsByUsername(request.getUsername());
        boolean phoneNumberIsAlreadyTaken = userRepository.existsByPhoneNumber(request.getPhoneNumber());
        boolean emailIsAlreadyTaken = userRepository.existsByEmail(request.getEmail());

        if(usernameIsAlreadyTaken) {
            UserServiceClass.Response response = UserServiceClass.Response
                    .newBuilder()
                    .setMessage("Registration failed. Username '"+request.getUsername()+"' already in use.")
                    .setSucceeded(false)
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
            return false;
        }

        if(phoneNumberIsAlreadyTaken) {
            UserServiceClass.Response response = UserServiceClass.Response
                    .newBuilder()
                    .setMessage("Registration failed. Phone number '"+request.getPhoneNumber()+"' already in use.")
                    .setSucceeded(false)
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
            return false;
        }

        if(emailIsAlreadyTaken) {
            UserServiceClass.Response response = UserServiceClass.Response
                    .newBuilder()
                    .setMessage("Registration failed. Email '"+request.getEmail()+"' already in use.")
                    .setSucceeded(false)
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
            return false;
        }

        return true;
    }

    private boolean userDoesNotExist(
            UserServiceClass.UserWithRolesDTO request,
            StreamObserver<UserServiceClass.Response> responseObserver,
            User userEntity
    ) {
        if(userEntity == null) {
            UserServiceClass.Response response = UserServiceClass.Response
                    .newBuilder()
                    .setMessage("Modification failed. User with username '"+request.getUsername()+"' and email '"+request.getEmail()+"' does not exists.")
                    .setSucceeded(false)
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
            return true;
        }

        return false;
    }

    private boolean userDoesNotExist(
            UserServiceClass.UserRequest request,
            StreamObserver<UserServiceClass.Response> responseObserver,
            User userEntity
    ) {
        if(userEntity == null) {
            UserServiceClass.Response response = UserServiceClass.Response
                    .newBuilder()
                    .setMessage("Deletion failed. User with username '"+request.getUsername()+"' does not exists.")
                    .setSucceeded(false)
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
            return true;
        }

        return false;
    }
}
