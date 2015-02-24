package vtech.xmb.grabber.db.xmb.entities;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "xmb_vote_desc")
public class XmbVoteDesc implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "vote_id")
  private Long voteId;

  @Column(name = "topic_id", nullable = false, unique = false)
  private long topicId;

  @Column(name = "vote_text", nullable = false, unique = false)
  private String voteText;

  public Long getVoteId() {
    return voteId;
  }

  public long getTopicId() {
    return topicId;
  }

  public String getVoteText() {
    return voteText;
  }
}
