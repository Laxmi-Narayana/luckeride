package com.lucke.luckeride;

import com.lucke.luckeride.auth.security.JwtProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(JwtProperties.class)
public class LuckerideApplication {

    public static void main(String[] args) {
        SpringApplication.run(LuckerideApplication.class, args);
    }

}
