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
        ChromeDriver driver = null;

        try {
            System.setProperty("webdriver.chrome.silentOutput", "true");
            ChromeOptions opt = new ChromeOptions();
            opt.setExperimentalOption("debuggerAddress", "localhost:9222");
            opt.addArguments("--remote-allow-origins=*");
            opt.addArguments("--disable-extensions");
            opt.addArguments("--disable-dev-shm-usage");
            opt.addArguments("--disable-gpu");
            opt.addArguments("--no-sandbox");
            driver = new ChromeDriver(opt);

            String script = "var inputs = document.querySelectorAll('input[id^=\"q\"]');" +
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
                String question = "";
                boolean flag = false;
                boolean subFlag = false;

                for (WebElement answerElement : answerElements) {
                    String answerText = answerElement.getText();
                    answers += "Ответ " + i + ": " + answerText + ". ";

                    if(i == 0){
                        if(answerElement.getAttribute("for").contains("choice")){
                            flag = true;
                        } else if(answerElement.getAttribute("for").contains("sub")){
                            subFlag = true;
                        }
                    }
                    i++;
                }

                if(subFlag){
                    continue;
                }


                if(flag) {
                    question = "В ответ дай мне только цифры наиболее подходящих ответов через запятую (скорее всего их несколько). Мне нужен точный ответ, так что можешь думать над ответом столько времени сколько нужно. Вопрос: " + questionText + ". " + answers;
                } else {
                    question = "В ответ дай мне только цифру наиболее подходящего ответа. Мне нужен точный ответ, так что можешь думать над ответом столько времени сколько нужно. Вопрос: " + questionText + ". " + answers;
                }

                question = question.replace("\n", " ");
                question = question.replace("\n\n", " ");
                question = question.replace("\"", "'");
                String preResult = chatGptApiClient.getAnswers(question);
                String result = "";
                if(preResult.contains(",")){
                    String[] arrAnswers = preResult.split(",");
                    int index = 0;
                    while (index < arrAnswers.length) {
                        String el = arrAnswers[index];
                        result = questionId.replace(":sequencecheck", "choice") + el;
                        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(result)));
                        ((JavascriptExecutor) driver).executeScript("arguments[0].setAttribute('checked', 'checked');", element);
                        index++;
                    }
                } else {
                    result = questionId.replace(":sequencecheck", "answer") + preResult;
                    WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(result)));
                    ((JavascriptExecutor) driver).executeScript("arguments[0].setAttribute('checked', 'checked');", element);
                }

                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e){
                    throw new RuntimeException(e);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (driver != null) {
                driver.quit();
            }
        }
    }
}
