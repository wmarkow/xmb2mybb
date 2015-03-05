package vtech.xmb.grabber;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(entityManagerFactoryRef = "xmbEntityManagerFactory", transactionManagerRef = "xmbTransactionManager", basePackages = { "vtech.xmb.grabber.db.xmb" })
public class XmbConfig {

  @Autowired
  private JpaVendorAdapter jpaVendorAdapter;

  @Value("${db.xmb.url}")
  private String databaseUrl;
  @Value("${db.xmb.username}")
  private String databaseUsername;
  @Value("${db.xmb.password}")
  private String databasePasword;
  @Value("${db.xmb.driver.class.name}")
  private String databaseDriverClass;

  @Bean(name = "xmbDataSource")
  public DataSource dataSource() {
    return DataSourceBuilder.create().driverClassName(databaseDriverClass).url(databaseUrl).username(databaseUsername).password(databasePasword).build();
  }

  @Bean(name = "xmbEntityManager")
  public EntityManager entityManager() {
    return entityManagerFactory().createEntityManager();
  }

  @Bean(name = "xmbEntityManagerFactory")
  public EntityManagerFactory entityManagerFactory() {
    LocalContainerEntityManagerFactoryBean lef = new LocalContainerEntityManagerFactoryBean();
    lef.setDataSource(dataSource());
    lef.setJpaVendorAdapter(jpaVendorAdapter);
    lef.setPackagesToScan("vtech.xmb.grabber.db.xmb");
    lef.setPersistenceUnitName("xmbPersistenceUnit");
    lef.afterPropertiesSet();
    return lef.getObject();
  }

  @Bean(name = "xmbTransactionManager")
  public PlatformTransactionManager transactionManager() {
    return new JpaTransactionManager(entityManagerFactory());
  }
}
