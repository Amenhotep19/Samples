import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URL;

public class driverCreator {

	//TODO: Set your Host and Security Token for Perfecto Lab
    static String PERFECTO_HOST = "MY_HOST.perfectomobile.com";
    static String PERFECTO_TOKEN = "MY_SECURITY_TOKEN";

    //Old school credentials, we recommend using Security Token instead.
    //static String PERFECTO_USER = "MY_USER";
    //static String PERFECTO_PASS = "MY_PASS";




    public static RemoteWebDriver init_driver(String platformName, String platformVersion, String browserName, String browserVersion, String screenResolution) throws MalformedURLException {

        DesiredCapabilities capabilities = new DesiredCapabilities().chrome();
        capabilities.setCapability("securityToken", PERFECTO_TOKEN);
        //capabilities.setCapability("user", PERFECTO_USER);
        //capabilities.setCapability("password", PERFECTO_PASS);
        capabilities.setCapability("platformName", platformName);
        capabilities.setCapability("platformVersion", platformVersion);
        capabilities.setCapability("browserName", browserName);
        capabilities.setCapability("browserVersion", browserVersion);
        capabilities.setCapability("resolution", screenResolution);

        return new RemoteWebDriver(new URL("https://" + PERFECTO_HOST + "/nexperience/perfectomobile/wd/hub"), capabilities);
    }

}
