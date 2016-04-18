package vtech.xmb.grabber.db.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vtech.xmb.grabber.db.cache.MybbThreadsCache;
import vtech.xmb.grabber.db.domain.ProgressCalculator;
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
  private final static Logger LOGGER = Logger.getLogger(MigratePolls.class);
  private final static Logger ROOT_LOGGER = Logger.getRootLogger();

  @Autowired
  private XmbVoteDescRepository xmbVoteDescRepository;
  @Autowired
  private XmbVoteResultRepository xmbVoteResultRepository;
  @Autowired
  private MybbThreadsCache mybbThreadsCache;

  @Autowired
  private MybbPollsRepository mybbPollsRepository;
  @Autowired
  private MybbPollVotesRepository mybbPollVotesRepository;
  @Autowired
  private MybbThreadsRepository mybbThreadsRepository;

  public void migratePolls() {
    LOGGER.info("Polls migration started.");
    ROOT_LOGGER.info("Polls migration started.");

    List<XmbVoteDesc> xmbVoteDescs = (List<XmbVoteDesc>) xmbVoteDescRepository.findAll();

    final long xmbCount = xmbVoteDescs.size();
    LOGGER.info(String.format("Found %s polls to migrate from XMB.", xmbCount));
    ROOT_LOGGER.info(String.format("Found %s polls to migrate from XMB.", xmbCount));

    for (XmbVoteDesc xmbVoteDesc : xmbVoteDescs) {
      List<XmbVoteResult> xmbVoteResults = xmbVoteResultRepository.findByVoteId(xmbVoteDesc.voteId);

      if (xmbVoteResults.size() == 0) {
        LOGGER.warn(String
            .format("A vote with empty options voteId=%s, voteText=%s. This poll will not be migrated.", xmbVoteDesc.voteId, xmbVoteDesc.voteText));
        continue;
      }

      MybbPoll poll = new MybbPoll();
      update(poll, xmbVoteResults);
      final MybbThread mybbThread = mybbThreadsCache.findByXmbVoteDesc(xmbVoteDesc);

      if (mybbThread == null) {
        LOGGER.warn(String.format(
            "Can not find a MyBB thread that owns the poll with XMB poll_id=%s, description='%s' tid=%s. This poll will not be migrated.", xmbVoteDesc.voteId,
            xmbVoteDesc.voteText, xmbVoteDesc.topicId));
        continue;
      }
      poll.tid = mybbThread.tid;
      poll.question = xmbVoteDesc.voteText;
      MybbPoll savedPoll = mybbPollsRepository.save(poll);

      // prepare the poll votes
      List<MybbPollVote> mybbPollVotes = preparePollVotes(savedPoll, xmbVoteResults);
      mybbPollVotesRepository.save(mybbPollVotes);

      // update the correct thread with a poll id
      mybbThread.poll = savedPoll.pid;
      mybbThreadsRepository.save(mybbThread);
    }

    // evict the cache
    mybbThreadsCache.evictCache();
    
    final long mybbCount = mybbPollsRepository.count();
    final long notMigrated = xmbCount - mybbCount;
    LOGGER.info(String.format("Found %s polls in MyBB after migration. %s polls not migrated.", mybbCount, notMigrated));
    ROOT_LOGGER.info(String.format("Found %s polls in MyBB after migration. %s polls not migrated.", mybbCount, notMigrated));
    LOGGER.info("Polls migration finished.");
    ROOT_LOGGER.info("Polls migration finished.");
  }

  private void update(MybbPoll poll, List<XmbVoteResult> xmbVoteResults) {
    Map<Long, String> options = new HashMap<Long, String>();
    List<Long> keys = new ArrayList<Long>();

    for (XmbVoteResult xmbVoteResult : xmbVoteResults) {
      keys.add(xmbVoteResult.voteOptionId);
      options.put(xmbVoteResult.voteOptionId, xmbVoteResult.voteOptionText);
    }

    Collections.sort(keys);

    StringBuilder optionsBuilder = new StringBuilder();
    int numVotes = 0;
    for (int q = 0; q < keys.size(); q++) {
      final long key = keys.get(q);

      optionsBuilder.append(options.get(key));

      if (q != keys.size() - 1) {
        optionsBuilder.append("||~|~||");
      }
    }

    poll.options = optionsBuilder.toString();
    poll.votes = "";
    poll.closed = 1;
    poll.numoptions = options.size();
    poll.numvotes = numVotes;
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
