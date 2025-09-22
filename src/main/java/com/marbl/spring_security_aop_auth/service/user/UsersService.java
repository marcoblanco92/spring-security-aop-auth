package com.marbl.spring_security_aop_auth.service.user;

import com.marbl.spring_security_aop_auth.dto.user.RegisterUserDto;
import com.marbl.spring_security_aop_auth.entity.role.Roles;
import com.marbl.spring_security_aop_auth.entity.role.RolesEnum;
import com.marbl.spring_security_aop_auth.entity.user.Users;
import com.marbl.spring_security_aop_auth.exception.UserAlreadyExistsException;
import com.marbl.spring_security_aop_auth.mapper.user.UsersMapper;
import com.marbl.spring_security_aop_auth.repository.role.RolesRepository;
import com.marbl.spring_security_aop_auth.repository.user.UsersRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UsersService {

    private final UsersMapper usersMapper;
    private final PasswordEncoder passwordEncoder;
    private final UsersRepository usersRepository;
    private final RolesRepository rolesRepository;


    public void registerUser(@Valid RegisterUserDto registerUserDto) {
        boolean exists = usersRepository.existsByUsernameOrEmail(registerUserDto.getUsername(), registerUserDto.getEmail());
        log.info("Checking registration for username: {}, exists: {}", registerUserDto.getUsername(), exists);

        if (exists) {
            log.warn("Attempt to register already existing user: {}", registerUserDto.getUsername());
            throw new UserAlreadyExistsException("Username or email already in use");
        }

        Users users = usersMapper.toEntity(registerUserDto);
        users.setPassword(passwordEncoder.encode(registerUserDto.getPassword()));
        users.getRoles().add(findUserRole());
        usersRepository.save(users);
        log.info("User {} successfully registered", registerUserDto.getUsername());
    }

    private Roles findUserRole() {
        return rolesRepository.findByRoleName(RolesEnum.ROLE_USER)
                .orElseThrow(() -> new IllegalStateException("Default role ROLE_USER not found in DB"));
    }
}
