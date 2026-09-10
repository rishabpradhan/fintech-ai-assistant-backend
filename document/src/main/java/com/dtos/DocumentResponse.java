package com.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DocumentResponse {

    private Long documentId;
    private String documentName;
    private String contentType;
    private Long fileSize;
    private String documentStatus;
}
