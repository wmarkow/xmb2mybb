package vtech.xmb.grabber;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.orm.hibernate4.HibernateExceptionTranslator;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import vtech.xmb.grabber.db.services.MigrateAll;

@Configuration
@ComponentScan
@EnableAutoConfiguration
public class Application {

  public static void main(String[] args) {
    ApplicationContext ctx = SpringApplication.run(Application.class, args);

    MigrateAll migrateAll = ctx.getBean(MigrateAll.class);
    migrateAll.migrate();
  }

  @Bean
  public JpaVendorAdapter jpaVendorAdapter() {
    HibernateJpaVendorAdapter jpaVendorAdapter = new HibernateJpaVendorAdapter();
    jpaVendorAdapter.setShowSql(false);
    jpaVendorAdapter.setGenerateDdl(false);
    jpaVendorAdapter.setDatabase(Database.MYSQL);
    return jpaVendorAdapter;
  }

  @Bean
  public HibernateExceptionTranslator hibernateExceptionTranslator() {
    return new HibernateExceptionTranslator();
  }
}
