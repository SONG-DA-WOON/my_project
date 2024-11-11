package kr.co.steellink.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
public class SteelLinkUserApplication {

    public static void main(String[] args) {
        SpringApplication.run(SteelLinkUserApplication.class, args);
    }

}
