package ru.netology.card;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.Condition.*;

public class FormTest {
    @BeforeAll
    static void setUpAll() {
        SelenideLogger.addListener("allure", new AllureSelenide());
    }

    @AfterAll
    static void tearDownAll() {
        SelenideLogger.removeListener("allure");
    }

    @BeforeEach
    public void openBrowser() {
        open("http://localhost:9999/");
    }

    public String generateDate(int days) {
        return LocalDate.now().plusDays(days).format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }

    public void enterValidPhone() {
        $("[data-test-id=\"phone\"] span span input").sendKeys("+79991234567");
    }

    @Test
    public void criticalPath() {
        $("[data-test-id=\"city\"] input").sendKeys("Москва");
        $("[data-test-id='date'] input").sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE);
        String planningDate = generateDate(3);
        $("[data-test-id=\"date\"] input").sendKeys(planningDate);
        $("[data-test-id=\"name\"] input").sendKeys("Иванов Иван");
        enterValidPhone();
        $("[data-test-id=\"agreement\"]").click();
        $x("//*[contains(text(),'Забронировать')]").click();
        $(".notification__content")
                .shouldHave(Condition.text("Встреча успешно забронирована на " + planningDate), Duration.ofSeconds(15))
                .shouldBe(Condition.visible);
    }

    @Test
    public void wrongCity() {
        $("[data-test-id=\"city\"] input").sendKeys("Неизвестность");
        $("[data-test-id='date'] input").sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE);
        String planningDate = generateDate(3);
        $("[data-test-id=\"date\"] input").sendKeys(planningDate);
        $("[data-test-id=\"name\"] input").sendKeys("Иванов Иван");
        enterValidPhone();
        $("[data-test-id=\"agreement\"]").click();
        $x("//*[contains(text(),'Забронировать')]").click();
        $x("//*[contains(text(),'Доставка в выбранный город недоступна')]").shouldBe(visible);
    }

    @Test
    public void tooEarly() {
        $("[data-test-id=\"city\"] input").sendKeys("Москва");
        $("[data-test-id='date'] input").sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE);
        String planningDate = generateDate(2);
        $("[data-test-id=\"date\"] input").sendKeys(planningDate);
        $("[data-test-id=\"date\"] input").sendKeys(planningDate);
        $("[data-test-id=\"name\"] input").sendKeys("Иванов Иван");
        enterValidPhone();
        $("[data-test-id=\"agreement\"]").click();
        $x("//*[contains(text(),'Забронировать')]").click();
        $x("//*[contains(text(),'Заказ на выбранную дату невозможен')]").shouldBe(visible);
    }

    @Test
    public void wrongDate() {
        $("[data-test-id=\"city\"] input").sendKeys("Москва");
        $("[data-test-id='date'] input").sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE);
        $("[data-test-id=\"date\"] input").sendKeys("33.33.3333");
        $("[data-test-id=\"name\"] input").sendKeys("Иванов Иван");
        enterValidPhone();
        $("[data-test-id=\"agreement\"]").click();
        $x("//*[contains(text(),'Забронировать')]").click();
        $x("//*[contains(text(),'Неверно введена дата')]").shouldBe(visible);
    }

    @Test
    public void positiveNameMinus() {
        $("[data-test-id=\"city\"] input").sendKeys("Москва");
        $("[data-test-id='date'] input").sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE);
        String planningDate = generateDate(3);
        $("[data-test-id=\"date\"] input").sendKeys(planningDate);
        $("[data-test-id=\"name\"] input").sendKeys("Мак-Кинли Иван");
        enterValidPhone();
        $("[data-test-id=\"agreement\"]").click();
        $x("//*[contains(text(),'Забронировать')]").click();
        $(".notification__content")
                .shouldHave(Condition.text("Встреча успешно забронирована на " + planningDate), Duration.ofSeconds(15))
                .shouldBe(Condition.visible);
    }

    @Test
    public void wrongName() {
        $("[data-test-id=\"city\"] input").sendKeys("Москва");
        $("[data-test-id='date'] input").sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE);
        String planningDate = generateDate(3);
        $("[data-test-id=\"date\"] input").sendKeys(planningDate);
        $("[data-test-id=\"name\"] input").sendKeys("Qwerty John");
        enterValidPhone();
        $("[data-test-id=\"agreement\"]").click();
        $x("//*[contains(text(),'Забронировать')]").click();
        $x("//*[contains(text(),'Имя и Фамилия указаные неверно')]").shouldBe(visible);
    }

    @Test
    public void phoneTooShort() {
        $("[data-test-id=\"city\"] input").sendKeys("Москва");
        $("[data-test-id='date'] input").sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE);
        String planningDate = generateDate(3);
        $("[data-test-id=\"date\"] input").sendKeys(planningDate);
        $("[data-test-id=\"name\"] input").sendKeys("Иванов Иван");
        $("[data-test-id=\"phone\"] input").sendKeys("+7999123456");
        $("[data-test-id=\"agreement\"]").click();
        $x("//*[contains(text(),'Забронировать')]").click();
        $x("//*[contains(text(),'Телефон указан неверно. Должно быть 11 цифр')]").shouldBe(visible);
    }

    @Test
    public void phoneMissPlacedPlus() {
        $("[data-test-id=\"city\"] input").sendKeys("Москва");
        $("[data-test-id='date'] input").sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE);
        String planningDate = generateDate(3);
        $("[data-test-id=\"date\"] input").sendKeys(planningDate);
        $("[data-test-id=\"name\"] input").sendKeys("Иванов Иван");
        $("[data-test-id=\"phone\"] input").sendKeys("7+9991234567");
        $("[data-test-id=\"agreement\"]").click();
        $x("//*[contains(text(),'Забронировать')]").click();
        $x("//*[contains(text(),'Телефон указан неверно.')]").shouldBe(visible);
    }

    @Test
    public void noAgreement() {
        String fullCLass = "checkbox checkbox_size_m checkbox_theme_alfa-on-white input_invalid";
        $("[data-test-id=\"city\"] input").sendKeys("Москва");
        $("[data-test-id='date'] input").sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE);
        String planningDate = generateDate(3);
        $("[data-test-id=\"date\"] input").sendKeys(planningDate);
        $("[data-test-id=\"name\"] input").sendKeys("Иванов Иван");
        enterValidPhone();
        $x("//*[contains(text(),'Забронировать')]").click();
        $("[data-test-id=\"agreement\"]").shouldHave(
                attribute("class", fullCLass)
        );
    }
}
