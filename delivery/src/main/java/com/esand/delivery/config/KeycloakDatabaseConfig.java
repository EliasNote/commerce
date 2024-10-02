package com.esand.delivery.config;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "keycloakEntityManagerFactory",
        transactionManagerRef = "keycloakTransactionManager",
        basePackages = { "com.esand.delivery.repository.keycloak" }
)
public class KeycloakDatabaseConfig {

    @Bean(name = "keycloakDataSource")
    @ConfigurationProperties(prefix = "keycloak.datasource")
    public DataSource keycloakDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "keycloakEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean keycloakEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("keycloakDataSource") DataSource dataSource) {

        LocalContainerEntityManagerFactoryBean emFactory = builder
                .dataSource(dataSource)
                .packages("com.esand.delivery.entity")
                .build();

        Map<String, Object> jpaProperties = new HashMap<>();
        jpaProperties.put("hibernate.hbm2ddl.auto", "update");
        emFactory.setJpaPropertyMap(jpaProperties);

        return emFactory;
    }

    @Bean(name = "keycloakTransactionManager")
    public PlatformTransactionManager keycloakTransactionManager(
            @Qualifier("keycloakEntityManagerFactory")
            EntityManagerFactory keycloakEntityManagerFactory) {
        return new JpaTransactionManager(keycloakEntityManagerFactory);
    }
}

