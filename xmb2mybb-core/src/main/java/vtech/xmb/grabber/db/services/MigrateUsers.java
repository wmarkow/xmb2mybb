package vtech.xmb.grabber.db.services;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;

import vtech.xmb.grabber.db.cache.MybbUsersCache;
import vtech.xmb.grabber.db.domain.ProgressCalculator;
import vtech.xmb.grabber.db.mybb.entities.MybbUser;
import vtech.xmb.grabber.db.mybb.entities.MybbUserFields;
import vtech.xmb.grabber.db.mybb.repositories.MybbUserFieldsRepository;
import vtech.xmb.grabber.db.mybb.repositories.MybbUsersRepository;
import vtech.xmb.grabber.db.xmb.entities.XmbMember;
import vtech.xmb.grabber.db.xmb.repositories.XmbMembersRepository;

@Service
public class MigrateUsers {
  private final static Logger LOGGER = Logger.getLogger(MigrateUsers.class);
  private final static Logger ROOT_LOGGER = Logger.getRootLogger();

  @Autowired
  private XmbMembersRepository xmbMembersRepository;
  @Autowired
  private MybbUsersRepository mybbUsersRepository;
  @Autowired
  private MybbUserFieldsRepository mybbUserFieldsRepository;
  @Autowired
  private MybbUsersCache mybbUsersCache;

  @Value("${xmb.forum.domain}")
  private String xmbForumDomain;
  @Value("${mybb.forum.domain}")
  private String mybbForumDomain;
  @Value("${xmb.forum.avatars.path}")
  private String xmbForumAvatarsPath;
  @Value("${mybb.forum.avatars.path}")
  private String mybbForumAvatarsPath;

  @Value("${xmb.avatars.path}")
  private String xmbAvatarsPath;
  @Value("${mybb.avatars.path}")
  private String mybbAvatarsPath;

  private int xmbAvatarsCount = 0;
  private int nativeInvalidXmbAvatarsCount = 0;
  private int otherAvatarsCount = 0;
  
  public void migrateUsers() {
    LOGGER.info("Users migration started.");
    ROOT_LOGGER.info("Users migration started.");

    List<XmbMember> xmbMembers = (List<XmbMember>) xmbMembersRepository.findAll();

    ProgressCalculator progressCalc = new ProgressCalculator(xmbMembers.size());

    LOGGER.info(String.format("Found %s users to migrate from XMB.", xmbMembers.size()));
    ROOT_LOGGER.info(String.format("Found %s users to migrate from XMB.", xmbMembers.size()));

    copyAvatars();

    for (XmbMember xmbMember : xmbMembers) {
      MybbUser mybbUser = new MybbUser();

      applyDefaults(mybbUser);
      updateStatus(mybbUser, xmbMember.status);
      mybbUser.aim = xmbMember.aim;
      mybbUser.avatar = fixAvatarUrlIfNeeded(xmbMember.avatar);
      mybbUser.birthday = deriveMybbBirthDate(xmbMember.bday);
      mybbUser.email = xmbMember.email;
      if (xmbMember.icq.length() < 10) {
        mybbUser.icq = xmbMember.icq;
      } else {
        mybbUser.icq = "";
      }
      mybbUser.password = xmbMember.password;
      mybbUser.regdate = xmbMember.regdate;
      mybbUser.lastvisit = xmbMember.lastvisit;
      mybbUser.lastactive = xmbMember.lastvisit;
      mybbUser.signature = xmbMember.sig;
      mybbUser.username = xmbMember.username;
      mybbUser.website = xmbMember.site;
      mybbUser.xmbUid = xmbMember.uid;
      mybbUser.yahoo = xmbMember.yahoo;
      mybbUser.usertitle = xmbMember.customstatus;
      mybbUser.regip = encodeRegIp(xmbMember);
      mybbUser.tpp = xmbMember.tpp;
      mybbUser.ppp = xmbMember.ppp;
      if ("yes".equals(xmbMember.showemail)) {
        mybbUser.hideemail = 0;
      } else {
        mybbUser.hideemail = 1;
      }
      if ("1".equals(xmbMember.invisible)) {
        mybbUser.invisible = 1;
      } else {
        mybbUser.invisible = 0;
      }

      MybbUser savedUser = mybbUsersRepository.save(mybbUser);

      if (!isNullOrEmpty(xmbMember.bio) || !isNullOrEmpty(xmbMember.location)) {
        MybbUserFields mybbUserFields = new MybbUserFields();
        mybbUserFields.ufid = savedUser.uid;
        mybbUserFields.location = xmbMember.location;
        mybbUserFields.bio = xmbMember.bio;
        mybbUserFields.sex = "Undisclosed";

        mybbUserFieldsRepository.save(mybbUserFields);
      }

      progressCalc.hit();

      progressCalc.logProgress(LOGGER, ROOT_LOGGER);
    }

    mybbUsersCache.evictCache();

    LOGGER.info(String.format("Found %s users in MyBB after migration.", mybbUsersCache.getSize()));
    ROOT_LOGGER.info(String.format("Found %s users in MyBB after migration.", mybbUsersCache.getSize()));
    LOGGER.info(String.format("Migrated %s XMB native avatars with files.", xmbAvatarsCount));
    LOGGER.info(String.format("Found    %s XMB native invalid (not migrated) avatars with files.", nativeInvalidXmbAvatarsCount));
    LOGGER.info(String.format("Migrated %s non-XMB avatars (external links).", otherAvatarsCount));
    LOGGER.info("Users migration finished.");
    ROOT_LOGGER.info("Users migration finished.");
  }

