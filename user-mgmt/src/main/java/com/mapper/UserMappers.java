package com.mapper;

import com.dtos.requestDtos.UserLoginRequestDtos;
import com.entity.UserDetail;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Getter
@Setter
public class UserMappers {

    public UserDetail toUserDetail(UserLoginRequestDtos userRequestDto ){
        if(userRequestDto == null){
            return null;
        }
        UserDetail user = new UserDetail();
        user.setEmail(userRequestDto.getEmail());
        user.setCreatedAt(LocalDateTime.now());
        user.setMfaEnabled(false);
        user.setLoginAttempts(0);
        user.setOtpSecrect(null);
        user.setLockedUntil(null);

        return user;
    }

//    public UserAudit toAuditDetail(){
//
//    }
}
