package br.com.devfest.codecash;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "br.com.devfest.codecash.repository")
public class CodecashApplication {

	public static void main(String[] args) {
		SpringApplication.run(CodecashApplication.class, args);
	}

}
