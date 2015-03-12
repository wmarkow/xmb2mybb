package vtech.xmb.grabber;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.hibernate4.HibernateExceptionTranslator;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import vtech.xmb.grabber.db.services.MigrateAll;

@Configuration
@ComponentScan
@EnableAutoConfiguration
public class Application {
  private final static Logger LOGGER = Logger.getLogger(Application.class);

  @Value("${xmb.forum.links.prefix}")
  private String xmbForumLinksPrefix;
  @Value("${mybb.forum.links.prefix}")
  private String mybbForumLinksPrefix;
  
  @Value("${xmb.attachments.path}")
  private String xmbAttachmentsPath;
  @Value("${mybb.attachments.path}")
  private String mybbAttachmentsPath;

  @Autowired
  private ConfigurableApplicationContext ctx;

  public static void main(String[] args) {
    ConfigurableApplicationContext ctx = SpringApplication.run(Application.class, args);

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

  @PostConstruct
  private void check() {
    boolean exit = false;
    
    if (!xmbForumLinksPrefix.endsWith("/")) {
      LOGGER.error(String.format("xmb.forum.links.prefix must end with a '/'!!! Current value is %s.", xmbForumLinksPrefix));

      exit = true;
    }

    if (!mybbForumLinksPrefix.endsWith("/")) {
      LOGGER.error(String.format("mybb.forum.links.prefix must end with a '/'!!! Current value is %s.", mybbForumLinksPrefix));

      exit = true;
    }
    
    if (!xmbAttachmentsPath.endsWith("/")) {
      LOGGER.error(String.format("xmb.attachments.path must end with a '/'!!! Current value is %s.", xmbAttachmentsPath));

      exit = true;
    }
    
    if (!mybbAttachmentsPath.endsWith("/")) {
      LOGGER.error(String.format("mybb.attachments.path must end with a '/'!!! Current value is %s.", mybbAttachmentsPath));

      exit = true;
    }
    
    if(exit){
      exit();
    }
  }
  
  private void exit(){
    ctx.refresh();
    ctx.stop();
    ctx.close();
  }
}
