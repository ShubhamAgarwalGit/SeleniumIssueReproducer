package org.example.seleniumissuereproducer;


import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.Duration;

/**
 * Minimal Spring Boot application to reproduce a Selenium TimeoutException issue.
 * This class implements CommandLineRunner to execute the Selenium logic immediately
 * after the Spring context initializes.
 */
@SpringBootApplication
public class SeleniumIssueReproducer implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(SeleniumIssueReproducer.class, args);
    }

    @Override
    public void run(String... args) {
        WebDriver driver = null;
        try {
            System.out.println("--- Starting Issue Reproducer ---");

            // 1. Setup ChromeDriver using WebDriverManager (Bonigarcia)
            // This will automatically download and set up the correct driver executable.
            WebDriverManager.chromedriver().setup();
            System.out.println("WebDriverManager finished setting up ChromeDriver.");

            // 2. Configure Chrome Options and Timeouts
            ChromeOptions options = new ChromeOptions();
          //  options.addArguments("--headless"); // Optional: run headless for CI or simplicity
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");

            // --- IMPORTANT: Set a page load timeout that might fail in your scenario ---
            // If you are facing a TimeoutException, this is a key place to configure it.
            // Example: set a very short timeout to induce a failure in a slow environment
            // driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(1));
            // However, we will use the default for a basic repro.

            // 3. Initialize the WebDriver
            driver = new ChromeDriver(options);
            System.out.println("ChromeDriver initialized successfully. Selenium 4.38.0");

            // 4. Execute the command that is failing (GET command)
            System.out.println("Attempting to navigate to www.google.com...");
            driver.get("https://www.google.com");

            System.out.println("Successfully navigated to: " + driver.getTitle());

            // Add a small pause so you can see the browser/console output before quitting
            System.out.println("Test successful. Waiting 5 seconds before quitting...");
            Thread.sleep(Duration.ofSeconds(5).toMillis());

        } catch (Exception e) {
            System.err.println("\n\n--- !!! ISSUE REPRODUCER FAILED !!! ---");
            System.err.println("An exception occurred during execution. This should provide the full logs:");

            // This is where you want to ensure the verbose logs you enabled in the previous step appear.
            e.printStackTrace();

            // Ensure the application exits with a non-zero code to signal failure
            System.exit(1);

        } finally {
            // 5. Always quit the driver
            if (driver != null) {
                driver.quit();
                System.out.println("WebDriver quit successfully.");
            }
        }
        System.out.println("--- Issue Reproducer Finished ---");
        System.exit(0); // Gracefully shut down the Spring context
    }
}
