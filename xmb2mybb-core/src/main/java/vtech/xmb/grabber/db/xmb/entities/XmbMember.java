package vtech.xmb.grabber.db.xmb.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "xmb_members")
public class XmbMember implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "uid")
  public Long uid;

  @Column(name = "username", nullable = false, unique = false)
  public String username;
  
  @Column(name = "password", nullable = false, unique = false)
  public String password;
  
  @Column(name = "regdate", nullable = false, unique = false)
  public long regdate;
  
  @Column(name = "lastvisit", nullable = false, unique = false)
  public long lastvisit;
  
  @Column(name = "email", nullable = false, unique = false)
  public String email;
  
  @Column(name = "site", nullable = false, unique = false)
  public String site;
  
  @Column(name = "aim", nullable = false, unique = false)
  public String aim;
  
  @Column(name = "status", nullable = false, unique = false)
  public String status;
  
  @Column(name = "location", nullable = false, unique = false)
  public String location;
  
  @Column(name = "bio", nullable = false, unique = false)
  public String bio;
  
  @Column(name = "sig", nullable = false, unique = false)
  public String sig;
  
  @Column(name = "icq", nullable = false, unique = false)
  public String icq;
  
  @Column(name = "avatar", nullable = false, unique = false)
  public String avatar;
  
  @Column(name = "yahoo", nullable = false, unique = false)
  public String yahoo;
  
  @Column(name = "customstatus", nullable = false, unique = false)
  public String customstatus;

  @Column(name = "bday", nullable = false, unique = false)
  public String bday;
  
  @Column(name = "regip", nullable = false, unique = false)
  public String regip;
  
  @Column(name = "mood", nullable = false, unique = false)
  public String mood;
  
  @Column(name = "showemail")
  public String showemail;
  
  @Column(name = "invisible", nullable = false, unique = false)
  public String invisible;
  
  @Column(name = "tpp", nullable = true, unique = false)
  public int tpp;

  @Column(name = "ppp", nullable = true, unique = false)
  public int ppp;
}
