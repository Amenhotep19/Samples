using System;
using System.Collections.Generic;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using OpenQA.Selenium;
using OpenQA.Selenium.Remote;

namespace GeicoCarInsuranceCSharp
{
    /// <summary>
    /// Summary description for MobileRemoteTest
    /// </summary>
    [TestClass]
    public class GeicoCarInsurance
    {
        private RemoteWebDriver driver;

        // TODO: Set your cloud host and credentials
        public static String USER_NAME = "MY_USER";
        public static String PASSWORD = "MY_PASSWORD";
        public static String PERFECTO_HOST = "MY_HOST.perfectomobile.com";

        private static String TARGET_EXECUTION = "Desktop"; // If "Desktop" create Web Machine, else run on Mobile browser

        [TestInitialize]
        public void PerfectoOpenConnection()
        {
            // Create Remote WebDriver based on target execution platform
            if (TARGET_EXECUTION == "Desktop")
            {
                DesiredCapabilities capabilities = new DesiredCapabilities();
                capabilities.SetCapability("user", USER_NAME);
                capabilities.SetCapability("password", PASSWORD);

                // Target Web Machine configuration
                capabilities.SetCapability("platformName", "Windows");
                capabilities.SetCapability("platformVersion", "8.1");
                capabilities.SetCapability("browserName", "Chrome");
                capabilities.SetCapability("browserVersion", "54");

                var url = new Uri(string.Format("http://{0}/nexperience/perfectomobile/wd/hub", PERFECTO_HOST));
                driver = new RemoteWebDriverExtended(new HttpAuthenticatedCommandExecutor(url), capabilities);
            }
            else
            {
                var browserName = "mobileOS";
                DesiredCapabilities capabilities = new DesiredCapabilities(browserName, string.Empty, new Platform(PlatformType.Any));
                capabilities.SetCapability("user", USER_NAME);
                capabilities.SetCapability("password", PASSWORD);

                //TODO: Provide your device ID
                capabilities.SetCapability("deviceName", "[ENTER YOUR DEVICE ID HERE]");
                capabilities.SetPerfectoLabExecutionId(PERFECTO_HOST);

                // Add a persona to your script (see https://community.perfectomobile.com/posts/1048047-available-personas)
                //capabilities.SetCapability(WindTunnelUtils.WIND_TUNNEL_PERSONA_CAPABILITY, WindTunnelUtils.GEORGIA);
                
                // Define device allocation timeout, in minutes
                capabilities.SetCapability("openDeviceTimeout", 5);

                // Script name
                capabilities.SetCapability("scriptName", "GeicoCarInsuranceCSharp");

                var url = new Uri(string.Format("http://{0}/nexperience/perfectomobile/wd/hub", PERFECTO_HOST));
                driver = new RemoteWebDriverExtended(new HttpAuthenticatedCommandExecutor(url), capabilities);

            }
            driver.Manage().Timeouts().ImplicitlyWait(TimeSpan.FromSeconds(30));
            driver.Manage().Timeouts().SetPageLoadTimeout(TimeSpan.FromSeconds(30));
        }


        [TestMethod]
        public void GeicoCarInsuranceGetQuote()
        {
            // Navigate to geico.com
            driver.Navigate().GoToUrl("http://geico.com");

            // Maximize browser window on desktop
            if (TARGET_EXECUTION == "Desktop")
            {
                driver.Manage().Window.Maximize();
            }
                
            // Enter form data
            driver.FindElementById("motorcycle").Click();
            driver.FindElement(By.Id("zip")).SendKeys("01434");
            driver.FindElement(By.Id("submitButton")).Click();

            driver.FindElementByXPath("//*[@class = 'row control-item']/div/label[2]").Click();
            driver.FindElement(By.Id("firstName")).SendKeys("MyFirstName");
            driver.FindElement(By.Id("lastName")).SendKeys("MyFamilyName");
            driver.FindElement(By.Id("street")).SendKeys("My Address");
            driver.FindElementById("apt").SendKeys("");
            driver.FindElement(By.Id("date-monthdob")).SendKeys("8");
            driver.FindElement(By.Id("date-daydob")).SendKeys("3");
            driver.FindElement(By.Id("date-yeardob")).SendKeys("1981");

            driver.FindElementById("hasCycle").Click();
        }


        [TestCleanup]
        public void PerfectoCloseConnection()
        {
            // Retrieve the URL of the Single Test Report, can be saved to your execution summary and used to download the report at a later point
            string reportUrl = (string)(driver.Capabilities.GetCapability(WindTunnelUtils.SINGLE_TEST_REPORT_URL_CAPABILITY));

            driver.Close();

            if (TARGET_EXECUTION == "Desktop")
            {
                var parameters = new Dictionary<string, object>();
                driver.ExecuteScript("mobile:execution:close", parameters);
            }

            // In case you want to download the report or the report attachments, do it here.
            //try
            //{
            //    driver.DownloadReport(DownloadReportTypes.pdf, "C:/test/report");
            //    driver.DownloadAttachment(DownloadAttachmentTypes.video, "C:/test/report/video", "flv");
            //    driver.DownloadAttachment(DownloadAttachmentTypes.image, "C:/test/report/images", "jpg");
            //}
            //catch (Exception ex)
            //{
            //    Trace.WriteLine(string.Format("Error getting test logs: {0}", ex.Message));
            //}

            driver.Quit();
        }
    }
}
