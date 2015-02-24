package vtech.xmb.grabber;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
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

  @Bean(name = "xmbDataSource")
  @ConfigurationProperties(prefix = "spring.datasource.primary")
  public DataSource dataSource() {
    return DataSourceBuilder.create().driverClassName("com.mysql.jdbc.Driver").url("jdbc:mysql://localhost/baza7446?useUnicode=yes&characterEncoding=UTF-8").username("xmbuser").password("xmbuser")
        .build();
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
