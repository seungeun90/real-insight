package io.insight.real.apt.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "apt-api")
@Getter @Setter
public class ApiProperties {
    private String url;
    private String key;
}
