package vtech.xmb.grabber.db.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MigrateAll {
  @Autowired
  private MigrateUsers migrateUsers;
  @Autowired
  private MigrateForums migrateForums;
  @Autowired
  private MigrateThreads migrateThreads;
  @Autowired
  private MigratePosts migratePosts;
  @Autowired
  private MigratePolls migratePolls;
  @Autowired
  private MigratePrivateMessages migratePrivateMessages;
  @Autowired
  private MigrateModeratorPermissions migrateModeratorPermissions;
  @Autowired
  private MigrateAttachments migrateAttachments;

  public void migrate() {
    // migrateUsers.migrateUsers();
    // migrateForums.migrateForums();
    // migrateModeratorPermissions.migrateModeratorPermissions();
    // migrateThreads.migrateThreads();
    // migratePosts.migratePosts();
    // migratePolls.migratePolls();
    // migratePrivateMessages.migrateU2Us();
    migrateAttachments.migrateAttachments();
  }
}