  private void updateStatus(MybbUser mybbUser, String xmbStatus) {
    if (xmbStatus == null) {
      mybbUser.usergroup = 2;
      return;
    }

    if (xmbStatus.equals("Administrator") || xmbStatus.equals("Super Administrator")) {
      mybbUser.usergroup = 4;
      return;
    }

    if (xmbStatus.equals("Super Moderator")) {
      mybbUser.usergroup = 3;
      return;
    }

    if (xmbStatus.equals("Moderator")) {
      mybbUser.usergroup = 6;
      return;
    }

    if (xmbStatus.equals("Banned")) {
      mybbUser.usergroup = 7;
      return;
    }

    mybbUser.usergroup = 2;
  }

  private String deriveMybbBirthDate(String xmbBirthDay) {
    if (xmbBirthDay == null) {
      return "";
    }

    if (xmbBirthDay.trim().isEmpty()) {
      return "";
    }

    if (xmbBirthDay.equals("0000-00-00")) {
      return "";
    }

    try {
      DateTimeFormatter xmbFormatter = DateTimeFormat.forPattern("yyyy-MM-dd");
      DateTimeFormatter mybbFormatter = DateTimeFormat.forPattern("dd-M-yyyy");

      DateTime dt = xmbFormatter.parseDateTime(xmbBirthDay.trim());

      return dt.toString(mybbFormatter);
    } catch (IllegalArgumentException ex) {
      LOGGER.warn(String.format("Can not convert XMB birthday of %s to MyBB", xmbBirthDay));
    }

    return "";
  }

  private boolean isNullOrEmpty(String string) {
    if (string == null) {
      return true;
    }

    if (string.trim().isEmpty()) {
      return true;
    }

    return false;
  }

  private byte[] encodeRegIp(XmbMember xmbMember) {
    String regIp = xmbMember.regip;

    if (regIp == null) {
      return null;
    }

    byte[] result = { 0, 0, 0, 0 };

    // ::ffff:213.25.2
    if (regIp.startsWith("::ffff:")) {
      // it is a IPv6 address and it is incorrectly stored in XMB as in XMB
      // regip's length is restricted to 15 characters
      LOGGER
          .warn(String
              .format(
                  "XMB user with id %s and username %s has an incorrect registration IPv6 address with value %s. Will migrate a 0.0.0.0 as registration ip for that user.",
                  xmbMember.uid, xmbMember.username, xmbMember.regip));
      return result;
    }

    if (regIp.contains(",")) {
      final int index = regIp.indexOf(",");
      final String truncatedRegIp = regIp.substring(0, index);
      LOGGER.warn(String.format("XMB user with id %s and username %s has an incorrect registration IP address with value %s. It will be truncated to %s",
          xmbMember.uid, xmbMember.username, regIp, truncatedRegIp));

      regIp = truncatedRegIp;
    }

    if (regIp.equals("unknown")) {
      LOGGER.warn(String.format(
          "XMB user with id %s and username %s has an unknown registration IP address. Will migrate a 0.0.0.0 as registration ip for that user.",
          xmbMember.uid, xmbMember.username));
      return result;
    }

    String[] splits = regIp.split("\\.");

    if (splits.length != 4) {
      LOGGER.warn(String.format("XMB user with id %s and username %s has an incorrect IPv6 address with value %s.", xmbMember.uid, xmbMember.username,
          xmbMember.regip));
      return null;
    }

    for (int q = 0; q < 4; q++) {
      String split = splits[q];

      ByteBuffer b = ByteBuffer.allocate(4);
      b.putInt(Integer.valueOf(split));

      result[q] = b.get(3);
    }

    return result;
  }

  private void applyDefaults(MybbUser mybbUser) {
    mybbUser.buddylist = "";
    mybbUser.ignorelist = "";
    mybbUser.pmfolders = "";
    mybbUser.notepad = "";
    mybbUser.usernotes = "";
    mybbUser.allowNotices = 1;
    mybbUser.receivepms = 1;
    mybbUser.pmnotice = 1;
    mybbUser.pmnotify = 1;

    mybbUser.showavatars = 1;
    mybbUser.showimages = 1;
    mybbUser.showquickreply = 1;
    mybbUser.showredirect = 1;
    mybbUser.showsigs = 1;
    mybbUser.showvideos = 1;

    mybbUser.classicpostbit = 1;
  }

  private String fixAvatarUrlIfNeeded(String xmbAvatarUrl) {
    if (xmbAvatarUrl == null) {
      return null;
    }

    if (xmbAvatarUrl.trim().isEmpty()) {
      return "";
    }

    xmbAvatarUrl = xmbAvatarUrl.trim().toLowerCase();

    if (xmbAvatarUrl.contains(xmbForumDomain) && xmbAvatarUrl.contains(xmbForumAvatarsPath)) {
      xmbAvatarsCount++;
      return xmbAvatarUrl.replace(xmbForumDomain, mybbForumDomain).replace(xmbForumAvatarsPath, mybbForumAvatarsPath);
    }

    if (xmbAvatarUrl.contains(xmbForumDomain) && !xmbAvatarUrl.contains(xmbForumAvatarsPath)) {
      // incorrect native attachment
      nativeInvalidXmbAvatarsCount ++;
      LOGGER.warn(String.format("Invalid format of XMB native avatar URL: %s. Avatar link will not be migrated.", xmbAvatarUrl));
      return "";
    }

    otherAvatarsCount ++;
    return xmbAvatarUrl;
  }

  private void copyAvatars() {
    File xmbAvatarsDirFile = new File(xmbAvatarsPath);
    File mybbAvatarsDirFile = new File(mybbAvatarsPath);

    mybbAvatarsDirFile.mkdirs();

    try {
      FileSystemUtils.copyRecursively(xmbAvatarsDirFile, mybbAvatarsDirFile);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
