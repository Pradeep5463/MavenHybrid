package driverFactory;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.testng.Reporter;
import org.testng.annotations.Test;

import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

import commonFunctions.FunctionLibrary;
import config.AppUtil;
import utilities.ExcelFileUtil;

public class AppTest extends AppUtil{
String inputpath = "./FileInput/LoginData.xlsx";
String outputpath = "./FileOutput/DataDrivenResults.xlsx";
boolean res = false;
ExtentReports report;
ExtentTest logger;
@Test
public void startTest() throws Throwable
{
//define path of html
	report = new ExtentReports("./Reports/Login.html");
	//Create object for ExcelFileUtil class
	ExcelFileUtil xl = new ExcelFileUtil(inputpath);
	//count no of rows in login sheet
	int rc = xl.rowCount("Login");
	Reporter.log("No. of rows are::"+rc,true);
	//iterate all rows in login sheet
	for(int i=1;i<=rc;i++)
	{
		//test case starts here
		logger = report.startTest("Validate_Login");
		//read username and password cells
		String username = xl.getCellData("Login", i, 0);
		String password = xl.getCellData("Login", i, 1);
		//cal login method from function library class
		res = FunctionLibrary.Verify_Login(username, password);
		if(res)
		{
			//if res is true write as login success into results cell
			xl.setCellData("Login", i, 2, "Login Success", outputpath);
			//write as pass into status cell
			xl.setCellData("Login", i, 3, "Pass", outputpath);
			logger.log(LogStatus.PASS, "Valid Username and Password");
		}
		else
		{
			//take screenshot for fail steps
			File Screen = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
			FileUtils.copyFile(Screen, new File("./Screenshot/Iteration/"+i+"Loginpage.png"));
			//capture error message
			String Error_Message = driver.findElement(By.xpath(conpro.getProperty("Objerror"))).getText();
			xl.setCellData("Login", i, 2, Error_Message, outputpath);
			//write as fail into status cell
			xl.setCellData("Login", i, 3, "Fail", outputpath);
			logger.log(LogStatus.FAIL, Error_Message);
		}
		report.endTest(logger);
		report.flush();
	}
	
}




}
