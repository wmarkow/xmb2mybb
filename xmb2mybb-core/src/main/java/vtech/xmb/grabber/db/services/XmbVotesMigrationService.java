package vtech.xmb.grabber.db.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vtech.xmb.grabber.db.mybb.entities.MybbPoll;
import vtech.xmb.grabber.db.mybb.entities.MybbUser;
import vtech.xmb.grabber.db.mybb.repositories.MybbPollsRepository;
import vtech.xmb.grabber.db.mybb.repositories.MybbUsersRepository;
import vtech.xmb.grabber.db.xmb.entities.XmbVoteDesc;
import vtech.xmb.grabber.db.xmb.entities.XmbVoteResult;
import vtech.xmb.grabber.db.xmb.repositories.XmbMembersRepository;
import vtech.xmb.grabber.db.xmb.repositories.XmbVoteDescRepository;
import vtech.xmb.grabber.db.xmb.repositories.XmbVoteResultRepository;

@Service
public class XmbVotesMigrationService {

  @Autowired
  private XmbMembersRepository xmbMembersRepository;
  @Autowired
  private MybbUsersRepository mybbUsersRepository;

  @Autowired
  private XmbVoteDescRepository xmbVoteDescRepository;
  @Autowired
  private XmbVoteResultRepository xmbVoteResultRepository;

  @Autowired
  private MybbPollsRepository mybbPollsRepository;

  public void migrateVotes() {
    List<XmbVoteDesc> xmbVoteDescs = (List<XmbVoteDesc>) xmbVoteDescRepository.findAll();
    System.out.println(String.format("Znaleziono %s ankiet", xmbVoteDescs.size()));

    for (XmbVoteDesc xmbVoteDesc : xmbVoteDescs) {
      List<XmbVoteResult> xmbVoteResults = xmbVoteResultRepository.findByVoteId(xmbVoteDesc.getVoteId());

      MybbPoll poll = mybbPollsRepository.findByQuestion(xmbVoteDesc.getVoteText());
      if (poll == null) {
        System.out.println(String.format("Nie znaleziono mybb poll o tytule %s", xmbVoteDesc.getVoteText()));
      } else {
        poll.update(xmbVoteResults);
        mybbPollsRepository.save(poll);
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
