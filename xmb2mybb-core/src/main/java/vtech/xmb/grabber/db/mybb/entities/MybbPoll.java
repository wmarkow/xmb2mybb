package vtech.xmb.grabber.db.mybb.entities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import vtech.xmb.grabber.db.xmb.entities.XmbVoteResult;

@Entity
@Table(name = "mybb_polls")
public class MybbPoll {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "pid")
  private Long pid;

  @Column(name = "tid")
  private Long tid;

  @Column(name = "question")
  private String question;

  @Column(name = "options")
  private String options;

  @Column(name = "votes")
  private String votes;

  @Column(name = "numoptions")
  private int numoptions;

  @Column(name = "numvotes")
  private int numvotes;

  @Column(name = "closed")
  private int closed;

  public Long getPid() {
    return pid;
  }

  public Long getTid() {
    return tid;
  }

  public String getQuestion() {
    return question;
  }

  public String getOptions() {
    return options;
  }

  public String getVotes() {
    return votes;
  }

  public int getNumoptions() {
    return numoptions;
  }

  public int getNumvotes() {
    return numvotes;
  }

  public int getClosed() {
    return closed;
  }

  public void update(List<XmbVoteResult> xmbVoteResults) {
    Map<Long, String> options = new HashMap<Long, String>();
    Map<Long, Integer> votes = new HashMap<Long, Integer>();
    List<Long> keys = new ArrayList<Long>();

    for (XmbVoteResult xmbVoteResult : xmbVoteResults) {
      keys.add(xmbVoteResult.getVoteOptionId());
      options.put(xmbVoteResult.getVoteOptionId(), xmbVoteResult.getVoteOptionText());
      votes.put(xmbVoteResult.getVoteOptionId(), xmbVoteResult.getVoteResult());
    }

    Collections.sort(keys);

    StringBuilder optionsBuilder = new StringBuilder();
    StringBuilder resultsBuilder = new StringBuilder();
    int numVotes = 0;
    for (int q = 0; q < keys.size(); q++) {
      final long key = keys.get(q);

      optionsBuilder.append(options.get(key));
      resultsBuilder.append(votes.get(key));
      numVotes += votes.get(key);

      if (q != keys.size() - 1) {
        optionsBuilder.append("||~|~||");
        resultsBuilder.append("||~|~||");
      }
    }

    this.options = optionsBuilder.toString();
    this.votes = resultsBuilder.toString();
    this.closed = 1;
    this.numoptions = options.size();
    this.numvotes = numVotes;
  }
}
