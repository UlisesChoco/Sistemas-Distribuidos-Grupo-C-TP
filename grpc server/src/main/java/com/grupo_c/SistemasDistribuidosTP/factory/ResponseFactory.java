package com.grupo_c.SistemasDistribuidosTP.factory;

import com.grupo_c.SistemasDistribuidosTP.service.UserServiceClass;

public class ResponseFactory {
    public static UserServiceClass.Response createResponse(String message, boolean succeeded) {
        return UserServiceClass.Response
                .newBuilder()
                .setMessage(message)
                .setSucceeded(succeeded)
                .build();
    }
}
