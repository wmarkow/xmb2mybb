package vtech.xmb.grabber.db.services;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import vtech.xmb.grabber.db.cache.MybbUsersCache;
import vtech.xmb.grabber.db.domain.ProgressCalculator;
import vtech.xmb.grabber.db.mybb.entities.MybbPrivateMessage;
import vtech.xmb.grabber.db.mybb.entities.MybbUser;
import vtech.xmb.grabber.db.mybb.repositories.MybbPrivateMessagesRepository;
import vtech.xmb.grabber.db.xmb.entities.XmbU2U;
import vtech.xmb.grabber.db.xmb.repositories.XmbU2URepository;

@Service
public class MigratePrivateMessages {
  private final static Logger LOGGER = Logger.getLogger(MigratePrivateMessages.class);
  private final static Logger ROOT_LOGGER = Logger.getRootLogger();

  @Autowired
  private XmbU2URepository xmbU2uRepository;
  @Autowired
  private MybbUsersCache mybbUsersCache;
  @Autowired
  private MybbPrivateMessagesRepository mybbPrivateMessagesRepository;

  public void migrateU2Us() {
    LOGGER.info("Private messages migration started.");
    ROOT_LOGGER.info("Private messages migration started.");

    final long xmbCount = xmbU2uRepository.count();
    ProgressCalculator progressCalc = new ProgressCalculator(xmbCount);
    LOGGER.info(String.format("Found %s private messages to migrate from XMB.", xmbCount));
    ROOT_LOGGER.info(String.format("Found %s private messages to migrate from XMB.", xmbCount));

    final int pageSize = 1000;
    int pageNumber = 0;
    boolean shouldContinue = true;

    Pageable pageRequest = new PageRequest(pageNumber, pageSize);

    while (shouldContinue) {
      Page<XmbU2U> xmbU2uPage = (Page<XmbU2U>) xmbU2uRepository.findAll(pageRequest);
      List<XmbU2U> xmbU2Us = xmbU2uPage.getContent();

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

      progressCalc.hit(xmbU2Us.size());
      progressCalc.logProgress(1, LOGGER, ROOT_LOGGER);
    }

    final long mybbCount = mybbPrivateMessagesRepository.count();
    final long notMigrated = xmbCount - mybbCount;
    LOGGER.info(String.format("Found %s private messages in MyBB after migration. %s posts not migrated.", mybbCount, notMigrated));
    ROOT_LOGGER.info(String.format("Found %s private messages in MyBB after migration. %s posts not migrated.", mybbCount, notMigrated));
    LOGGER.info("Private messages migration finished.");
    ROOT_LOGGER.info("Private messages migration finished.");
  }
}
