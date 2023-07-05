package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class UpdateDialog extends JFrame {

    public UpdateDialog() {
        // Настройка основного окна
        setTitle("Обновление приложения");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 200);
        setLocationRelativeTo(null); // Центрирование окна

        // Создание панели с макетом BorderLayout
        JPanel panel = new JPanel(new BorderLayout());

        // Создание надписи с HTML-разметкой
        JLabel label = new JLabel("<html>Доступна новая версия приложения.<br><br><center>Хотите обновить?</center></html>");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(label, BorderLayout.CENTER);

        // Создание панели с кнопками
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        // Создание кнопки "Да"
        JButton yesButton = new JButton("Да");
        yesButton.setPreferredSize(new Dimension(100, 40));
        yesButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Действия при нажатии на кнопку "Да"
                String batFilePath = "updater.bat";
                try {
                    // Запуск bat-файла в отдельном процессе
                    Runtime.getRuntime().exec("cmd.exe /c start " + batFilePath + " " + AutoUpdater.getBrowserDownloadUrl());
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

                dispose(); // Закрытие окна
                System.exit(0);
            }

        });
        buttonPanel.add(yesButton);

        // Создание кнопки "Нет"
        JButton noButton = new JButton("Нет");
        noButton.setPreferredSize(new Dimension(100, 40));
        noButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String command = "start chrome.exe --remote-debugging-port=9222 --user-data-dir=C:\\Windows";
                    ProcessBuilder processBuilder = new ProcessBuilder();
                    processBuilder.command("cmd.exe", "/c", command);
                    processBuilder.start();
                } catch (IOException x) {
                    x.printStackTrace();
                }
                System.out.println("Обновление отклонено");
                dispose(); // Закрытие окна
            }
        });
        buttonPanel.add(noButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        add(panel);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
                    e.printStackTrace();
                }

                UpdateDialog dialog = new UpdateDialog();
                dialog.setVisible(true);
            }
        });
    }
}
