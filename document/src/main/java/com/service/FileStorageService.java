package com.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.UUID;

@Service
@Slf4j
public class FileStorageService {

    @Value("${app.storage.upload-dir}")
    private String uploadDir;

    public String store(MultipartFile file) throws IOException {
        try{
            Path dirPath = Paths.get(uploadDir).toAbsolutePath().normalize();
            Files.createDirectories(dirPath);

            String safeFileName = UUID.randomUUID() + "_" + StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
            Path targetDir = dirPath.resolve(safeFileName);

            Files.copy(file.getInputStream(), targetDir, StandardCopyOption.REPLACE_EXISTING);

            return targetDir.toString();

        } catch (Exception e) {
            log.error("Error in ");
            throw  new IOException("Failed to store file",e);
        }
    }

}
