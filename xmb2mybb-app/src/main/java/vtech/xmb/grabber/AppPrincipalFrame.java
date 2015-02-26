package vtech.xmb.grabber;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.annotation.PostConstruct;
import javax.swing.JButton;
import javax.swing.JFrame;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import vtech.xmb.grabber.db.services.MigrateForums;
import vtech.xmb.grabber.db.services.MigratePosts;
import vtech.xmb.grabber.db.services.MigrateThreads;
import vtech.xmb.grabber.db.services.MigrateUsers;

@Component("asd")
public class AppPrincipalFrame extends JFrame implements WindowListener {

  @Autowired
  private ConfigurableApplicationContext context;

  // @Autowired
  // private XmbU2UMigrationService xmbMigrationService;
  //
  // @Autowired
  // private XmbVotesMigrationService xmbVotesMigrationService;

  @Autowired
  private MigrateUsers migrateUsers;
  @Autowired
  private MigrateForums migrateForums;
  @Autowired
  private MigrateThreads migrateThreads;
  @Autowired
  private MigratePosts migratePosts;

  @PostConstruct
  public void test() {
    setTitle("Spring booted Swing application frame");
    setSize(400, 200);
    setVisible(true);
    addWindowListener(this);
    JButton wczytaj = new JButton("wczytaj");

    this.add(wczytaj);

    // migrateUsers.migrateUsers();
    // migrateForums.migrateForums();
    // migrateThreads.migrateThreads();
    migratePosts.migratePosts();
  }

  @Override
  public void windowOpened(WindowEvent e) {
  }

  @Override
  public void windowClosing(WindowEvent e) {
    System.out.println("closing");
    context.close();
  }

  @Override
  public void windowClosed(WindowEvent e) {
  }

  @Override
  public void windowIconified(WindowEvent e) {
  }

  @Override
  public void windowDeiconified(WindowEvent e) {
  }

  @Override
  public void windowActivated(WindowEvent e) {
  }

  @Override
  public void windowDeactivated(WindowEvent e) {
  }
}