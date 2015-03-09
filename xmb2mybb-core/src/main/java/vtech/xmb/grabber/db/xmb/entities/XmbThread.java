package vtech.xmb.grabber.db.xmb.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "xmb_threads")
public class XmbThread implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "tid")
  public Long tid;

  @Column(name = "fid")
  public Long fid;
  
  @Column(name = "views")
  public int views;

  @Column(name = "subject")
  public String subject;

  @Column(name = "author")
  public String author;

  @Column(name = "closed")
  public String closed;

  @Column(name = "topped")
  public int topped;

  @Column(name = "pollopts")
  public int pollopts;
}
