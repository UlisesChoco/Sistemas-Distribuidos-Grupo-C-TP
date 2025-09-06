package com.grupo_c.SistemasDistribuidosTP.configuration.security;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.grupo_c.SistemasDistribuidosTP.constants.RoleEnum;
import io.grpc.*;
import org.springframework.grpc.server.GlobalServerInterceptor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import java.util.*;

@Component
@GlobalServerInterceptor
public class GrpcServerInterceptor implements ServerInterceptor {
    private final JwtTokenAuthenticationProvider jwtTokenAuthenticationProvider;
    private final Map<String, List<String>> rolesAndMethods;
    private final Set<String> publicMethods;

    public GrpcServerInterceptor(JwtTokenAuthenticationProvider jwtTokenAuthenticationProvider) {
        this.jwtTokenAuthenticationProvider = jwtTokenAuthenticationProvider;
        this.rolesAndMethods = initRolesAndMethods();
        this.publicMethods = initPublicMethods();
    }

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> serverCall, Metadata metadata, ServerCallHandler<ReqT, RespT> serverCallHandler) {
        String incomingMethod = serverCall.getMethodDescriptor().getFullMethodName();
        if(publicMethods.contains(incomingMethod)) {
            return serverCallHandler.startCall(serverCall, metadata);
        }

        Metadata.Key<String> authKey = Metadata.Key.of("Authorization", Metadata.ASCII_STRING_MARSHALLER);
        String token = metadata.get(authKey);
        JwtAuthentication jwtAuthentication;
        try {
            jwtAuthentication = jwtTokenAuthenticationProvider.authenticate(new JwtAuthentication(token));
        } catch (AuthenticationException authenticationException) {
            serverCall.close(Status.UNAUTHENTICATED.withDescription("Failed to authenticate JWT. JWT can not be null or empty."), metadata);
            return new ServerCall.Listener<>() {};
        } catch (JWTVerificationException jwtVerificationException) {
            serverCall.close(Status.UNAUTHENTICATED.withDescription("Failed to authenticate JWT. JWT not valid."), metadata);
            return new ServerCall.Listener<>() {};
        }

        if(incomingCallDoesNotHaveEnoughPermissions(incomingMethod, jwtAuthentication)) {
            serverCall.close(Status.PERMISSION_DENIED.withDescription("You do not have enough permissions to execute this call."), metadata);
            return new ServerCall.Listener<>() {};
        }

        return serverCallHandler.startCall(serverCall, metadata);
    }

    private boolean incomingCallDoesNotHaveEnoughPermissions(String incomingMethod, JwtAuthentication jwtAuthentication) {
        boolean doesNotHaveEnoughPermissions = true;
        for(String role : jwtAuthentication.getRoles()) {
            List<String> permissionsByGivenRole = rolesAndMethods.get(role);
            if(permissionsByGivenRole.contains(incomingMethod)) {
                doesNotHaveEnoughPermissions = false;
                break;
            }
        }
        return doesNotHaveEnoughPermissions;
    }

    private Map<String, List<String>> initRolesAndMethods() {
        Map<String, List<String>> rolesAndMethods = new HashMap<>();

        // metodos a los que pueden acceder distintos roles
        rolesAndMethods.put(
                RoleEnum.PRESIDENTE.name(),
                List.of(
                        "UserService/CreateUser",
                        "UserService/ModifyUser",
                        "UserService/DeleteUser",
                        "UserService/GetUserList",
                        "UserService/GetUser"
                )
        );
        rolesAndMethods.put(
                RoleEnum.VOCAL.name(),
                List.of()
        );
        rolesAndMethods.put(
                RoleEnum.COORDINADOR.name(),
                List.of()
        );
        rolesAndMethods.put(
                RoleEnum.VOLUNTARIO.name(),
                List.of()
        );

        return rolesAndMethods;
    }

    private Set<String> initPublicMethods() {
        // queda para agregar mas metodos publicos a futuro (si los hay)
        return Set.of(
                "UserService/Login"
        );
    }
}
