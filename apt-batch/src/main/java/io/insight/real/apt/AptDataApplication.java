package io.insight.real.apt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@ConfigurationPropertiesScan
@SpringBootApplication
public class AptDataApplication {

	public static void main(String[] args) {
		SpringApplication.run(AptDataApplication.class, args);
	}

}
