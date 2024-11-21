package swe.second.team_matching_server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.stereotype.Component;

@SpringBootApplication(scanBasePackages = {"swe.second.team_matching_server.domain.file.repository"})
public class TeamMatchingServerApplication {

	
	public static void main(String[] args) {
		System.setProperty("slf4j.internal.verbosity", "WARN");
		SpringApplication.run(TeamMatchingServerApplication.class, args);
	}

}
