/*package core.web;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class SelmiCrawler extends WebCrawler
{
    public SelmiCrawler()
    {
        super("https://solucoesabc.involves.com/login/#/");
    }

    public void crawl()
    {
        WebDriver driver = new ChromeDriver();

        driver.get(this.getBaseUrl());

        WebElement username = driver.findElement(By.xpath("//*[@id=\"username\"]"));
        username.click();
        username.sendKeys("joao.vitor");

        WebElement password = driver.findElement(By.xpath("//*[@id=\"password\"]"));
        password.click();
        password.sendKeys("123456");

        WebElement loginButton = driver.findElement(By.xpath("/html/body/div/div[1]/div/form/div[2]/div[3]/button"));
        loginButton.click();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20).getSeconds());

        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"app\"]/div/div[2]/ag-view/div/ag-view-body/div/div/div[1]/div/div/div[2]/div")));
        element.click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"app\"]/div[1]/ap-menu/div/div/div[1]")));

        driver.get("https://solucoesabc.involves.com/webapp/#!/app/tTUiuIthv3HkbFguykZDcw==/colaboradores");
    }
}
*/