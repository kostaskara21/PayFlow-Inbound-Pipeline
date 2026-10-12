package com.kara.payflow.configuration;

import org.apache.sshd.sftp.client.SftpClient;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.integration.file.remote.session.CachingSessionFactory;
import org.springframework.integration.file.remote.session.Session;
import org.springframework.integration.file.remote.session.SessionFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

// Needs the SFTP server running: docker compose up -d
class SftpConnectionTest {

    private static final String UPLOAD_DIR = "/upload";

    private static SessionFactory<SftpClient.DirEntry> sessionFactory;

    @BeforeAll
    static void createSessionFactory() {
        sessionFactory = new Sftpconfig().sessionFactory();
    }

    @AfterAll
    static void closeSessionFactory() {
        ((CachingSessionFactory<SftpClient.DirEntry>) sessionFactory).destroy();
    }

    @Test
    void connectsAndLogsIn() {
        try (Session<SftpClient.DirEntry> session = sessionFactory.getSession()) {
            assertThat(session.isOpen()).isTrue();
        }
    }

    @Test
    void uploadDirectoryExists() throws IOException {
        try (Session<SftpClient.DirEntry> session = sessionFactory.getSession()) {
            assertThat(session.exists(UPLOAD_DIR)).isTrue();
        }
    }

    @Test
    void canWriteAndReadBackAFile() throws IOException {
        // Not a .csv, so the running app ignores it
        String remotePath = UPLOAD_DIR + "/connection-test-" + UUID.randomUUID() + ".tmp";
        String content = "sftp connection test";

        try (Session<SftpClient.DirEntry> session = sessionFactory.getSession()) {
            try {
                session.write(new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8)), remotePath);
                assertThat(session.exists(remotePath)).isTrue();

                ByteArrayOutputStream downloaded = new ByteArrayOutputStream();
                session.read(remotePath, downloaded);
                assertThat(downloaded.toString(StandardCharsets.UTF_8)).isEqualTo(content);
            } finally {
                if (session.exists(remotePath)) {
                    session.remove(remotePath);
                }
            }
        }
    }
}
