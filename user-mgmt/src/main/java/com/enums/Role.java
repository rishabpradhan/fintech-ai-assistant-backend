package com.enums;

import lombok.Getter;

import java.lang.reflect.Array;
import java.util.Arrays;

@Getter
public enum Role {

    USER("USER","user"),
    ADMIN("ADMIN", "admin");

    private final String code;
    private final String name;

    Role(String code, String name){
        this.code = code;
        this.name = name;
    }


}
