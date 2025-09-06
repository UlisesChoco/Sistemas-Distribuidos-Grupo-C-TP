package com.grupo_c.SistemasDistribuidosTP.mapper;

import com.grupo_c.SistemasDistribuidosTP.entity.Role;
import com.grupo_c.SistemasDistribuidosTP.entity.User;
import com.grupo_c.SistemasDistribuidosTP.service.UserServiceClass;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UserMapper {
    public static Set<UserServiceClass.RoleDTO> roleEntityToRoleDTO(Set<String> roles) {
        Set<UserServiceClass.RoleDTO> rolesDTO = new HashSet<>();
        for(String role : roles)
            rolesDTO.add(UserServiceClass.RoleDTO.newBuilder().setName(role).build());
        return rolesDTO;
    }

    public static UserServiceClass.UserWithRolesDTO userEntityToUserWithRolesDTO(User userEntity) {
        return UserServiceClass.UserWithRolesDTO
                .newBuilder()
                .setUsername(userEntity.getUsername())
                .setName(userEntity.getName())
                .setSurname(userEntity.getSurname())
                .setPhoneNumber(userEntity.getPhoneNumber())
                .setEmail(userEntity.getEmail())
                .setIsActive(userEntity.getIsActive())
                .addAllRoles(roleEntityToRoleDTO((userEntity.getRolesAsStrings())))
                .build();
    }

    public static Set<Role> rolesDTOToRoles(List<UserServiceClass.RoleDTO> rolesDTO, List<Role> rolesFromDB) {
        Set<Role> roles = new HashSet<>();
        for(UserServiceClass.RoleDTO roleDTO : rolesDTO) {
            for(Role roleFromDB : rolesFromDB) {
                if(roleDTO.getName().equals(roleFromDB.getName())) {
                    roles.add(roleFromDB);
                    break;
                }
            }
        }
        return roles;
    }

    public static void userWithRolesDTOToUser(UserServiceClass.UserWithRolesDTO userWithRolesDTO, User userEntity, List<Role> rolesFromDB) {
        userEntity.setUsername(userWithRolesDTO.getUsername());
        userEntity.setName(userWithRolesDTO.getName());
        userEntity.setSurname(userWithRolesDTO.getSurname());
        userEntity.setPhoneNumber(userWithRolesDTO.getPhoneNumber());
        userEntity.setEmail(userWithRolesDTO.getEmail());
        userEntity.setIsActive(userWithRolesDTO.getIsActive());
        userEntity.setRoles(rolesDTOToRoles(userWithRolesDTO.getRolesList(), rolesFromDB));
    }

    public static UserServiceClass.UserListResponse usersToUserListResponse(List<User> users) {
        List<UserServiceClass.UserWithRolesDTO> usersWithRolesDTO = new ArrayList<>();
        for(User user : users) {
            usersWithRolesDTO.add(UserMapper.userEntityToUserWithRolesDTO(user));
        }

        return UserServiceClass.UserListResponse
                .newBuilder()
                .addAllUsers(usersWithRolesDTO)
                .build();
    }

    public static UserServiceClass.UserWithTokenDTO userToUserWithTokenDTO(User userEntity, String token) {
        return UserServiceClass.UserWithTokenDTO
                .newBuilder()
                .setUserWithRolesDTO(userEntityToUserWithRolesDTO(userEntity))
                .setToken(token)
                .build();
    }
}
