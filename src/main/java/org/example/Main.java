package org.example;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;


public class Main {
    private static TrayIcon trayIcon;
    private static final QuestionsTasks questionsTasks = new QuestionsTasks();

    public static void main(String[] args) {
        initialize();

        try {
            GlobalScreen.registerNativeHook();

            SystemTray tray = SystemTray.getSystemTray();
            BufferedImage trayImage = ImageIO.read(new File("icon.png"));
            PopupMenu popup = new PopupMenu();
            MenuItem answersItem = new MenuItem("Answers");
            MenuItem exitItem = new MenuItem("Exit");
            popup.add(answersItem);
            popup.add(exitItem);
            trayIcon = new TrayIcon(trayImage, "Test Helper", popup);
            trayIcon.setImageAutoSize(true);
            tray.add(trayIcon);

            // Обработчик для кнопки Capture
            answersItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    fillAnswers();
                }
            });

            // Обработчик для кнопки Exit
            exitItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    SystemTray tray = SystemTray.getSystemTray();
                    tray.remove(trayIcon);
                    try {
                        GlobalScreen.unregisterNativeHook();
                    } catch (NativeHookException ex) {
                        throw new RuntimeException(ex);
                    }
                    System.exit(0);
                }
            });

            GlobalScreen.addNativeKeyListener(new NativeKeyListener() {
                @Override
                public void nativeKeyPressed(NativeKeyEvent e) {
                    if (e.getKeyCode() == NativeKeyEvent.VC_F2) {
                        fillAnswers();
                    }
                }

                @Override
                public void nativeKeyReleased(NativeKeyEvent e) {
                }

                @Override
                public void nativeKeyTyped(NativeKeyEvent e) {
                }
            });
        } catch (NativeHookException | IOException | AWTException ex) {
            ex.printStackTrace();
        }
    }

    private static void initialize() {
        try {
            String command = "start chrome.exe --remote-debugging-port=9222 --user-data-dir=D:\\chromeData"; // Замените "команда" на нужную вам команду
            ProcessBuilder processBuilder = new ProcessBuilder();
            processBuilder.command("cmd.exe", "/c", command);
            processBuilder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void fillAnswers() {
        questionsTasks.getQuestions();
    }
}
