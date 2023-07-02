package org.example;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import java.io.IOException;


public class Main {
    private static TrayIcon trayIcon;
    private static final QuestionsTasks questionsTasks = new QuestionsTasks();

    private static final OneQuestionTask oneQuestionTask = new OneQuestionTask();

    public static void main(String[] args) {
        initialize();

        try {
            GlobalScreen.registerNativeHook();
            SystemTray tray = SystemTray.getSystemTray();
            PopupMenu popup = new PopupMenu();
            MenuItem exitItem = new MenuItem("Закрыть");
            popup.add(exitItem);
            ImageIcon icon = new ImageIcon(new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB));
            trayIcon = new TrayIcon(icon.getImage(), "Test Helper", popup);
            tray.add(trayIcon);

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
                    if (e.getKeyCode() == NativeKeyEvent.VC_E) {
                        fillAnswers();
                    } else if (e.getKeyCode() == NativeKeyEvent.VC_R) {
                        fillAnswer();
                    }
                }

                @Override
                public void nativeKeyReleased(NativeKeyEvent e) {
                }

                @Override
                public void nativeKeyTyped(NativeKeyEvent e) {
                }
            });
        } catch (NativeHookException | AWTException ex) {
            ex.printStackTrace();
        }
    }

    private static void initialize() {
        try {
            String command = "start chrome.exe --remote-debugging-port=9222 --user-data-dir=C:\\Windows";
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

    private static void fillAnswer(){
        oneQuestionTask.getQuestion();
    }
}
