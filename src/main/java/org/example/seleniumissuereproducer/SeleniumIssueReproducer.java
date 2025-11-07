package org.example.seleniumissuereproducer;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

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

            // *** HARDCODED TO EDGE ***
            // Browser type is explicitly set to Edge to simplify execution.
            final String browserType = "edge";
            // ***************************

            WebDriver driver = null;
            try {
                // 1. Setup and Initialization
                if ("chrome".equalsIgnoreCase(browserType)) {
                    WebDriverManager.chromedriver().setup();
                    ChromeOptions options = new ChromeOptions();
                    options.addArguments("--headless=new");
                    driver = new ChromeDriver(options);
                    System.out.println("ChromeDriver initialized successfully. Selenium 4.38.0");
                } else if ("edge".equalsIgnoreCase(browserType)) {
                    // This line (WebDriverManager.edgedriver().setup()) is where the driver
                    // is downloaded and the system property is set.
                    WebDriverManager.edgedriver().setup();

                    EdgeOptions options = new EdgeOptions();
                    options.addArguments("--headless=new");

                    // The next line (new EdgeDriver(options)) is where the NullPointerException
                    // in your system's environment variables is triggered.
                    driver = new EdgeDriver(options);
                    System.out.println("EdgeDriver initialized successfully. Selenium 4.38.0");
                } else {
                    System.err.println("Unsupported browser type: " + browserType);
                    return;
                }

                // 2. Timeout Configuration
                driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(1));
                System.out.println("Page load timeout set to 1 second to induce a failure.");

                // 3. Command Execution
                System.out.println("Attempting to navigate to www.google.com...");
                driver.get("https://www.google.com");

                System.out.println("Successfully navigated to Google (unexpected success).");
            } catch (TimeoutException e) {
                // Catch for the main issue you want to report (The TimeoutException)
                System.out.println("\n--- !!! ISSUE REPRODUCER FAILED (Timeout) !!! ---");
                System.out.println("Full command log captured:");
                e.printStackTrace(System.out);
            } catch (Exception e) {
                // Catch for the environment-specific error (NullPointerException when checking system vars)
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