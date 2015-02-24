package vtech.xmb.grabber.db.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vtech.xmb.grabber.db.mybb.entities.MybbUser;
import vtech.xmb.grabber.db.mybb.repositories.MybbUsersRepository;
import vtech.xmb.grabber.db.xmb.entities.XmbMember;
import vtech.xmb.grabber.db.xmb.repositories.XmbMembersRepository;

@Service
public class MigrateUsers {

  @Autowired
  private XmbMembersRepository xmbMembersRepository;

  @Autowired
  private MybbUsersRepository mybbUsersRepository;

  public void migrateUsers() {

    List<XmbMember> xmbMembers = (List<XmbMember>) xmbMembersRepository.findAll();

    for (XmbMember xmbMember : xmbMembers) {
      MybbUser mybbUser = new MybbUser();

      mybbUser.applyDefaults();
      updateStatus(mybbUser, xmbMember.status);
      mybbUser.aim = xmbMember.aim;
      mybbUser.avatar = xmbMember.avatar;
      mybbUser.birthday = xmbMember.bday;
      mybbUser.email = xmbMember.email;
      if (xmbMember.icq.length() < 10) {
        mybbUser.icq = xmbMember.icq;
      } else {
        mybbUser.icq = "";
      }
      mybbUser.password = xmbMember.password;
      mybbUser.regdate = xmbMember.regdate;
      mybbUser.lastvisit = xmbMember.lastvisit;
      mybbUser.signature = xmbMember.sig;
      mybbUser.username = xmbMember.username;
      mybbUser.website = xmbMember.site;
      mybbUser.xmbUid = xmbMember.uid;
      mybbUser.yahoo = xmbMember.yahoo;
      mybbUser.usertitle = xmbMember.customstatus;

      mybbUsersRepository.save(mybbUser);
    }
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
}
