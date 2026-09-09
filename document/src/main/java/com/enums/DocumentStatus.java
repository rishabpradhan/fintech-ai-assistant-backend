package com.enums;

import lombok.Getter;
import lombok.Setter;

@Getter

public enum DocumentStatus {

    UPLOADED("UPLOADED","uploaded"),
    PROCESSING("PROCESSING", "processing"),
    PROCESSED("PROCESSED", "processed"),
    FAILED("FAILED", "failed");


    private final String code;
    private final String name;

    DocumentStatus(String code, String name){
        this.code = code;
        this.name = name;
    }
}
