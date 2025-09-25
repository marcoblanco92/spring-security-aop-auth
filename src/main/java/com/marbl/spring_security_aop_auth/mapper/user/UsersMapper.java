package com.marbl.spring_security_aop_auth.mapper.user;

import com.marbl.spring_security_aop_auth.dto.auth.LoginRequestDto;
import com.marbl.spring_security_aop_auth.dto.user.RegisterDto;
import com.marbl.spring_security_aop_auth.entity.user.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UsersMapper {

    UsersMapper INSTANCE = Mappers.getMapper(UsersMapper.class);

    @Mapping(target = "password", ignore = true)
    User toEntity(RegisterDto dto);

    @Mapping(target = "password", ignore = true)
    User toEntity(LoginRequestDto dto);

}