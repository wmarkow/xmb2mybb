package vtech.xmb.grabber.db.mybb.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "mybb_threads")
public class MybbThread implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "tid")
  public Long tid;

  @Column(name = "fid")
  public Long fid;

  @Column(name = "subject")
  public String subject;

  @Column(name = "uid")
  public Long uid;
  
  @Column(name = "views")
  public int views;

  @Column(name = "username")
  public String username;

  @Column(name = "closed")
  public int closed;

  @Column(name = "sticky")
  public int sticky;

  @Column(name = "xmb_tid")
  public Long xmbtid;

  @Column(name = "xmb_fid")
  public Long xmbfid;

  @Column(name = "visible")
  public int visible;
  
  @Column(name = "poll")
  public long poll;
  
  @Column(name = "notes")
  public String notes;
}
