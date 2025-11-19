//package com.example.moviechat.service;
//
//import org.springframework.ai.chat.client.ChatClient;
//import org.springframework.ai.chat.prompt.Prompt;
//import org.springframework.ai.chat.prompt.PromptTemplate;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//import java.util.Map;
//
//@Service
//public class ChatService {
//
//    private final ChatClient chatClient;
//    private final JdbcTemplate jdbcTemplate;
//
//    public ChatService(ChatClient.Builder builder, JdbcTemplate jdbcTemplate) {
//        this.chatClient = builder.build();
//        this.jdbcTemplate = jdbcTemplate;
//    }
//
//    public Map<String, Object> getAnswer(String userQuery) {
//        // 1. We provide the Database Schema to the AI so it knows how to write the SQL
//        String schemaPrompt = """
//            You are an AI assistant that converts English questions into SQL queries for a PostgreSQL database.
//
//            The Database Schema is:
//            Table: movies (id, title, release_year, genre, director)
//            Table: artists (id, name, birth_date)
//            Table: movie_artists (id, movie_id, artist_id, role)
//
//            Rules:
//            1. Return ONLY the SQL query. Do not include markdown, backticks, or explanations.
//            2. Use ONLY SELECT statements.
//
//            Question: {question}
//            """;
//
//        PromptTemplate template = new PromptTemplate(schemaPrompt);
//        template.add("question", userQuery);
//        Prompt prompt = template.create();
//
//        // 2. Get SQL from AI
//        String sqlQuery = chatClient.prompt(prompt).call().content();
//
//        // Cleanup cleanup in case AI adds backticks
//        sqlQuery = sqlQuery.replace("```sql", "").replace("```", "").trim();
//
//        System.out.println("Generated SQL: " + sqlQuery);
//
//        // 3. Execute SQL and return result
//        try {
//            List<Map<String, Object>> result = jdbcTemplate.queryForList(sqlQuery);
//            return Map.of("query", userQuery, "sql", sqlQuery, "answer", result);
//        } catch (Exception e) {
//            return Map.of("error", "Failed to execute query: " + e.getMessage());
//        }
//    }
//}


package com.example.moviechat.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ChatService {

    private final ChatClient chatClient;
    private final JdbcTemplate jdbcTemplate;

    @Value("${spring.ai.openai.api-key}")
    private String apiKey;

    public ChatService(ChatClient.Builder builder, JdbcTemplate jdbcTemplate) {
        this.chatClient = builder.build();
        this.jdbcTemplate = jdbcTemplate;
    }

    public Map<String, Object> getAnswer(String userQuery) {
        // --- MOCK MODE (If no API Key) ---
        if (apiKey == null || apiKey.contains("INSERT_YOUR_OPENAI_API_KEY")) {
            System.out.println("⚠️ Mock Mode: Returning fake data for testing.");
            String mockSql = "SELECT * FROM movies WHERE title ILIKE '%Matrix%'";
            // Ensure we actually have data to show even in mock mode
            try {
                List<Map<String, Object>> result = jdbcTemplate.queryForList(mockSql);
                return Map.of(
                        "query", userQuery,
                        "sql", mockSql,
                        "answer", result.isEmpty() ? "No movies found (Did you run data.sql?)" : result
                );
            } catch (Exception e) {
                return Map.of("error", "Database error: " + e.getMessage());
            }
        }

        // --- REAL AI MODE (Future Use) ---
        // ... (Your normal AI code would go here)
        return Map.of("error", "Real AI not implemented in this test snippet");
    }
}