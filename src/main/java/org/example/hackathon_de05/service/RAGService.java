package org.example.hackathon_de05.service;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RAGService {

    private final VectorStore vectorStore;
    private final IngestService ingestService;

    public List<Document> search(String query) {
        return vectorStore.similaritySearch(
                SearchRequest.builder().query(query).topK(3).build()
        );
    }

    public void ingestPdf() {
        ingestService.ingestPdf();
    }
}
