package com.marbl.spring_security_aop_auth.repository.provider;

import aj.org.objectweb.asm.commons.Remapper;
import com.marbl.spring_security_aop_auth.entity.provider.AuthProvider;
import com.marbl.spring_security_aop_auth.entity.provider.UserAuthProvider;
import com.marbl.spring_security_aop_auth.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserAuthProviderRepository extends JpaRepository<UserAuthProvider, Long> {

    Optional<UserAuthProvider> findByProviderAndProviderId(AuthProvider provider, String oauthId);

    boolean existsByUserAndProvider(User user, AuthProvider provider);
}
