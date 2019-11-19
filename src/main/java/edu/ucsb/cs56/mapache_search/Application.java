package edu.ucsb.cs56.mapache_search;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.context.annotation.PropertySource;

@EnableOAuth2Sso
@SpringBootApplication
@PropertySource(value = "localhost.json", factory = JsonPropertySourceFactory.class, ignoreResourceNotFound = true)
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
