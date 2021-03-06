package ssau.qa.googlesearch;

import org.apache.commons.io.FileUtils;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static java.lang.Thread.sleep;
import static org.junit.Assert.assertEquals;

public class RunTest {

    private static WebDriver driver;

    private static HashMap<String,String> map = new HashMap<>();

    //выполняется 1 раз перед всеми тестами в этом классе
    @BeforeClass
    public static void run() {
        //указываем путь к хромдрайверу, он отдельно качается
        System.setProperty("webdriver.chrome.driver", "/Users/anka/Downloads/chromedriver_2");
        //инициализируем драйвер
        driver = new ChromeDriver();
        //разворачиваем окно максимально
        driver.manage().window().maximize();
        //неявное ожидаение того, когда браузер всё сделает
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        //переходим по url
        driver.get("https://www.google.ru/");
    }

    //выполняется 1 раз ПОСЛЕ всех тестов в этом классе
    @AfterClass
    public static void end() {
        //выключаем драйвер
        driver.quit();
    }

    @Test @Ignore
    public void search() {
        System.out.println("вводим текст в строку поиска");
        driver.findElement(By.name("q")).sendKeys("netcracker");
        System.out.println("ждём нужного нам результата");
        wait("//div[text()='netcracker']", 2);
        System.out.println("выбираем его");
        driver.findElement(By.xpath("//div[text()='netcracker']")).click();
        System.out.println("ждем, когда появятся результаты поиска");
        wait("//cite[text()='https://www.netcracker.com/']", 2);
        System.out.println("переходим на сайт крекера по ссылке из результата поиска");
        driver.findElement(By.xpath("//cite[text()='https://www.netcracker.com/']/ancestor::div[@data-hveid]//h3/a")).click();
        System.out.println("переключаемся на вторую вкладку");
        switchTab(1);
        System.out.println("ждем, когда откроется сайт крекера, точнее, когда появится нужная нам ссылка");
        wait("//div[@class='container main-nav-container']//a[@href='//www.netcracker.com/careers/']", 5);
        System.out.println("кликаем на ссылка курьера");
        driver.findElement(By.xpath("//body/div[2]/section/nav/div[1]/div[1]/ul/li[2]/a")).click();
        System.out.println("ждем, когда отобразится логотип крекера");
        wait("//div[@class='navbar-header']", 2);
        System.out.println("скроллим до ссылки на Россию");
        WebElement linkRussia = driver.findElement(By.xpath("//a[@href='open-positions/?region=Russia']"));
        Actions actions = new Actions(driver);
        actions.moveToElement(linkRussia);
        actions.perform();
        System.out.println("ждём, когда проскроллиться");
        wait("12321", 1);
        System.out.println("кликаем на линку");
        linkRussia.click();
        wait("//button[@data-id='location']", 10);
        System.out.println("открываем дропдаун менюшку Location");
        driver.findElement(By.xpath("//button[@data-id='location']")).click();
        System.out.println("так как России в списке нет, выбираем Колумбию");
        driver.findElement(By.xpath("//div[@class='dropdown-menu open']//span[text()='Colombia']")).click();
        wait("12321", 1);
        System.out.println("выбираем чекбокс");
        driver.findElement(By.xpath("//fieldset[@class='region-group']//input[@type='checkbox']")).click();
        System.out.println("делаем скриншот");
        File scrFile = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
        System.out.println("переименовываем скриншот");
        String screenshotName = driver.findElement(By.xpath("//header//h1")).getText();
        File destFile = new File( screenshotName + ".png");
        try {
            FileUtils.copyFile(scrFile, destFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        scrFile.delete();
        System.out.println("сохраняем все найденные вакансии в лист");
        List<String> positions = driver.findElements(By.xpath("//div[@class='job result active']//h3")).stream().map(WebElement::getText).collect(Collectors.toList());
        assertEquals("Найденные позиции не соответсвуют ожидаемому результату", positions,
                driver.findElements(By.xpath("//div[@id='positionslist']//div[contains(@data-country,'Colombia')]//h3")).stream().map(WebElement::getText).collect(Collectors.toList()));
        System.out.println();
    }

    @Test
    public void searchWithXLSX() throws InterruptedException {
        System.out.println("вытаскиваем значения из файлика");
        getValuesFromXLSX();
        System.out.println("вводим текст в строку поиска");
        driver.findElement(By.name("q")).sendKeys(map.get("search"));
        System.out.println("ждём нужного нам результата");
        wait("//*[text()='netcracker']", 2);
        System.out.println("выбираем его");
        driver.findElement(By.xpath("//*[text()='netcracker']")).click();
        System.out.println("ждем, когда появятся результаты поиска");
        wait("//cite[text()='https://www.netcracker.com/']", 2);
        System.out.println("переходим на сайт крекера по ссылке из результата поиска");
        driver.findElement(By.xpath("//cite[text()='https://www.netcracker.com/']/../../h3")).click();
        System.out.println("переключаемся на вторую вкладку");
        switchTab(1);
        sleep(5000);
        System.out.println("ждем, когда откроется сайт крекера, точнее, когда появится нужная нам ссылка");
        (new WebDriverWait(driver, 10))
                .until(ExpectedConditions.presenceOfElementLocated(By.xpath("//a[@href='//www.netcracker.com/careers/']")));
        System.out.println("кликаем на ссылка курьера");
        driver.findElement(By.xpath("//body/div[2]/section/nav/div[1]/div[1]/ul/li[2]/a")).click();
        System.out.println("ждем, когда отобразится логотип крекера");
        wait("//div[@class='navbar-header']", 2);
        System.out.println("скроллим до ссылки на Россию");
        WebElement link = driver.findElement(By.xpath("//a[@href='internships.html']"));
        Actions actions = new Actions(driver);
        actions.moveToElement(link);
        actions.perform();
        System.out.println("ждём, когда проскроллиться");
        wait("12321", 1);
        System.out.println("кликаем на линку");
        link.click();
        wait("//button[@data-id='location']", 10);
        System.out.println("открываем дропдаун менюшку Location");
        driver.findElement(By.xpath("//button[@data-id='location']")).click();
        System.out.println("выбираем страну из файла");
        driver.findElement(By.xpath("//div[@class='dropdown-menu open']//span[text()='"+ map.get("country")+"']")).click();
        wait("12321", 1);
        System.out.println("выбираем чекбокс");
        driver.findElement(By.xpath("//fieldset[@class='region-group']//input[@type='checkbox']")).click();
        System.out.println("делаем скриншот");
        File scrFile = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
        System.out.println("переименовываем скриншот");
        File destFile = new File( map.get("screenshotName") + ".png");
        try {
            FileUtils.copyFile(scrFile, destFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        scrFile.delete();
        System.out.println("сохраняем все найденные вакансии в лист");
        List<String> positions = driver.findElements(By.xpath("//div[@class='job result active']//h3")).stream().map(WebElement::getText).collect(Collectors.toList());
        assertEquals("Найденные позиции не соответсвуют ожидаемому результату", positions,
                driver.findElements(By.xpath("//div[@id='positionslist']//div[contains(@data-country,'Colombia')]//h3")).stream().map(WebElement::getText).collect(Collectors.toList()));
        System.out.println();
    }

    private static boolean wait(String element, int timeout) {
        int time = 0;
        while (!isElementPresent(element) && time < timeout) {
            try {
                time++;
                sleep(1000); //1000 милисекунд = 1 секунда
            } catch (InterruptedException e) {}
        }
        return isElementPresent(element);
    }

    private static boolean isElementPresent(String element) {
        try {
            return driver.findElement(By.xpath(element)).isDisplayed();
        } catch (NoSuchElementException | NullPointerException | IndexOutOfBoundsException | StaleElementReferenceException e) {
            return false;
        }
    }

    public void switchTab(int numberOfTab){
        String handle = driver.getWindowHandles().toArray()[numberOfTab].toString();
        driver.switchTo().window(handle);
    }

    private void getValuesFromXLSX() {
        File file = new File(System.getProperty("user.dir") + File.separator + "src" + File.separator + "test" + File.separator + "file.xlsx");
        try (BufferedInputStream stream = new BufferedInputStream(new FileInputStream(file))) {
            XSSFWorkbook wb = new XSSFWorkbook(stream);
            XSSFSheet sheet = wb.getSheetAt(0);
            for (int i = 0; i < 3; i++) {
                map.put(sheet.getRow(i).getCell(0).toString(),sheet.getRow(i).getCell(1).toString());
            }
        } catch (IOException e) {
            System.out.println("Файл не найден");
        }
    }
}
