package vtech.xmb.grabber;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.flyway.FlywayDataSource;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(entityManagerFactoryRef = "mybbEntityManagerFactory", transactionManagerRef = "mybbTransactionManager", basePackages = { "vtech.xmb.grabber.db.mybb" })
@ComponentScan
public class MybbConfig {

  @Autowired
  private JpaVendorAdapter jpaVendorAdapter;

  @Bean(name = "mybbDataSource")
  @ConfigurationProperties(prefix = "spring.datasource.primary")
  @FlywayDataSource
  public DataSource dataSource() {
    return DataSourceBuilder.create().driverClassName("com.mysql.jdbc.Driver").url("jdbc:mysql://localhost/mybb?useUnicode=yes&characterEncoding=UTF-8")
        .username("mybbuser").password("mybbuser").build();
  }

  @Bean(name = "mybbEntityManager")
  public EntityManager entityManager() {
    return entityManagerFactory().createEntityManager();
  }

  @Bean(name = "mybbEntityManagerFactory")
  public EntityManagerFactory entityManagerFactory() {
    LocalContainerEntityManagerFactoryBean lef = new LocalContainerEntityManagerFactoryBean();
    lef.setDataSource(dataSource());
    lef.setJpaVendorAdapter(jpaVendorAdapter);
    lef.setPackagesToScan("vtech.xmb.grabber.db.mybb");
    lef.setPersistenceUnitName("xmbPersistenceUnit");
    lef.afterPropertiesSet();
    return lef.getObject();
  }

  @Bean(name = "mybbTransactionManager")
  public PlatformTransactionManager transactionManager() {
    return new JpaTransactionManager(entityManagerFactory());
  }

  @PostConstruct
  private void migrate() {
    org.flywaydb.core.Flyway flyway = new org.flywaydb.core.Flyway();
    flyway.setDataSource(dataSource());

    flyway.setInitOnMigrate(true);
    flyway.migrate();
  }
}
