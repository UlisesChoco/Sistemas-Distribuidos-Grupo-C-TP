package com.grupo_c.SistemasDistribuidosTP.validator;

import com.grupo_c.SistemasDistribuidosTP.service.UserServiceClass;

public class UserValidator {
    public static boolean isUserValid(UserServiceClass.UserWithRolesDTO request) {
        if(request.getUsername().isBlank()) return false;
        if(request.getName().isBlank()) return false;
        if(request.getSurname().isBlank()) return false;
        if(request.getPhoneNumber().isBlank()) return false;
        if(request.getEmail().isBlank()) return false;
        if(request.getRolesList().isEmpty()) return false;
        return true;
    }
}
