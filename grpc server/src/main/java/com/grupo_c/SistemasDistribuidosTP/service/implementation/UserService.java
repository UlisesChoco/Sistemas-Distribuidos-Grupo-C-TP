package com.grupo_c.SistemasDistribuidosTP.service.implementation;

import com.grupo_c.SistemasDistribuidosTP.service.UserServiceClass;
import com.grupo_c.SistemasDistribuidosTP.service.UserServiceGrpc;
import io.grpc.stub.StreamObserver;
import org.springframework.stereotype.Service;

@Service
public class UserService extends UserServiceGrpc.UserServiceImplBase {
    @Override
    public void login(
            UserServiceClass.LoginRequest request,
            StreamObserver<UserServiceClass.UserWithTokenDTO> responseObserver
    ) {

    }
    @Override
    public void createUser(
            UserServiceClass.UserWithRolesDTO request,
            StreamObserver<UserServiceClass.Response> responseObserver
    ) {

    }
    @Override
    public void modifyUser(
            UserServiceClass.UserWithRolesDTO request,
            StreamObserver<UserServiceClass.Response> responseObserver
    ) {

    }
    @Override
    public void deleteUser(
            UserServiceClass.UserRequest request,
            StreamObserver<UserServiceClass.Response> responseObserver
    ) {

    }
    @Override
    public void getUserList(
            UserServiceClass.Empty request,
            StreamObserver<UserServiceClass.UserListResponse> responseObserver
    ) {

    }
    @Override
    public void getUser(
            UserServiceClass.UserRequest request,
            StreamObserver<UserServiceClass.UserWithRolesDTO> responseObserver
    ) {

    }
}
