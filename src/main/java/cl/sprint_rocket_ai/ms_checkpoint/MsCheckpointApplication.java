package cl.sprint_rocket_ai.ms_checkpoint;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MsCheckpointApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsCheckpointApplication.class, args);
	}

}
