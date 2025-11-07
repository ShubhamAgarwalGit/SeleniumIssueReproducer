package org.example.seleniumissuereproducer;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.edge.EdgeDriverService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.io.File;
import java.time.Duration;

@SpringBootApplication
public class SeleniumIssueReproducer {

    public static void main(String[] args) {
        SpringApplication.run(SeleniumIssueReproducer.class, args);
    }

    @Bean
    public CommandLineRunner run() {
        return args -> {
            System.out.println("--- Starting Issue Reproducer ---");

            // *** HARDCODED TO [chrome / edge] FOR CURRENT DEBUGGING ***
            final String browserType = "chrome"; // Change to "edge" to test EdgeDriver
            System.out.printf("Browser selected for testing: %s\n", browserType);
            // **********************************************

            // Store the timeout duration in a variable for easy logging
            // CRITICAL: Set to 1 second to GUARANTEE reproduction of the bug
            final Duration timeoutDuration = Duration.ofSeconds(1);

            WebDriver driver = null;
            try {
                // 1. Setup and Initialization
                if ("chrome".equalsIgnoreCase(browserType)) {
                    WebDriverManager.chromedriver().setup();
                    ChromeOptions options = new ChromeOptions();
                    options.addArguments("--headless=new");

                    // Explicitly create the service for robustness (avoids environment issues)
                    File driverFile = new File(System.getProperty(ChromeDriverService.CHROME_DRIVER_EXE_PROPERTY));
                    ChromeDriverService service = new ChromeDriverService.Builder()
                            .usingDriverExecutable(driverFile)
                            .usingAnyFreePort()
                            .build();

                    driver = new ChromeDriver(service, options);
                    System.out.println("ChromeDriver initialized successfully. Selenium 4.38.0");

                } else if ("edge".equalsIgnoreCase(browserType)) {
                    WebDriverManager.edgedriver().setup();
                    EdgeOptions options = new EdgeOptions();
                    options.addArguments("--headless=new");

                    // Explicitly create the service for robustness (avoids environment issues)
                    File driverFile = new File(System.getProperty(EdgeDriverService.EDGE_DRIVER_EXE_PROPERTY));
                    EdgeDriverService service = new EdgeDriverService.Builder()
                            .usingDriverExecutable(driverFile)
                            .usingAnyFreePort()
                            .build();

                    driver = new EdgeDriver(service, options);
                    System.out.println("EdgeDriver initialized successfully. Selenium 4.38.0");
                } else {
                    System.err.println("Unsupported browser type: " + browserType);
                    return;
                }

                // 2. Timeout Configuration
                driver.manage().timeouts().pageLoadTimeout(timeoutDuration);

                // Logging the value that should trigger the bug
                System.out.printf("Page load timeout explicitly set and confirmed for logging: %d seconds.\n",
                        timeoutDuration.toSeconds());
                // **********************************

                // 3. Command Execution (Gives a clean log for the issue report)
                System.out.println("Attempting to navigate to www.google.com...");
                driver.get("https://www.google.com");

                System.out.println("Successfully navigated to Google (unexpected success).");
            } catch (TimeoutException e) {
                // This catch block is crucial for logging the full command info
                System.out.println("\n--- !!! ISSUE REPRODUCER FAILED (Timeout) !!! ---");
                System.out.println("Full command log captured:");
                e.printStackTrace(System.out);
            } catch (Exception e) {
                System.err.println("\n--- !!! FATAL ERROR (Environment/Driver Issue) !!! ---");
                e.printStackTrace(System.err);
            } finally {
                if (driver != null) {
                    driver.quit();
                }
            }
        };
    }
}