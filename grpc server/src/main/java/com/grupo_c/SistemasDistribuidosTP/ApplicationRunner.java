package com.grupo_c.SistemasDistribuidosTP;

import com.grupo_c.SistemasDistribuidosTP.constants.RoleEnum;
import com.grupo_c.SistemasDistribuidosTP.entity.Role;
import com.grupo_c.SistemasDistribuidosTP.entity.User;
import com.grupo_c.SistemasDistribuidosTP.service.IRoleService;
import com.grupo_c.SistemasDistribuidosTP.service.IUserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Component
public class ApplicationRunner implements CommandLineRunner {
    private final IRoleService roleService;
    private final IUserService userService;
    private final PasswordEncoder passwordEncoder;
    public ApplicationRunner(IRoleService roleService, IUserService userService, PasswordEncoder passwordEncoder) {
        this.roleService = roleService;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }
    @Override
    public void run(String... args) throws Exception {
        if(roleService.findAll().isEmpty()) {
            Set<Role> roles = new HashSet<>();
            roles.add(new Role(RoleEnum.VOLUNTARIO.name()));
            roles.add(new Role(RoleEnum.COORDINADOR.name()));
            roles.add(new Role(RoleEnum.VOCAL.name()));
            roles.add(new Role(RoleEnum.PRESIDENTE.name()));
            roleService.saveAll(roles);
        }
        if(userService.findAll().isEmpty()) {
            Role role = roleService.findByName(RoleEnum.PRESIDENTE.name());
            User user = new User();
            user.setUsername("Presidente");
            user.setName("Tomás");
            user.setSurname("Lopez");
            user.setPhoneNumber("1143763389");
            user.setPassword(passwordEncoder.encode("admin"));
            user.setEmail("tomaslopez1987@gmail.com");
            user.setIsActive(true);
            user.setRoles(Set.of(role));
            userService.save(user);
        }
    }
}
