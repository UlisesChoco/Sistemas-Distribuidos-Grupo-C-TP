package com.grupo_c.SistemasDistribuidosTP.serviceImp;

import com.grupo_c.SistemasDistribuidosTP.configuration.security.JwtUtils;
import com.grupo_c.SistemasDistribuidosTP.entity.User;
import com.grupo_c.SistemasDistribuidosTP.exception.*;
import com.grupo_c.SistemasDistribuidosTP.factory.ResponseFactory;
import com.grupo_c.SistemasDistribuidosTP.mapper.UserMapper;
import com.grupo_c.SistemasDistribuidosTP.service.*;
import com.grupo_c.SistemasDistribuidosTP.validator.UserValidator;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class UserServiceGrpcImpl extends UserServiceGrpc.UserServiceImplBase {
    private final JwtUtils jwtUtils;
    private final IRoleService roleService;
    private final IUserService userService;
    public UserServiceGrpcImpl(JwtUtils jwtUtils, IRoleService roleService, IUserService userService) {
        this.jwtUtils = jwtUtils;
        this.roleService = roleService;
        this.userService = userService;
    }
    @Override
    public void login(
            UserServiceClass.LoginRequest request,
            StreamObserver<UserServiceClass.UserWithTokenDTO> responseObserver
    ) {
        User userEntity = userService.findByUsername(request.getUsername());
        try {
            userService.isUserValid(userEntity, request.getPassword());
        } catch (UsernameNotFoundException usernameNotFoundException) {
            sendGrpcError(responseObserver, Status.NOT_FOUND, usernameNotFoundException.getMessage());
            return;
        } catch (InvalidPasswordException invalidPasswordException) {
            sendGrpcError(responseObserver, Status.INVALID_ARGUMENT, invalidPasswordException.getMessage());
            return;
        } catch (UserNotActiveException userNotActiveException) {
            sendGrpcError(responseObserver, Status.FAILED_PRECONDITION, userNotActiveException.getMessage());
            return;
        }

        //dado que manejamos autenticacion con jwt, creamos uno para cada usuario que se loguea en el sistema
        String accessToken = jwtUtils.createToken(userEntity, 1800000L); // token valido por media hora

        responseObserver.onNext(UserMapper.userToUserWithTokenDTO(userEntity, accessToken));
        responseObserver.onCompleted();
    }

    @Override
    public void createUser(
            UserServiceClass.UserWithRolesDTO request,
            StreamObserver<UtilsServiceClass.Response> responseObserver
    ) {
        if(!UserValidator.isUserValid(request)) {
            responseObserver.onNext(ResponseFactory.createResponse(
                    "Registration failed.",
                    false
            ));
            responseObserver.onCompleted();
            return;
        }

        try {
            userService.createUser(request, roleService.findAll());
        } catch (UsernameAlreadyExistsException | EmailAlreadyExistsException | PhoneNumberAlreadyExistsException alreadyExistsException) {
            sendGrpcError(responseObserver, Status.ALREADY_EXISTS, alreadyExistsException.getMessage());
            return;
        }

        responseObserver.onNext(ResponseFactory.createResponse("Registration succeeded.", true));
        responseObserver.onCompleted();
    }

    @Override
    public void modifyUser(
            UserServiceClass.UserWithRolesDTO request,
            StreamObserver<UtilsServiceClass.Response> responseObserver
    ) {
        User userEntity;
        try {
            userEntity = userService.findByUsername(request.getUsername());
        } catch(UsernameNotFoundException usernameNotFoundException) {
            responseObserver.onNext(ResponseFactory.createResponse(
                    "Modification failed. User with specified username does not exist.",
                    false
            ));
            responseObserver.onCompleted();
            return;
        }

        try {
            userService.modifyUser(userEntity, request, roleService.findAll());
        } catch (EmailAlreadyExistsException | PhoneNumberAlreadyExistsException alreadyExistsException) {
            responseObserver.onNext(ResponseFactory.createResponse(
                    alreadyExistsException.getMessage(),
                    false
            ));
            responseObserver.onCompleted();
            return;
        }

        responseObserver.onNext(ResponseFactory.createResponse("Modification succeeded.", true));
        responseObserver.onCompleted();
    }

    @Override
    public void deleteUser(
            UserServiceClass.UserRequest request,
            StreamObserver<UtilsServiceClass.Response> responseObserver
    ) {
        User userEntity;
        try {
            userEntity = userService.findByUsername(request.getUsername());
        } catch(UsernameNotFoundException usernameNotFoundException) {
            responseObserver.onNext(ResponseFactory.createResponse(
                    "Deletion failed. User with specified username does not exist.",
                    false
            ));
            responseObserver.onCompleted();
            return;
        }

        userService.deleteUser(userEntity);

        responseObserver.onNext(ResponseFactory.createResponse("Deletion succeeded.", true));
        responseObserver.onCompleted();
    }

    @Override
    public void getUserList(
            UtilsServiceClass.Empty request,
            StreamObserver<UserServiceClass.UserListResponse> responseObserver
    ) {
        responseObserver.onNext(UserMapper.usersToUserListResponse(userService.findAll()));
        responseObserver.onCompleted();
    }

    @Override
    public void getUser(
            UserServiceClass.UserRequest request,
            StreamObserver<UserServiceClass.UserWithRolesDTO> responseObserver
    ) {
        User userEntity;
        try {
            userEntity = userService.findByUsername(request.getUsername());
        } catch(UsernameNotFoundException usernameNotFoundException) {
            sendGrpcError(responseObserver, Status.NOT_FOUND, usernameNotFoundException.getMessage());
            return;
        }

        responseObserver.onNext(UserMapper.userEntityToUserWithRolesDTO(userEntity));
        responseObserver.onCompleted();
    }

    private void sendGrpcError(StreamObserver<?> responseObserver, Status status, String message) {
        responseObserver.onError(new StatusRuntimeException(status.withDescription(message)));
    }
}
