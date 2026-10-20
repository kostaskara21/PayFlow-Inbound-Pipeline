package com.kara.payflow.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.sshd.sftp.client.SftpClient;
import org.springframework.http.ResponseEntity;
import org.springframework.integration.file.remote.session.Session;
import org.springframework.integration.file.remote.session.SessionFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class UploadServiceImpl implements UploadService{

    private final SessionFactory<SftpClient.DirEntry> sessionFactory;

    @Override
    public ResponseEntity<?> upload(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        String filename = file.getOriginalFilename();

        if (filename == null ||
                !filename.toLowerCase().endsWith(".csv") ||
                filename.contains("/") ||
                filename.contains("\\")) {
            throw new IllegalArgumentException("Invalid CSV filename");
        }

        try (Session<SftpClient.DirEntry> session =
                     sessionFactory.getSession();
             InputStream inputStream = file.getInputStream()) {

            if(session.exists("/upload/" + filename)){
                log.info("File with this name already exists");
                return ResponseEntity.status(409).body(
                        Map.of("message", "File already exists")
                );
            }
            session.write(inputStream, "/upload/" + filename);
            return ResponseEntity.ok(
                    Map.of("message", "CSV uploaded successfully")
            );

        }
    }
}

