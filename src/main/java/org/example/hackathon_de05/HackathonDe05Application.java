package org.example.hackathon_de05;

import org.example.hackathon_de05.service.DatabaseInitializeService;
import org.example.hackathon_de05.service.IngestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootApplication
public class HackathonDe05Application implements CommandLineRunner {

    @Autowired
    private DatabaseInitializeService databaseInitializeService;

    @Autowired
    private IngestService ingestService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public static void main(String[] args) {
        SpringApplication.run(HackathonDe05Application.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        databaseInitializeService.initializeDatabase();

        try {
            Integer count = jdbcTemplate.queryForObject("SELECT count(*) FROM vector_store", Integer.class);
            if (count != null && count == 0) {
                System.out.println("Vector store is empty, starting to ingest PDF...");
                ingestService.ingestPdf();
                System.out.println("PDF ingested successfully.");
            } else {
                System.out.println("Vector store already contains data (" + count + " rows). Skip ingestion.");
            }
        } catch (Exception e) {
            System.out.println("Table vector_store might not exist yet or another error occurred. Ingesting PDF...");
            ingestService.ingestPdf();
        }
    }
}
