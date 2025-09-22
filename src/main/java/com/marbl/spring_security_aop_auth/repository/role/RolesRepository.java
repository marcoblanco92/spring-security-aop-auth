package com.marbl.spring_security_aop_auth.repository.role;

import com.marbl.spring_security_aop_auth.entity.role.Roles;
import com.marbl.spring_security_aop_auth.entity.role.RolesEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RolesRepository extends JpaRepository<Roles, Long> {

    Optional<Roles> findByRoleName(RolesEnum name);

}
