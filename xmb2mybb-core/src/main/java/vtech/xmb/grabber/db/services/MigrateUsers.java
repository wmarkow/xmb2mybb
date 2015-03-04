package vtech.xmb.grabber.db.services;

import java.nio.ByteBuffer;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vtech.xmb.grabber.db.mybb.cache.MybbUsersCache;
import vtech.xmb.grabber.db.mybb.entities.MybbUser;
import vtech.xmb.grabber.db.mybb.entities.MybbUserFields;
import vtech.xmb.grabber.db.mybb.repositories.MybbUserFieldsRepository;
import vtech.xmb.grabber.db.mybb.repositories.MybbUsersRepository;
import vtech.xmb.grabber.db.xmb.entities.XmbMember;
import vtech.xmb.grabber.db.xmb.repositories.XmbMembersRepository;

@Service
public class MigrateUsers {

  @Autowired
  private XmbMembersRepository xmbMembersRepository;
  @Autowired
  private MybbUsersRepository mybbUsersRepository;
  @Autowired
  private MybbUserFieldsRepository mybbUserFieldsRepository;
  @Autowired
  private MybbUsersCache mybbUsersCache;

  public void migrateUsers() {

    List<XmbMember> xmbMembers = (List<XmbMember>) xmbMembersRepository.findAll();

    for (XmbMember xmbMember : xmbMembers) {
      MybbUser mybbUser = new MybbUser();

      mybbUser.applyDefaults();
      updateStatus(mybbUser, xmbMember.status);
      mybbUser.aim = xmbMember.aim;
      mybbUser.avatar = xmbMember.avatar;
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

      MybbUser savedUser = mybbUsersRepository.save(mybbUser);

      if (isNullOrEmpty(xmbMember.bio) && isNullOrEmpty(xmbMember.location)) {
        continue;
      }

      MybbUserFields mybbUserFields = new MybbUserFields();
      mybbUserFields.ufid = savedUser.uid;
      mybbUserFields.location = xmbMember.location;
      mybbUserFields.bio = xmbMember.bio;
      mybbUserFields.sex = "Undisclosed";

      mybbUserFieldsRepository.save(mybbUserFields);
    }

    mybbUsersCache.evictCache();
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

    try {
      DateTimeFormatter xmbFormatter = DateTimeFormat.forPattern("yyyy-MM-dd");
      DateTimeFormatter mybbFormatter = DateTimeFormat.forPattern("dd-M-yyyy");

      DateTime dt = xmbFormatter.parseDateTime(xmbBirthDay.trim());

      return dt.toString(mybbFormatter);
    } catch (IllegalArgumentException ex) {
      System.out.println(String.format("Can not convert XMB birthday of %s to MyBB", xmbBirthDay));
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
      System.out
          .println(String
              .format(
                  "XMB user with id %s and username %s has an incorrect registration IPv6 address with value %s. Will migrate a 0.0.0.0 as registration ip for that user.",
                  xmbMember.uid, xmbMember.username, xmbMember.regip));
      return result;
    }

    if (regIp.contains(",")) {
      final int index = regIp.indexOf(",");
      final String truncatedRegIp = regIp.substring(0, index);
      System.out.println(String.format(
          "XMB user with id %s and username %s has an incorrect registration IP address with value %s. It will be truncated to %s", xmbMember.uid,
          xmbMember.username, regIp, truncatedRegIp));

      regIp = truncatedRegIp;
    }

    if (regIp.equals("unknown")) {
      System.out.println(String.format(
          "XMB user with id %s and username %s has an unknown registration IP address. Will migrate a 0.0.0.0 as registration ip for that user.",
          xmbMember.uid, xmbMember.username));
      return result;
    }

    String[] splits = regIp.split("\\.");

    if (splits.length != 4) {
      System.out.println(String.format("XMB user with id %s and username %s has an incorrect IPv6 address with value %s.", xmbMember.uid, xmbMember.username,
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
}
