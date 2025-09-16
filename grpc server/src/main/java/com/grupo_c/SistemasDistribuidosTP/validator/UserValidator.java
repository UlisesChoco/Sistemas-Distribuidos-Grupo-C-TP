package com.grupo_c.SistemasDistribuidosTP.validator;

import com.grupo_c.SistemasDistribuidosTP.exception.user.UserNotValidException;
import com.grupo_c.SistemasDistribuidosTP.service.UserServiceClass;

import java.util.regex.Pattern;

public class UserValidator {
    private static final String EMAIL_REGEX = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
    public static void isUserValid(UserServiceClass.UserWithRolesDTO request) throws UserNotValidException {
        if(request.getUsername().isBlank()) throw new UserNotValidException("Username can not be empty.");
        if(request.getName().isBlank()) throw new UserNotValidException("Name can not be empty.");
        if(request.getSurname().isBlank()) throw new UserNotValidException("Surname can not be empty.");
        if(request.getPhoneNumber().isBlank()) throw new UserNotValidException("Phone number can not be empty.");
        if(request.getEmail().isBlank()) throw new UserNotValidException("Email can not be empty.");
        if(emailIsNotValid(request.getEmail())) throw new UserNotValidException("Email format is not valid. Example: email@domain.com");
        if(request.getRolesList().isEmpty()) throw new UserNotValidException("User should at least have one role.");
    }
    public static boolean emailIsNotValid(String email) {
        return !Pattern.matches(EMAIL_REGEX, email);
    }
}
