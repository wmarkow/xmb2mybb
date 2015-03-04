package vtech.xmb.grabber.db.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import vtech.xmb.grabber.db.cache.MybbUsersCache;
import vtech.xmb.grabber.db.mybb.entities.MybbPrivateMessage;
import vtech.xmb.grabber.db.mybb.entities.MybbUser;
import vtech.xmb.grabber.db.mybb.repositories.MybbPrivateMessagesRepository;
import vtech.xmb.grabber.db.xmb.entities.XmbU2U;
import vtech.xmb.grabber.db.xmb.repositories.XmbU2URepository;

@Service
public class MigratePrivateMessages {

  @Autowired
  private XmbU2URepository xmbU2uRepository;
  @Autowired
  private MybbUsersCache mybbUsersCache;
  @Autowired
  private MybbPrivateMessagesRepository mybbPrivateMessagesRepository;

  public void migrateU2Us() {
    final int pageSize = 1000;
    int pageNumber = 0;
    boolean shouldContinue = true;

    Pageable pageRequest = new PageRequest(pageNumber, pageSize);

    while (shouldContinue) {
      Page<XmbU2U> xmbU2uPage = (Page<XmbU2U>) xmbU2uRepository.findAll(pageRequest);
      List<XmbU2U> xmbU2Us = xmbU2uPage.getContent();

      System.out.println(String.format("Migrating U2Us package number %s", pageRequest.getPageNumber()));

      if (xmbU2Us.size() == 0) {
        shouldContinue = false;
        break;
      }

      for (XmbU2U xmbU2u : xmbU2Us) {
        MybbUser mybbSender = mybbUsersCache.findUserByName(xmbU2u.sender);
        MybbUser mybbRecipient = mybbUsersCache.findUserByName(xmbU2u.recipient);
        MybbUser mybbOwner = mybbUsersCache.findUserByName(xmbU2u.owner);

        if (mybbOwner == null) {
          continue;
        }

        MybbPrivateMessage mybbMessage = new MybbPrivateMessage();
        mybbMessage.dateline = xmbU2u.dateline;
        mybbMessage.message = xmbU2u.message;
        mybbMessage.subject = xmbU2u.subject;

        if ("outgoing".equals(xmbU2u.type)) {
          mybbMessage.setAsOutgoing(mybbSender, mybbRecipient);
        }
        if ("incoming".equals(xmbU2u.type)) {
          mybbMessage.setAsIncoming(mybbSender, mybbRecipient);
        }
        if ("draft".equals(xmbU2u.type)) {
          mybbMessage.setAsDraft(mybbOwner);
        }

        mybbPrivateMessagesRepository.save(mybbMessage);
      }

      pageRequest = pageRequest.next();
    }
  }
}
