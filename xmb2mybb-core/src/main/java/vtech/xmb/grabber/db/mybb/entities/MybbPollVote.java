package vtech.xmb.grabber.db.mybb.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "mybb_pollvotes")
public class MybbPollVote {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "vid")
  public Long vid;

  @Column(name = "pid")
  public Long pid;

  @Column(name = "uid")
  public Long uid;

  @Column(name = "voteoption")
  public Long voteoption;

  @Column(name = "dateline")
  public Long dateline;
}
