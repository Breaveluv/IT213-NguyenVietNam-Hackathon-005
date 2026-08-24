package org.example.hackathon_de05.config;

import org.example.hackathon_de05.service.TicketOrderService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatConfig {

    @Bean
    public ChatMemory chatMemory() {
        return MessageWindowChatMemory.builder()
                .chatMemoryRepository(new InMemoryChatMemoryRepository())
                .maxMessages(10)
                .build();
    }

    @Bean
    public ChatClient chatClient(ChatClient.Builder chatClientBuilder, TicketOrderService ticketOrderService) {
        return chatClientBuilder
                .defaultSystem("Bạn là trợ lý AI tư vấn và hỗ trợ đặt vé xe khách của nhà xe VietXeTravel. Hãy hỗ trợ khách hàng tra cứu thông tin chuyến xe, tuyến xe, giải đáp thắc mắc dịch vụ và giúp khách hàng thực hiện đặt vé khi có nhu cầu.")
                .defaultAdvisors(
                        MessageChatMemoryAdvisor.builder(chatMemory()).build()
                )
                .defaultTools(ticketOrderService)
                .build();
    }
}
