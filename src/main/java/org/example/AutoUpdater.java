package org.example;

import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import java.io.IOException;


public class AutoUpdater extends JFrame {
    private static final String applicationVersion = "v1.0";
    private static final GitHubAPI gitHubAPI = new GitHubAPI();
    private static final JSONObject myJson = gitHubAPI.versionGetting();

    public static String browserDownloadUrl = "";

    public static void checkVersion(){
        if(applicationVersion.equals(myJson.getString("tag_name"))){
            try {
                String command = "start chrome.exe --remote-debugging-port=9222 --user-data-dir=C:\\Windows";
                ProcessBuilder processBuilder = new ProcessBuilder();
                processBuilder.command("cmd.exe", "/c", command);
                processBuilder.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            JSONArray assets = myJson.getJSONArray("assets");
            JSONObject firstAsset = assets.getJSONObject(0);
            browserDownloadUrl = firstAsset.getString("browser_download_url");
            UpdateDialog updateDialog = new UpdateDialog();
            updateDialog.setVisible(true);
        }
    }

    protected static String getBrowserDownloadUrl(){
        return browserDownloadUrl;
    }
}
