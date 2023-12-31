package org.example;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatGptApiClient {
    private static final String API_URL = "https://api.openai.com/v1/engines/text-davinci-003/completions";
    private static final String API_KEY = "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";

    public String getAnswers(String message) {
        OkHttpClient client = new OkHttpClient();

        try {
            String response = sendChatRequest(client, message);
            String content = getResponseContent(response);
            content = content.replace("\n", "");
            content = content.replace("\n\n", "");
            content = content.replace(" ", "");
            return content;
        } catch (IOException e) {
            System.out.println("An error occurred: " + e.getMessage());

        }
        System.out.println("======================================================Error======================================================");
        return "Error";
    }

    private static String sendChatRequest(OkHttpClient client, String message) throws IOException {
        MediaType mediaType = MediaType.parse("application/json");

        String json = "{\"prompt\": \"" + message + "\", \"max_tokens\": 50, \"temperature\": 0.2}";

        RequestBody body = RequestBody.create(json, mediaType);


        Request request = new Request.Builder()
                .url(API_URL)
                .post(body)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                return response.body().string();
            } else {
                throw new IOException("Unexpected response: " + response);
            }
        }
    }

    private static String getResponseContent(String responseJson) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(responseJson);
        String content = root.path("choices").get(0).path("text").asText();
        return content;
    }
}
