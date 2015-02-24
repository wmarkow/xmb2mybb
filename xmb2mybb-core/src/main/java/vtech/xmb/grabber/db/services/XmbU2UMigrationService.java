package vtech.xmb.grabber.db.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vtech.xmb.grabber.db.mybb.entities.MybbPrivateMessage;
import vtech.xmb.grabber.db.mybb.entities.MybbUser;
import vtech.xmb.grabber.db.mybb.repositories.MybbPrivateMessagesRepository;
import vtech.xmb.grabber.db.mybb.repositories.MybbUsersRepository;
import vtech.xmb.grabber.db.xmb.entities.XmbMember;
import vtech.xmb.grabber.db.xmb.entities.XmbU2U;
import vtech.xmb.grabber.db.xmb.repositories.XmbMembersRepository;
import vtech.xmb.grabber.db.xmb.repositories.XmbU2URepository;

@Service
public class XmbU2UMigrationService {

  @Autowired
  private XmbU2URepository xmbU2uRepository;
  @Autowired
  private XmbMembersRepository xmbMembersRepository;

  @Autowired
  private MybbUsersRepository mybbUsersRepository;
  @Autowired
  private MybbPrivateMessagesRepository mybbPrivateMessagesRepository;

  public void migrateOutgoingU2u() {
    List<XmbU2U> xmbOutgoings = xmbU2uRepository.findAllByType("outgoing");
    System.out.println(String.format("Znaleiozno %s wychodzących wiadmości w bazie XMB", xmbOutgoings.size()));
    List<XmbMember> xmbMembers = (List<XmbMember>) xmbMembersRepository.findAll();

    List<MybbUser> mybbUsers = (List<MybbUser>) mybbUsersRepository.findAll();
    System.out.println(String.format("Znaleziono %s użytkowników w bazie MyBB", mybbUsers.size()));

    for (XmbU2U xmbOutgoing : xmbOutgoings) {
      if (xmbOutgoing.getSender() == null) {
        continue;
      }

      if (xmbOutgoing.getSender().username.equals("witas")) {
        MybbUser mybbSender = findUser(mybbUsers, xmbOutgoing.getSender().username);

        MybbPrivateMessage mybbMessage = new MybbPrivateMessage();
        mybbMessage.setDateline(xmbOutgoing.getDateline());
        mybbMessage.setFolderToOutgoing();
        mybbMessage.setMessage(xmbOutgoing.getMessage());
        mybbMessage.setSenderId(mybbSender.uid);
        mybbMessage.setSubject(xmbOutgoing.getSubject());

        if (xmbOutgoing.getRecipient() == null) {
          mybbMessage.setRecipientId(0);
        } else {
          MybbUser mybbRecipient = findUser(mybbUsers, xmbOutgoing.getRecipient().username);

          mybbMessage.setRecipientId(mybbRecipient.uid);
        }
        mybbPrivateMessagesRepository.save(mybbMessage);
      }
    }
  }

  private MybbUser findUser(List<MybbUser> mybbUsers, String username) {
    for (MybbUser mybbUser : mybbUsers) {
      if (mybbUser.username.equals(username)) {
        return mybbUser;
      }
    }

    throw new RuntimeException(String.format("Nie można znależć MybbUser dla username = %s", username));
  }
}
