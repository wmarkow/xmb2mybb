package vtech.xmb.grabber.db.mybb.entities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import vtech.xmb.grabber.db.xmb.entities.XmbVoteResult;

@Entity
@Table(name = "mybb_polls")
public class MybbPoll {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "pid")
  public Long pid;

  @Column(name = "tid")
  public Long tid;

  @Column(name = "question")
  public String question;

  @Column(name = "options")
  public String options;

  @Column(name = "votes")
  public String votes;

  @Column(name = "numoptions")
  public int numoptions;

  @Column(name = "numvotes")
  public int numvotes;

  @Column(name = "closed")
  public int closed;
}
