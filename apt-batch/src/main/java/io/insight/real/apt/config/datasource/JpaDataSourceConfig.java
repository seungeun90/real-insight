package io.insight.real.apt.config.datasource;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
//@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = "io.insight.real.apt.repository.jpa",
        entityManagerFactoryRef = "jpaEntityManagerFactory",
        transactionManagerRef = "jpaTransactionManager"
)
public class JpaDataSourceConfig {

    @Primary
    @Bean(name = "jpaDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.service")
    public DataSource jpaDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "jpaTransactionManager")
    @Primary //
    public JpaTransactionManager jpaTransactionManager(EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }

    @Primary
    @Bean(name = "jpaEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean jpaEntityManagerFactory() {

        LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();
        factoryBean.setDataSource(jpaDataSource());
        factoryBean.setPackagesToScan("io.insight.real.apt.repository.entity"); // 엔티티 패키지 경로 설정
        factoryBean.setJpaVendorAdapter(new HibernateJpaVendorAdapter());

        Properties jpaProperties = new Properties();
        jpaProperties.put("hibernate.hbm2ddl.auto", "update"); // 필요에 따라 변경
        jpaProperties.put("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
        factoryBean.setJpaProperties(jpaProperties);

        return factoryBean;
    }


}
