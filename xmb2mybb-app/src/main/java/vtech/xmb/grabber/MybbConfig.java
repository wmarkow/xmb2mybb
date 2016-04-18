package vtech.xmb.grabber;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.flyway.FlywayDataSource;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
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
@EnableAutoConfiguration
public class MybbConfig {

  @Autowired
  private JpaVendorAdapter jpaVendorAdapter;

  @Value("${db.mybb.url}")
  private String databaseUrl;
  @Value("${db.mybb.username}")
  private String databaseUsername;
  @Value("${db.mybb.password}")
  private String databasePasword;
  @Value("${db.mybb.driver.class.name}")
  private String databaseDriverClass;

  @Bean(name = "mybbDataSource")
  @Primary
  @FlywayDataSource
  public DataSource dataSource() {
    return DataSourceBuilder.create().driverClassName(databaseDriverClass).url(databaseUrl).username(databaseUsername).password(databasePasword).build();
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
}
