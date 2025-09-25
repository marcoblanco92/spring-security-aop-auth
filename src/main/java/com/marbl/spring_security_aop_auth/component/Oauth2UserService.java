package com.marbl.spring_security_aop_auth.component;

import com.marbl.spring_security_aop_auth.entity.role.Roles;
import com.marbl.spring_security_aop_auth.entity.role.RolesEnum;
import com.marbl.spring_security_aop_auth.entity.user.AuthProvider;
import com.marbl.spring_security_aop_auth.entity.user.Users;
import com.marbl.spring_security_aop_auth.repository.role.RolesRepository;
import com.marbl.spring_security_aop_auth.repository.user.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class Oauth2UserService {

    private final UsersRepository userRepository;
    private final RolesRepository rolesRepository;

    @Transactional
    public Users processOauthPostLogin(OAuth2User oauth2User, AuthProvider provider) {
        String oauthId = oauth2User.getName();
        String email = oauth2User.getAttribute("email");

        return userRepository.findByProviderAndProviderId(provider, oauthId)
                .orElseGet(() -> {
                    Users newUser = new Users();
                    newUser.setEmail(email);
                    newUser.setUsername(email);
                    newUser.setProvider(provider);
                    newUser.setProviderId(oauthId);
                    newUser.getRoles().add(findUserRole());
                    return userRepository.save(newUser);
                });
    }

    private Roles findUserRole() {
        return rolesRepository.findByRoleName(RolesEnum.ROLE_USER)
                .orElseThrow(() -> new IllegalStateException("Default role ROLE_USER not found in DB"));
    }
}