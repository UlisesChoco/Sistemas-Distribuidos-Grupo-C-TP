package com.grupo_c.SistemasDistribuidosTP.validator;

import com.grupo_c.SistemasDistribuidosTP.exception.user.UserNotValidException;
import com.grupo_c.SistemasDistribuidosTP.service.UserServiceClass;

import java.util.regex.Pattern;

public class UserValidator {
    private static final String EMAIL_REGEX = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
    public static void isUserValid(UserServiceClass.UserWithRolesDTO request) throws UserNotValidException {
        if(request.getUsername().isBlank()) throw new UserNotValidException("El nombre de usuario no puede estar vacío.");
        if(request.getName().isBlank()) throw new UserNotValidException("El nombre del usuario no puede estar vacío.");
        if(request.getSurname().isBlank()) throw new UserNotValidException("El apellido del usuario no puede estar vacío.");
        if(request.getPhoneNumber().isBlank()) throw new UserNotValidException("El número de telefono no puede estar vacío.");
        if(request.getEmail().isBlank()) throw new UserNotValidException("El email no puede estar vacío.");
        if(emailIsNotValid(request.getEmail())) throw new UserNotValidException("Formato de email incorrecto. Ejemplo con formato válido: email@dominio.com");
        if(request.getRolesList().isEmpty()) throw new UserNotValidException("El usuario debe tener asignado al menos un rol.");
    }
    public static boolean emailIsNotValid(String email) {
        return !Pattern.matches(EMAIL_REGEX, email);
    }
}
