package ru.akkuzin.vkr.backendVKR;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import ru.akkuzin.vkr.backendVKR.services.PeopleService;

@SpringBootApplication
public class BackendVkrApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackendVkrApplication.class, args);

	}

}
