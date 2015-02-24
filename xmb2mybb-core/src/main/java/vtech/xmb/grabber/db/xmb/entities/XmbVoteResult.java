package vtech.xmb.grabber.db.xmb.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

@Entity
@Table(name = "xmb_vote_results")
@IdClass(XmbVoteResult.class)
public class XmbVoteResult implements Serializable {

  @Id
  @Column(name = "vote_id", insertable = false, updatable = false)
  private Long voteId;

  @Id
  @Column(name = "vote_option_id", nullable = false, unique = false)
  private long voteOptionId;

  @Column(name = "vote_option_text", nullable = false, unique = false)
  private String voteOptionText;

  @Column(name = "vote_result", nullable = false, unique = false)
  private int voteResult;

  public Long getVoteId() {
    return voteId;
  }

  public long getVoteOptionId() {
    return voteOptionId;
  }

  public String getVoteOptionText() {
    return voteOptionText;
  }

  public int getVoteResult() {
    return voteResult;
  }
}
