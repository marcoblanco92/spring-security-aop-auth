package com.marbl.spring_security_aop_auth.service.user;

import com.marbl.spring_security_aop_auth.dto.user.RegisterDto;
import com.marbl.spring_security_aop_auth.entity.role.Roles;
import com.marbl.spring_security_aop_auth.entity.role.RolesEnum;
import com.marbl.spring_security_aop_auth.entity.user.Users;
import com.marbl.spring_security_aop_auth.exception.UserAlreadyExistsException;
import com.marbl.spring_security_aop_auth.mapper.user.UsersMapper;
import com.marbl.spring_security_aop_auth.repository.role.RolesRepository;
import com.marbl.spring_security_aop_auth.repository.user.UsersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.marbl.spring_security_aop_auth.utils.PrivacyUtils.maskUsername;

@Slf4j
@Service
@RequiredArgsConstructor
public class UsersService {

    private final UsersMapper usersMapper;
    private final PasswordEncoder passwordEncoder;
    private final UsersRepository usersRepository;
    private final RolesRepository rolesRepository;

    @Transactional
    public void register(RegisterDto registerBaseDto, RolesEnum rolesEnum) {
        boolean exists = usersRepository.existsByUsernameOrEmail(registerBaseDto.getUsername(), registerBaseDto.getEmail());
        log.info("Checking registration for username: {}, exists: {}", maskUsername(registerBaseDto.getUsername()), exists);

        if (exists) {
            log.warn("Attempt to register already existing user: {}", registerBaseDto.getUsername());
            throw new UserAlreadyExistsException("Username or email already in use");
        }

        Users users = usersMapper.toEntity(registerBaseDto);
        users.setPassword(passwordEncoder.encode(registerBaseDto.getPassword()));
        users.getRoles().add(findUserRole(rolesEnum));
        usersRepository.save(users);
        log.info("User {} successfully registered", registerBaseDto.getUsername());
    }

    private Roles findUserRole(RolesEnum role) {
        return rolesRepository.findByRoleName(role)
                .orElseThrow(() -> new IllegalStateException("Default role ROLE_USER not found in DB"));
    }
}
