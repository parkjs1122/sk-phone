package skphone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.web.reactive.config.EnableWebFlux;

@SpringBootApplication
@EnableWebFlux
public class SkphoneApplication {

	public static void main(String[] args) {
		SpringApplication.run(SkphoneApplication.class, args);
	}

}
