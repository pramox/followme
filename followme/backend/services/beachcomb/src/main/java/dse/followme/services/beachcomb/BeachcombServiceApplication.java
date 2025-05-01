package dse.followme.services.beachcomb;

import dse.followme.services.beachcomb.pubsub.Constants;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@EnableConfigurationProperties(Constants.class)
public class BeachcombServiceApplication {
	public static void main(String[] args) {
		SpringApplication.run(BeachcombServiceApplication.class, args);
	}
}
