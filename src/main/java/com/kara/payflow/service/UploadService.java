package com.kara.payflow.service;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface UploadService {
    ResponseEntity<?> upload(MultipartFile file) throws IOException;
}
