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
  public Long voteId;

  @Id
  @Column(name = "vote_option_id", nullable = false, unique = false)
  public long voteOptionId;

  @Column(name = "vote_option_text", nullable = false, unique = false)
  public String voteOptionText;

  @Column(name = "vote_result", nullable = false, unique = false)
  public int voteResult;
}
