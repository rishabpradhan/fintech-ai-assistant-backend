package com.service;

import com.api_format.APIResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;

public interface DocumentService {

    ResponseEntity<APIResponse<Object>> uploadDocuments(MultipartFile file, Principal principal);

    ResponseEntity<APIResponse<Object>> getAllFiles();

    ResponseEntity<APIResponse<Object>> getFilesById();
}
