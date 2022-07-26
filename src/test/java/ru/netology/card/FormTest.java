package ru.netology.card;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Calendar;
import java.util.Date;

import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.Condition.*;

public class FormTest {
    @BeforeEach
    public void openBrowser() {
        open("http://localhost:9999/");
    }

    public void clearDateField() {
        //chromium bug workaround
        for (int i = 0; i < 9; i++) {
            $("[data-test-id=\"date\"] span span input").sendKeys(Keys.BACK_SPACE);
        }
    }

    public void enterValidCity() {
        $("[data-test-id=\"city\"] span span input").sendKeys("Москва");
    }

    public void enterValidDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, 3);
        Date date = calendar.getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
        String dateString = formatter.format(date);
        clearDateField();
        $("[data-test-id=\"date\"] span span input").sendKeys(dateString);
    }

    public void enterValidName() {
        $("[data-test-id=\"name\"] span span input").sendKeys("Иванов Иван");
    }

    public void enterValidPhone() {
        $("[data-test-id=\"phone\"] span span input").sendKeys("+79991234567");
    }

    public void clickAgreement() {
        $("[data-test-id=\"agreement\"]").click();
    }

    public void clickSubmit() {
        $x("//*[contains(text(),'Забронировать')]").click();
    }

    @Test
    public void criticalPath() throws ParseException {
        enterValidCity();
        enterValidDate();
        enterValidName();
        enterValidPhone();
        clickAgreement();
        clickSubmit();
        $x("//div[@data-test-id=\"notification\"]/div[contains(text(),\"Успешно!\")]").shouldBe(
                visible,
                Duration.ofSeconds(15)
        );
    }

    @Test
    public void wrongCity() {
        $("[data-test-id=\"city\"] span span input").sendKeys("Неизвестность");
        enterValidDate();
        enterValidName();
        enterValidPhone();
        clickAgreement();
        clickSubmit();
        $x("//*[contains(text(),'Доставка в выбранный город недоступна')]").shouldBe(visible);
    }

    @Test
    public void tooEarly() {
        enterValidCity();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, 2);
        Date date = calendar.getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
        String dateString = formatter.format(date);
        clearDateField();
        $("[data-test-id=\"date\"] span span input").sendKeys(dateString);
        enterValidName();
        enterValidPhone();
        clickAgreement();
        clickSubmit();
        $x("//*[contains(text(),'Заказ на выбранную дату невозможен')]").shouldBe(visible);
    }

    @Test
    public void wrongDate() {
        enterValidCity();
        clearDateField();
        $("[data-test-id=\"date\"] span span input").sendKeys("33.33.3333");
        enterValidName();
        enterValidPhone();
        clickAgreement();
        clickSubmit();
        $x("//*[contains(text(),'Неверно введена дата')]").shouldBe(visible);
    }

    @Test
    public void positiveNameMinus() {
        enterValidCity();
        enterValidDate();
        $("[data-test-id=\"name\"] span span input").sendKeys("Мак-Кинли Иван");
        enterValidPhone();
        clickAgreement();
        clickSubmit();
        $x("//div[@data-test-id=\"notification\"]/div[contains(text(),\"Успешно!\")]").shouldBe(
                visible,
                Duration.ofSeconds(15)
        );
    }

    @Test
    public void wrongName() {
        enterValidCity();
        enterValidDate();
        $("[data-test-id=\"name\"] span span input").sendKeys("Qwerty John");
        enterValidPhone();
        clickAgreement();
        clickSubmit();
        $x("//*[contains(text(),'Имя и Фамилия указаные неверно')]").shouldBe(visible);
    }

    @Test
    public void phoneTooShort() {
        enterValidCity();
        enterValidDate();
        enterValidName();
        $("[data-test-id=\"phone\"] span span input").sendKeys("+7999123456");
        clickAgreement();
        clickSubmit();
        $x("//*[contains(text(),'Телефон указан неверно. Должно быть 11 цифр')]").shouldBe(visible);
    }

    @Test
    public void phoneMissPlacedPlus() {
        enterValidCity();
        enterValidDate();
        enterValidName();
        $("[data-test-id=\"phone\"] span span input").sendKeys("7+9991234567");
        clickAgreement();
        clickSubmit();
        $x("//*[contains(text(),'Телефон указан неверно.')]").shouldBe(visible);
    }

    @Test
    public void noAgreement() {
        String fullCLass = "checkbox checkbox_size_m checkbox_theme_alfa-on-white input_invalid";
        enterValidCity();
        enterValidDate();
        enterValidName();
        enterValidPhone();
        clickSubmit();
        $("[data-test-id=\"agreement\"]").shouldHave(
                attribute("class", fullCLass)
        );
    }
}
