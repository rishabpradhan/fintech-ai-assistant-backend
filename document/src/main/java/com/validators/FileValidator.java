package com.validators;

import com.exceptions.BusinessExceptions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;


@Component
@RequiredArgsConstructor
@Slf4j
public class FileValidator {

    private static final Tika tika = new Tika();

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;

    private static final List<String> allowedFileTypes = List.of("pdf", "doc", "docx");

    private static final List<String> allowedMIMETypes = List.of(
            "application/pdf",
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document"

    );

    public void fileValidate(MultipartFile file) throws BusinessExceptions.FileValidationException {

        if(Objects.isNull(file) || file.isEmpty()){
            throw new BusinessExceptions.FileValidationException("File is empty or missing");
        }

        if(file.getSize() > MAX_FILE_SIZE){
            throw new BusinessExceptions.FileValidationException("File size is greater than 5 MB");
        }


    }


}
