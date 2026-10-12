package com.kara.payflow.configuration;

import com.example.payflow.contracts.Transaction;
import com.kara.payflow.model.TransactionCsvRow;
import com.kara.payflow.util.CsvProcessor;
import org.apache.sshd.sftp.client.SftpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.Pollers;
import org.springframework.integration.file.filters.ChainFileListFilter;
import org.springframework.integration.file.filters.FileSystemPersistentAcceptOnceFileListFilter;
import org.springframework.integration.file.remote.session.CachingSessionFactory;
import org.springframework.integration.file.remote.session.SessionFactory;
import org.springframework.integration.metadata.PropertiesPersistingMetadataStore;
import org.springframework.integration.sftp.dsl.Sftp;
import org.springframework.integration.sftp.filters.SftpPersistentAcceptOnceFileListFilter;
import org.springframework.integration.sftp.filters.SftpRegexPatternFileListFilter;
import org.springframework.integration.sftp.session.DefaultSftpSessionFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

@Configuration
public class Sftpconfig {
    @Bean
    public SessionFactory<SftpClient.DirEntry> sessionFactory() {

        DefaultSftpSessionFactory factory = new DefaultSftpSessionFactory(true);

        factory.setHost("localhost");
        factory.setPort(2222);
        factory.setUser("user");
        factory.setPassword("password");
        factory.setAllowUnknownKeys(true);

        return new CachingSessionFactory<>(factory);
    }

    // Saves the list of already handled files to ./sftp-metadata so it survives restarts
    @Bean
    public PropertiesPersistingMetadataStore metadataStore() {
        PropertiesPersistingMetadataStore store = new PropertiesPersistingMetadataStore();
        store.setBaseDirectory("./sftp-metadata");
        return store;
    }

    @Bean
    public IntegrationFlow inboundConfig() {
        // 1. Create a filter that remembers what files it has seen on the SFTP server
        SftpPersistentAcceptOnceFileListFilter remoteSeen = new SftpPersistentAcceptOnceFileListFilter(metadataStore(), "remote-");
        remoteSeen.setFlushOnUpdate(true);
        ChainFileListFilter<SftpClient.DirEntry> remoteFilters = new ChainFileListFilter<>();
        remoteFilters.addFilter(new SftpRegexPatternFileListFilter(Pattern.compile(".*\\.csv")));
        remoteFilters.addFilter(remoteSeen);

        // 2. Create a filter that remembers which local copies were already processed
        FileSystemPersistentAcceptOnceFileListFilter localSeen = new FileSystemPersistentAcceptOnceFileListFilter(metadataStore(), "local-");
        localSeen.setFlushOnUpdate(true);

        return IntegrationFlow
                .from(
                        Sftp.inboundAdapter(sessionFactory())
                                .preserveTimestamp(true)
                                .remoteDirectory("/upload")
                                .filter(remoteFilters)
                                .localDirectory(new File("./downloads"))
                                .localFilter(localSeen) // Keeps track of local copies
                                .deleteRemoteFiles(false), // Keeps files on SFTP
                        e -> e.poller(
                                Pollers.fixedDelay(10000).maxMessagesPerPoll(-1) // Hand over every waiting file, not one per poll
                        )
                )
                .handle(message -> {
                    File file = (File) message.getPayload();
                    System.out.println("Processing New File: " + file.getName());
                    try {
                        List<TransactionCsvRow> transactions = CsvProcessor.read(file.getPath(), TransactionCsvRow.class);
                        transactions.forEach(System.out::println);
                    } catch (IOException e) {
                        System.out.println("Could not read " + file.getName() + ": " + e.getMessage());
                    }
                })
                .get();
    }

}
