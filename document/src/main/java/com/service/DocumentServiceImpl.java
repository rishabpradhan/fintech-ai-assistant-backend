package com.service;

import com.api_format.APIResponse;
import com.entity.Document;
import com.enums.DocumentStatus;
import com.enums.HttpStatusCode;
import com.enums.Status;
import com.exceptions.BusinessExceptions;
import com.repository.DocumentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.Collections;

@Service
@Slf4j
@RequiredArgsConstructor

public class DocumentServiceImpl implements DocumentService {

    private final FastApiClient fastApiClient;
    private final DocumentRepository documentRepository;
    private final FileStorageService fileStorageService;

    @Override
    @Transactional
    public ResponseEntity<APIResponse<Object>> uploadDocuments(MultipartFile file , Principal principal){

        try {

            String currentUser = principal.getName();
            String path = fileStorageService.store(file);
            Document document = new Document();
            document.setFileName(file.getOriginalFilename());
            document.setName(file.getOriginalFilename());
            document.setFilePath(path);
            document.setStatus(DocumentStatus.UPLOADED);
            document.setContentType(file.getContentType());
            document.setFileSize(file.getSize());
            document.setCreatedBy(currentUser);

            document = documentRepository.save(document);

            documentProcessInBackground(document);
            return new ResponseEntity<>(APIResponse.apiResponse(HttpStatusCode.SUCCESS.getCode(), Collections.emptyList(), Status.SUCCESS.getName(), "Document uploaded successFully"), HttpStatus.OK);

        } catch (IOException e) {
            log.error("Error in saving document:{}",e.getMessage());
            throw new BusinessExceptions.GeneralException("Internal Server Error");
        }


    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<APIResponse<Object>> getAllFiles(){
        return  null;
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<APIResponse<Object>> getFilesById(){

        return  null;
    }

    private void documentProcessInBackground(Document document){
        document.setStatus(DocumentStatus.UPLOADED);
        documentRepository.save(document);

        try{
            fastApiClient.processDocument(document.getId(), document.getFilePath());
            document.setStatus(DocumentStatus.PROCESSED);
        } catch (Exception e) {
            log.error("Error in fast api :{}", e.getMessage());
            document.setStatus(DocumentStatus.FAILED);
        }

        documentRepository.save(document);
    }

}
