package com.enums;


import lombok.Getter;

@Getter
public enum Status {
    SUCCESS(000, "SUCCESS"),
    ERROR(001, "ERROR");

    private final int code;
    private final String name;

    Status(int code, String name) {
        this.code = code;
        this.name = name;
    }
}