package io.insight.real.apt.config.datasource;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

@Configuration
@EnableR2dbcRepositories(basePackages = "io.insight.real.apt.repository.r2dbc")
public class R2dbcConfig {
}
