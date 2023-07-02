package org.example;

import org.openqa.selenium.By;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class OneQuestionTask {

    private static final ChatGptApiClient chatGptApiClient = new ChatGptApiClient();
    public void getQuestion() {
        System.setProperty("webdriver.chrome.silentOutput", "true");
        ChromeOptions opt = new ChromeOptions();
        opt.setExperimentalOption("debuggerAddress", "localhost:9222");
        opt.addArguments("--remote-allow-origins=*");
        opt.addArguments("--remote-allow-origins=*");
        opt.addArguments("--disable-extensions");
        opt.addArguments("--disable-dev-shm-usage");
        opt.addArguments("--disable-gpu");
        opt.addArguments("--no-sandbox");
        ChromeDriver driver = new ChromeDriver(opt);


        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));

        // Ожидание элемента <div> с id "scorm_content"
        WebElement div = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("scorm_content")));

        // Извлечение значения атрибута src из тега <iframe>
        WebElement iframe = div.findElement(By.tagName("iframe"));
        String iframeSrc = iframe.getAttribute("id");

        // Переключение на новый iframe
        driver.switchTo().frame(iframeSrc);


        WebElement span = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("p[style='text-align:center;'] span")));
        String questionText = span.getText();
        System.out.println(questionText);


        List<WebElement> answersElements = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("p[style='text-align:left;'] span")));

        int i = 0;
        String answers = "";

        List<String> answs = new ArrayList<>();

        for (WebElement answerElement : answersElements) {
            answers += "Ответ " + i + ": " + answerElement.getText() + ". ";
            System.out.println(answerElement.getText());
            answs.add(answerElement.getText());
            i++;
        }


        String question = "Дай мне цифру наиболее подходящего ответа. Мне нужен точный ответ, так что можешь думать над ответом столько времени сколько нужно. Вопрос: " + questionText + " " + answers;
        question = question.replace("\n", " ");
        question = question.replace("\n\n", " ");
        question = question.replace("\"", "'");
        int result = Integer.parseInt(chatGptApiClient.getAnswers(question));
        System.out.println(result);


        String txtToFind = answs.get(result);

        // Исполнение JavaScript-скрипта для поиска текста на странице
        String script = "var elements = document.getElementsByTagName('body')[0].getElementsByTagName('*');" +
                "for (var i = 0; i < elements.length; i++) {" +
                "   var element = elements[i];" +
                "   if (element.textContent === arguments[0]) {" +
                "       return element;" +
                "   }" +
                "}" +
                "return null;";
        WebElement textElement = (WebElement) ((JavascriptExecutor) driver).executeScript(script, txtToFind);

        // Проверка наличия элемента с найденным текстом и выполнение нажатия
        if (textElement != null) {
            textElement.click();
        } else {
            System.out.println("Текст не найден на странице");
        }

        driver.quit();
    }
}
