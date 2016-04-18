package vtech.xmb.grabber.db.mybb.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "mybb_threadsubscriptions")
public class MybbThreadsSubscriptions {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "sid")
  public Long sid;

  @Column(name = "tid", nullable = false, unique = false)
  public long tid;

  @Column(name = "uid", nullable = false, unique = false)
  public long uid;
  
  @Column(name = "notification", nullable = false, unique = false)
  public int notification;

  @Column(name = "dateline", nullable = false, unique = false)
  public long dateline;
  
  @Column(name = "subscriptionkey", nullable = false, unique = false)
  public String subscriptionkey;
}
