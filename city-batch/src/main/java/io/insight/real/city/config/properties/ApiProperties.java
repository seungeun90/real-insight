package io.insight.real.city.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "city-api")
@Getter
@Setter
public class ApiProperties {
    private String key;
    private String secret;
}
