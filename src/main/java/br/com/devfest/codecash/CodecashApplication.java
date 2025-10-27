package br.com.devfest.codecash;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(exclude = {RedisRepositoriesAutoConfiguration.class})
@EnableJpaRepositories(basePackages = "br.com.devfest.codecash.repository")
public class CodecashApplication {

	public static void main(String[] args) {
		SpringApplication.run(CodecashApplication.class, args);
	}

}
