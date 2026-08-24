package org.example.hackathon_de05.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatClient chatClient;
    private final VectorStore vectorStore;

//    @PostMapping("/ask")
//    private String askQuestion(String question) {
//        QuestionAnswerAdvisor advisor = new QuestionAnswerAdvisor(vectorStore);
//        return chatClient.chat(question, advisor).getResponse();
//
//
//    }
//    @GetMapping("/searchstripbyName")
//    private String searchBusTripByName(String name) {
//        QuestionAnswerAdvisor advisor = new QuestionAnswerAdvisor(vectorStore);
//        return chatClient.chat(name, advisor).getResponse();
//    }
//    @GetMapping("/searchtripbyRoute")
//    private String searchBusTripByRoute(String route) {
//        QuestionAnswerAdvisor advisor = new QuestionAnswerAdvisor(vectorStore);
//        return chatClient.chat(route, advisor).getResponse();
//    }
//    @GetMapping("/getTravelInfo")
//    private String getTravelInfo(String question) {
//        QuestionAnswerAdvisor advisor = new QuestionAnswerAdvisor(vectorStore);
//        return chatClient.chat(question, advisor).getResponse();
//    }
}
