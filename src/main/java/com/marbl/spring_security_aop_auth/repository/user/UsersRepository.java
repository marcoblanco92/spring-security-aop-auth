package com.marbl.spring_security_aop_auth.repository.user;

import com.marbl.spring_security_aop_auth.entity.user.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsersRepository extends JpaRepository<Users, Long> {

    boolean existsByUsernameOrEmail(String username, String email);
}
