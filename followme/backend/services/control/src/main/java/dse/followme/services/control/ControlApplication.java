package dse.followme.services.control;

import dse.followme.services.control.pubsub.Constants;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class })
@EnableConfigurationProperties(Constants.class)
public class ControlApplication {

	public static void main(String[] args) {
		SpringApplication.run(ControlApplication.class, args);
	}

}
