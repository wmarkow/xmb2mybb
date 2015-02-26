package vtech.xmb.grabber.db.mybb.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "mybb_posts")
public class MybbPost implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "pid")
  public Long pid;

  @Column(name = "tid")
  public Long tid;

  @Column(name = "fid")
  public Long fid;

  @Column(name = "subject")
  public String subject;

  @Column(name = "uid")
  public Long uid;

  @Column(name = "username")
  public String username;

  @Column(name = "dateline")
  public long dateline;

  @Column(name = "message")
  public String message;

  @Column(name = "xmb_pid")
  public Long xmbpid;

  @Column(name = "xmb_tid")
  public Long xmbtid;

  @Column(name = "xmb_fid")
  public Long xmbfid;

  @Column(name = "visible")
  public int visible;
}
