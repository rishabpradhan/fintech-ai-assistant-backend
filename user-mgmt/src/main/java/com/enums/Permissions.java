package com.enums;

import lombok.Getter;

@Getter
public enum Permissions {

    DOCUMENT_READ("DOCR","DOCUMENT_READ"),
    DOCUMENT_APPROVE("DOCA", "DOCUMENT_APPROVE"),
    DOCUMENT_DELETE("DOE","DOCUMENT_DELETE"),
    DOCUMENT_WRITE("DOW","DOCUMENT_WRITE"),
    USER_READ("USR","USER_READ"),
    USER_WRITE("USW","USER_WRITE");


    private final String code;
    private final String name;

    Permissions(String code, String name){
        this.code = code;
        this.name = name;
    }
}
