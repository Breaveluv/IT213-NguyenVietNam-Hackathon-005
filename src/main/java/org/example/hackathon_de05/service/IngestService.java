package org.example.hackathon_de05.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class IngestService {

    private final VectorStore vectorStore;
    private final TokenTextSplitter tokenTextSplitter;

    public void ingestPdf() {
        try {
            log.info("Starting ingestion of PDF file: De05_VietXeTravel_ThongTin.pdf");
            Resource pdfResource = new ClassPathResource("De05_VietXeTravel_ThongTin.pdf");

            TikaDocumentReader reader = new TikaDocumentReader(pdfResource);
            List<Document> documents = reader.read();

            List<Document> chunks = tokenTextSplitter.apply(documents);

            vectorStore.accept(chunks);
            log.info("Successfully ingested {} document chunks into vector_store", chunks.size());
        } catch (Exception e) {
            log.error("Error occurred while ingesting PDF into vector_store: {}", e.getMessage(), e);
        }
    }
}
