package vtech.xmb.grabber.db.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vtech.xmb.grabber.db.mybb.entities.MybbPoll;
import vtech.xmb.grabber.db.mybb.entities.MybbPollVote;
import vtech.xmb.grabber.db.mybb.entities.MybbThread;
import vtech.xmb.grabber.db.mybb.repositories.MybbPollVotesRepository;
import vtech.xmb.grabber.db.mybb.repositories.MybbPollsRepository;
import vtech.xmb.grabber.db.mybb.repositories.MybbThreadsRepository;
import vtech.xmb.grabber.db.xmb.entities.XmbVoteDesc;
import vtech.xmb.grabber.db.xmb.entities.XmbVoteResult;
import vtech.xmb.grabber.db.xmb.repositories.XmbVoteDescRepository;
import vtech.xmb.grabber.db.xmb.repositories.XmbVoteResultRepository;

@Service
public class MigratePolls {

  @Autowired
  private XmbVoteDescRepository xmbVoteDescRepository;
  @Autowired
  private XmbVoteResultRepository xmbVoteResultRepository;
  @Autowired
  private MybbThreadsRepository mybbThreadsRepository;

  @Autowired
  private MybbPollsRepository mybbPollsRepository;
  @Autowired
  private MybbPollVotesRepository mybbPollVotesRepository;

  public void migratePolls() {
    List<XmbVoteDesc> xmbVoteDescs = (List<XmbVoteDesc>) xmbVoteDescRepository.findAll();
    System.out.println(String.format("Znaleziono %s ankiet", xmbVoteDescs.size()));

    List<MybbThread> mybbThreads = (List<MybbThread>) mybbThreadsRepository.findAll();

    for (XmbVoteDesc xmbVoteDesc : xmbVoteDescs) {
      List<XmbVoteResult> xmbVoteResults = xmbVoteResultRepository.findByVoteId(xmbVoteDesc.voteId);

      if (xmbVoteResults.size() == 0) {
        System.out.println(String.format("A vote with empty options voteId=%s, voteText=%s. This poll will not be migrated.", xmbVoteDesc.voteId,
            xmbVoteDesc.voteText));
        continue;
      }

      MybbPoll poll = new MybbPoll();
      update(poll, xmbVoteResults);
      final Long threadId = findThreadId(mybbThreads, xmbVoteDesc);

      if (threadId == null) {
        continue;
      }
      poll.tid = findThreadId(mybbThreads, xmbVoteDesc);
      poll.question = xmbVoteDesc.voteText;
      MybbPoll savedPoll = mybbPollsRepository.save(poll);

      // prepare the poll votes
      List<MybbPollVote> mybbPollVotes = preparePollVotes(savedPoll, xmbVoteResults);
      mybbPollVotesRepository.save(mybbPollVotes);

      // update the correct thread with a poll id
      MybbThread mybbThreadWithPoll = mybbThreadsRepository.findOne(threadId);
      mybbThreadWithPoll.poll = savedPoll.pid;
      mybbThreadsRepository.save(mybbThreadWithPoll);
    }
  }

  private void update(MybbPoll poll, List<XmbVoteResult> xmbVoteResults) {
    Map<Long, String> options = new HashMap<Long, String>();
    // Map<Long, Integer> votes = new HashMap<Long, Integer>();
    List<Long> keys = new ArrayList<Long>();

    for (XmbVoteResult xmbVoteResult : xmbVoteResults) {
      keys.add(xmbVoteResult.voteOptionId);
      options.put(xmbVoteResult.voteOptionId, xmbVoteResult.voteOptionText);
      // votes.put(xmbVoteResult.getVoteOptionId(),
      // xmbVoteResult.getVoteResult());
    }

    Collections.sort(keys);

    StringBuilder optionsBuilder = new StringBuilder();
    // StringBuilder resultsBuilder = new StringBuilder();
    int numVotes = 0;
    for (int q = 0; q < keys.size(); q++) {
      final long key = keys.get(q);

      optionsBuilder.append(options.get(key));
      // resultsBuilder.append(votes.get(key));
      // numVotes += votes.get(key);

      if (q != keys.size() - 1) {
        optionsBuilder.append("||~|~||");
        // resultsBuilder.append("||~|~||");
      }
    }

    poll.options = optionsBuilder.toString();
    poll.votes = "";// resultsBuilder.toString();
    poll.closed = 1;
    poll.numoptions = options.size();
    poll.numvotes = numVotes;
  }

  private Long findThreadId(List<MybbThread> mybbThreads, XmbVoteDesc xmbVoteDesc) {
    for (MybbThread mybbThread : mybbThreads) {
      if (mybbThread.xmbtid == null) {
        continue;
      }

      if (mybbThread.xmbtid.equals(xmbVoteDesc.topicId)) {
        return mybbThread.tid;
      }
    }

    System.out.println(String.format("Can not find a thread for XMB Vote Desc with voteId=%s tid=%s, voteText=%s", xmbVoteDesc.voteId, xmbVoteDesc.topicId,
        xmbVoteDesc.voteText));

    return null;
  }

  private List<MybbPollVote> preparePollVotes(MybbPoll savedPoll, List<XmbVoteResult> xmbVoteResults) {
    List<MybbPollVote> result = new ArrayList<MybbPollVote>();

    for (XmbVoteResult xmbVoteResult : xmbVoteResults) {
      for (int q = 0; q < xmbVoteResult.voteResult; q++) {
        MybbPollVote mybbPollVote = new MybbPollVote();
        mybbPollVote.uid = 0L;
        mybbPollVote.dateline = 0L;
        mybbPollVote.pid = savedPoll.pid;
        mybbPollVote.voteoption = xmbVoteResult.voteOptionId;

        result.add(mybbPollVote);
      }
    }

    return result;
  }
}
