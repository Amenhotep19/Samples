package Reporting.Keep;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.perfecto.reportium.client.ReportiumClient;
import com.perfecto.reportium.client.ReportiumClientFactory;
import com.perfecto.reportium.exception.ReportiumException;
import com.perfecto.reportium.model.PerfectoExecutionContext;
import com.perfecto.reportium.model.Project;
import com.perfecto.reportium.test.TestContext;
import com.perfecto.reportium.test.result.TestResultFactory;

import com.google.common.base.Function;
import Reporting.Pages.*;

public class KeepTest {
	private AndroidDriver<MobileElement> driver;
	private String reportName = "KeepTake3";
	protected ReportiumClient repClient;
	
	@DataProvider(name = "NoteLists")
	public static Object[][] notes() {
		return new Object[][] {
			new Object[] {"Creating Automation Test", "Select app to test",
					"Write automation script", "null"},
			new Object[] {"Expand for Reportium", "Update import files", "Add report steps",
					"REM: Tomorrow"},
			new Object[] {"Updating Community Categories", "Decide on set of categories",
					"Review all posts", "REM: Next Week"}
		};
	}
	
	@Parameters({"OperSys", "device", "pkgName", "cloud", "userN", "userPw"})
	@BeforeTest
	public void preTest(String os, String dev, String pkg, String hostC, String uname, String pw) {		
        	try {
			DesiredCapabilities capabilities = new DesiredCapabilities("", "", Platform.ANY);
			String host = hostC;
			// provide Lab credentials
			capabilities.setCapability("user", uname);
			capabilities.setCapability("password", pw);
			// provide application identifier
			capabilities.setCapability("appPackage", pkg);
			// provide device identification
			capabilities.setCapability("deviceName", dev);
			capabilities.setCapability("platformName", os);
			
			// additional capabilities
			capabilities.setCapability("automationName", "Appium");
			capabilities.setCapability("scriptName", "KeepTests");

			// Call this method if you want the script to share the devices with the Perfecto Lab plugin.
			PerfectoLabUtils.setExecutionIdCapability(capabilities, host);
			driver = new AndroidDriver<MobileElement>(new URL("https://" + host + "/nexperience/perfectomobile/wd/hub"), capabilities);
			
			driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
			
		    repClient = createReportiumClient(driver);
        	} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Unable to initialize the Driver instance!");
		}
	}
	
	@AfterTest
	public void finishUp() {
		try {
			
			driver.closeApp();
		    driver.close();

		    System.out.println(repClient.getReportUrl());

		} catch (Exception e) {
		    e.printStackTrace();
		}
		    System.out.println("Report url = " + repClient.getReportUrl());

		driver.quit();
		System.out.println("Run ended");		
	}
	
	@Test(dataProvider = "NoteLists")
	public void createNotes(String title, String item1, String item2, String rem) {
		try {
			StickyBoard first = new StickyBoard(driver, repClient);
			NoteEditor edit = first.createNewList();
			edit.addTitle(title);
			edit.enterItem(item1);
			edit.addItem(item2);
			if (rem.startsWith("REM:")) {
				// set a reminder based on continuation of string - either "Today", "Tomorrow", or "Next Week"
				String whn = rem.substring(5);
				RemindWin remW = edit.remind();
				switch (whn) {
				case "Today":
					remW.pickDate();
					remW.remToday();
					edit = remW.save();
					break;
				case "Tomorrow":
					remW.pickDate();
					remW.remTmrrw();
					edit = remW.save();
					break;
				case "Next Week":
					remW.pickDate();
					remW.remWk();
					edit = remW.save();
					break;
				default:
					edit = remW.cancel();
				}
			}
			first = edit.goBack();
			// verify that note appears on StickyBoard
			repClient.testStep("Verifying that note appears on the bulletin board!");
			if (first.findNote(title))
				System.out.println("Note was successfully added to the Keep app");
			else
				System.out.println("Did not succeed in adding Note to the Keep app");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@BeforeMethod(alwaysRun = true)
	public void beforeTest(Method method) {
	    String testName = method.getDeclaringClass().getName() + "::" + method.getName();
	    repClient.testStart(testName, new TestContext());
	}

	@AfterMethod(alwaysRun = true)
	public void afterTest(ITestResult testResult) {
	    int status = testResult.getStatus();
	    switch (status) {
	        case ITestResult.FAILURE:
	            repClient.testStop(TestResultFactory.createFailure("An error occurred", testResult.getThrowable()));
	            break;
	        case ITestResult.SUCCESS_PERCENTAGE_FAILURE:
	        case ITestResult.SUCCESS:
	            repClient.testStop(TestResultFactory.createSuccess());
	            break;
	        case ITestResult.SKIP:
	            // Ignore
	            break;
	        default:
	            throw new ReportiumException("Unexpected status " + status);
	    }
	}

	private static ReportiumClient createReportiumClient (WebDriver driver) {
	    PerfectoExecutionContext perfectoExecutionContext = new PerfectoExecutionContext.PerfectoExecutionContextBuilder()
	            .withWebDriver(driver)
	            .build();
	    return new ReportiumClientFactory().createPerfectoReportiumClient(perfectoExecutionContext);
	}
}
