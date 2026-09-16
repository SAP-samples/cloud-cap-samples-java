package my.bookshop.config;

import my.bookshop.Application;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// Use this Application instead of the main one to run the app from the IDE

@SpringBootApplication
public class TestApplication {
  public static void main(String[] args) {
    SpringApplication.from(Application::main).run(args);
  }
}
