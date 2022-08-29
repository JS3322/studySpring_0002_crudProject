package com.example.lecture_spring_2_crudproject.service.textTransfer;

import org.openqa.selenium.By;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.stereotype.Service;
//import io.github.bonigarcia.wdm.WebDriverManager;

import java.time.Duration;
import java.util.List;

//https://chromedriver.chromium.org/downloads

@Service
public class Selenium {

    private WebDriver driver;
    public static final String WEB_DRIVER_ID = "webdriver.chrome.driver";
    public static final String WEB_DRIVER_PATH = "/Users/js/Cleancode/lecture_spring_2_crudProject/src/main/resources/static/tool/chromedriver";

    private String base_url;

    public void scraping() {

        System.setProperty(WEB_DRIVER_ID, WEB_DRIVER_PATH);

        driver = new ChromeDriver();
        base_url = "https://www.fragrantica.com/perfume/Maison-Francis-Kurkdjian/Baccarat-Rouge-540-33519.html";
        driver.manage().timeouts().implicitlyWait(Duration.ofMillis(50000));

        try{
            driver.get(base_url);
            driver.manage().timeouts().implicitlyWait(Duration.ofMillis(50000));

            System.out.println(driver.getPageSource());

//            WebElement textBox = driver.findElement(By.name("my-text"));
//            WebElement submitButton = driver.findElement(By.cssSelector("button"));
//            driver.get("https://www.fragrantica.com/perfume/Maison-Francis-Kurkdjian/Baccarat-Rouge-540-33519.html");
//            WebElement element = driver.findElement(By.tagName("div"));
//
//            List<WebElement> elements = element.findElements(By.tagName("p"));
//            for (WebElement e : elements) {
//                System.out.println(e.getText());
//            }
        }catch(Exception e) {
            e.printStackTrace();

        }finally {
            driver.quit();
        }
    }
}