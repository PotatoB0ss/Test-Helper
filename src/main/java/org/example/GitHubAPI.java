package org.example;

import okhttp3.*;
import org.json.JSONObject;

public class GitHubAPI {
    public JSONObject versionGetting() {
        String owner = "PotatoB0ss";
        String repo = "ForReleases";
        String token = "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";

        OkHttpClient client = new OkHttpClient();

        String url = "https://api.github.com/repos/" + owner + "/" + repo + "/releases/latest";
        Request request = new Request.Builder()
                .url(url)
                .header("Authorization", "Bearer " + token)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                String jsonResponse = response.body().string();
                return new JSONObject(jsonResponse);
            } else {
                System.out.println("Request failed with code: " + response.code());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        JSONObject JSONObject;
        return JSONObject = new JSONObject("");
    }
}


