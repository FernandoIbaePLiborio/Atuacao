package com.gps.selenium;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gps.selenium.model.Defeito;
import com.gps.selenium.model.Integrante;

public class DefaultSuiteTest {

	private WebDriver driver;
	private WebClient web;
	private WebDriverWait webDriverWait;
	private Map<String, Object> vars;
	private JavascriptExecutor js;
	private String bug;
	private String dominioProjeto;
	private List<Integrante> integrantes;

	@Before
	public void setUp() {
//		web.getOptions().setThrowExceptionOnFailingStatusCode(false);
//		web.getOptions().setThrowExceptionOnScriptError(false);
		System.setProperty("webdriver.gecko.driver", "C:\\Users\\a0095499\\Downloads\\selenium-java-3.141.59\\geckodriver\\geckodriver.exe");
		driver = new FirefoxDriver();
		driver.manage().window().maximize();
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		webDriverWait = new WebDriverWait(driver, 10);
		js = (JavascriptExecutor) driver;
		vars = new HashMap<String, Object>();
		Integrante integrante = new Integrante();
		integrante.setName("d.jefferson.ribeiro");
		integrante.setType("DEV");
		if (integrantes == null) {
			integrantes = new ArrayList<Integrante>();
		}
		integrantes.add(integrante);
	}

	@After
	public void tearDown() {
		driver.quit();
	}

	@SuppressWarnings("unchecked")
	public String waitForWindow(int timeout) {
		try {
			Thread.sleep(timeout);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Set<String> whNow = driver.getWindowHandles();
		Set<String> whThen = (Set<String>) vars.get("window_handles");
		if (whNow.size() > whThen.size()) {
			whNow.removeAll(whThen);
		}
		return whNow.iterator().next();
	}

	@Test
	public void buscarAtuacaoDoTime() throws FailingHttpStatusCodeException, MalformedURLException, IOException {

		driver.get("http://10.41.252.111:9010/controle-demanda/defectQuickSearch.jsf");
		
		selectComboValue("fBusca:dominio_label", "li[data-label='QA_QA']");
		webDriverWait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("li[data-label='2020_PJ_EST_ONGOING_B2C']")));
		selectComboValue("fBusca:projeto_label", "li[data-label='2020_PJ_EST_ONGOING_B2C']");
		
		driver.findElement(By.id("fBusca:bugID")).click();
		driver.findElement(By.id("fBusca:bugID")).sendKeys("2129");
		driver.findElement(By.cssSelector(".ui-button-text")).click();
//		vars.put("window_handles", driver.getWindowHandles());
		String oldTab = driver.getWindowHandle();
		driver.findElement(By.linkText("2129")).click();
//		vars.put("win9684", waitForWindow(2000));
//		vars.put("root", driver.getWindowHandle());
		
		List<String> abas = new ArrayList<>(driver.getWindowHandles());
		
		String link = driver.findElement(By.xpath("/html/body/div[2]/div/form[2]/div/div[2]/div/div/table/tbody/tr/td[2]/a")).getAttribute("href");
		if (link != null ) {
			
			driver.get(link);
			List<WebElement> textList = driver.findElements(By.tagName("span"));
			for (Integrante integrante : integrantes) {
				textList.stream().filter(i -> i.getText().contains(integrante.getName())).collect(Collectors.toList());
				if (textList != null) {
					for (WebElement webElement : textList) {
						System.out.println(textList.get(0).getText());
						System.out.println(integrante.getName());
						Defeito def = new Defeito();
					}
				}
			}
		}
		
		driver.get("http://10.41.252.111:9010/controle-demanda/defectQuickSearch.jsf");
		driver.switchTo().window(abas.get(10));
	}

	public void selectComboValue(final String elementId, final String value) {
		driver.findElement(By.id(elementId)).click();
		driver.findElement(By.cssSelector(value)).click();
	}

	public String getBug() {
		return bug;
	}

	public void setBug(String bug) {
		this.bug = bug;
	}

	public String getDominioProjeto() {
		return dominioProjeto;
	}

	public void setDominioProjeto(String dominioProjeto) {
		this.dominioProjeto = dominioProjeto;
	}

	public List<Integrante> getIntegrantes() {
		if (integrantes == null) {
			integrantes = new ArrayList<Integrante>();
		}
		return integrantes;
	}

	public void setIntegrantes(List<Integrante> integrantes) {
		this.integrantes = integrantes;
	}

}
