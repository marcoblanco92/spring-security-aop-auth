package com.marbl.spring_security_aop_auth.component.oauth;

import com.marbl.spring_security_aop_auth.entity.provider.AuthProvider;
import com.marbl.spring_security_aop_auth.entity.provider.UserAuthProvider;
import com.marbl.spring_security_aop_auth.entity.role.Roles;
import com.marbl.spring_security_aop_auth.entity.role.RolesEnum;
import com.marbl.spring_security_aop_auth.entity.user.User;
import com.marbl.spring_security_aop_auth.repository.provider.UserAuthProviderRepository;
import com.marbl.spring_security_aop_auth.repository.role.RolesRepository;
import com.marbl.spring_security_aop_auth.repository.user.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class Oauth2Service {

    private final UsersRepository userRepository;
    private final RolesRepository rolesRepository;
    private final UserAuthProviderRepository authProviderRepository;

    @Transactional
    public User processOauthPostLogin(OAuth2User oauth2User, AuthProvider provider) {
        String oauthId = oauth2User.getName();
        String email = oauth2User.getAttribute("email");

        return authProviderRepository.findByProviderAndProviderId(provider, oauthId)
                .map(UserAuthProvider::getUser)
                .orElseGet(() -> handleNewOrExistingUser(email, provider, oauthId));
    }

    private User handleNewOrExistingUser(String email, AuthProvider provider, String oauthId) {
        User user = userRepository.findByEmail(email).orElseGet(() -> {
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setEnabled(true);
            newUser.getRoles().add(findUserRole());
            return userRepository.save(newUser);
        });

        if (!authProviderRepository.existsByUserAndProvider(user, provider)) {
            UserAuthProvider authProvider = new UserAuthProvider();
            authProvider.setUser(user);
            authProvider.setProvider(provider);
            authProvider.setProviderId(oauthId);
            authProviderRepository.save(authProvider);
        }

        return user;
    }

    private Roles findUserRole() {
        return rolesRepository.findByRoleName(RolesEnum.ROLE_USER)
                .orElseThrow(() -> new IllegalStateException("Default role ROLE_USER not found in DB"));
    }
}