package com.marbl.spring_security_aop_auth.repository.user;

import com.marbl.spring_security_aop_auth.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsersRepository extends JpaRepository<User, Long> {

    boolean existsByUsernameOrEmail(String username, String email);

    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String userName);
    Optional<User> findByUsernameOrEmail(String userName, String email);

}
