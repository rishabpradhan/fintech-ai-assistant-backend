package com.validators;


import com.exceptions.BusinessExceptions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;
import java.util.Objects;


@Component
@RequiredArgsConstructor
@Slf4j
public class FileValidator {

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;

    private static final List<String> allowedFileTypes = List.of("pdf", "doc", "docx");

    private static final List<String> allowedMIMETypes = List.of(
            "application/pdf",
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document"

    );

    private static final byte[] PDF_SIGNATURE   = {0x25, 0x50, 0x44, 0x46};             // %PDF
    private static final byte[] DOC_SIGNATURE   = {(byte) 0xD0, (byte) 0xCF, 0x11, (byte) 0xE0}; // OLE
    private static final byte[] DOCX_SIGNATURE  = {0x50, 0x4B, 0x03, 0x04};

    public void fileValidate(MultipartFile file) throws BusinessExceptions.FileValidationException {

        if(Objects.isNull(file) || file.isEmpty()){
            throw new BusinessExceptions.FileValidationException("File is empty or missing");
        }

        if(file.getSize() > MAX_FILE_SIZE){
            throw new BusinessExceptions.FileValidationException("File size is greater than 5 MB");
        }

        // extension checking
        String fileName = file.getOriginalFilename();
        if(fileName == null || file.isEmpty()){
            throw new BusinessExceptions.FileValidationException("File name is missing");
        }

        // check for extenstions

        String fileExtensions = getExtension(fileName).toLowerCase();
        if(!allowedFileTypes.contains(fileExtensions)){
            throw new BusinessExceptions.FileValidationException("Only pdf and words files are allowed");
        }

        try(InputStream input = file.getInputStream()) {
            byte[] header = new byte[8];
            int bytesRead = input.read(header);

            if(bytesRead > 4 || matchesAnySignature(header,fileExtensions)){
                log.info("File content verified");
            }

        } catch (Exception e) {
            log.error("Error in file content validation :{}", e.getMessage());
            throw new BusinessExceptions.FileValidationException("File content validation failed");
        }

    }

    private static boolean matchesAnySignature(byte[] header, String extension) {
        return switch (extension) {
            case "pdf" -> startsWith(header, PDF_SIGNATURE);
            case "doc" -> startsWith(header, DOC_SIGNATURE);
            case "docx" -> startsWith(header, DOCX_SIGNATURE);
            default -> false;
        };
    }

    private static boolean startsWith(byte[] fileHeader, byte[] signature) {
        if (fileHeader.length < signature.length) return false;
        for (int i = 0; i < signature.length; i++) {
            if (fileHeader[i] != signature[i]) {
                return false;
            }
        }
        return true;
    }

    private static String getExtension(String fileName){
        int dotIndex = fileName.lastIndexOf(".");
        return (dotIndex == -1) ? "" : fileName.substring(dotIndex+1);
    }
}
