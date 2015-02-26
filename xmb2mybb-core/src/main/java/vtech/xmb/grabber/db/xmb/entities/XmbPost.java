package vtech.xmb.grabber.db.xmb.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "xmb_posts")
public class XmbPost implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "pid")
  public Long pid;

  @Column(name = "fid")
  public Long fid;

  @Column(name = "tid")
  public Long tid;

  @Column(name = "author")
  public String author;

  @Column(name = "message")
  public String message;

  @Column(name = "subject")
  public String subject;

  @Column(name = "dateline")
  public long dateline;
}
