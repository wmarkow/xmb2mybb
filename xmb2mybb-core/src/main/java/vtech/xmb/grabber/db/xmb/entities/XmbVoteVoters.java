package vtech.xmb.grabber.db.xmb.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "xmb_vote_voters")
public class XmbVoteVoters {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "vote_id", insertable = false, updatable = false)
  private Long voteId;

  @Column(name = "vote_user_id", nullable = false, unique = false)
  private long voteUserId;

  public long getVoteUserId() {
    return voteUserId;
  }
}
