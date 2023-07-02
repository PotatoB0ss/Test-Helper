package org.example;


import org.openqa.selenium.By;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.Collections;
import java.util.List;

public class QuestionsTasks {

    private static final ChatGptApiClient chatGptApiClient = new ChatGptApiClient();

    public void getQuestions() {
        System.setProperty("webdriver.chrome.silentOutput", "true");
        ChromeOptions opt = new ChromeOptions();
        opt.setExperimentalOption("debuggerAddress","localhost:9222");
        opt.addArguments("--remote-allow-origins=*");
        opt.addArguments("--disable-extensions"); // Отключает расширения браузера
        opt.addArguments("--disable-dev-shm-usage"); // Отключает /dev/shm использование
        opt.addArguments("--disable-gpu"); // Отключает использование GPU
        opt.addArguments("--no-sandbox"); // Отключает песочницу
        ChromeDriver driver = new ChromeDriver(opt);

        String script = "var inputs = document.querySelectorAll('input[id^=\"q205186\"]');" +
                "for (var i = 0; i < inputs.length; i++) {" +
                "  inputs[i].removeAttribute('checked');" +
                "}";

        // Очистка ответов чтобы не забагалось
        ((JavascriptExecutor) driver).executeScript(script);

        Duration timeoutDuration = Duration.ofSeconds(10);
        WebDriverWait wait = new WebDriverWait(driver, timeoutDuration);

        List<WebElement> questionElements = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("que")));


        // Парсинг вопросов и ответов
        for (WebElement questionElement : questionElements) {

            int i = 0;

            // Получение текста вопроса
            WebElement questionTextElement = questionElement.findElement(By.className("qtext"));
            String questionText = questionTextElement.getText();

            // Получение уникального идентификатора вопроса
            WebElement questionIdElement = questionElement.findElement(By.cssSelector("input[name^='q'][name$=':sequencecheck']"));
            String questionId = questionIdElement.getAttribute("name");

            // Получение всех ответов на вопрос
            List<WebElement> answerElements = questionElement.findElements(By.tagName("label"));
            String answers = "";
            for (WebElement answerElement : answerElements) {
                String answerText = answerElement.getText();

                answers += "Ответ " + i + ": " + answerText + ". ";
                i++;
            }

            String question = "В ответ дай мне только цифру наиболее подходящего ответа. Мне нужен точный ответ, так что можешь думать над ответом столько времени сколько нужно. Вопрос: " + questionText + ". " + answers;
            question = question.replace("\n", " ");
            question = question.replace("\n\n", " ");
            question = question.replace("\"", "'");
            String result = questionId.replace(":sequencecheck", "answer") + chatGptApiClient.getAnswers(question);


            WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(result)));

            ((JavascriptExecutor) driver).executeScript("arguments[0].setAttribute('checked', 'checked');", element);
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        // Закрытие браузера
        driver.quit();
    }

}
