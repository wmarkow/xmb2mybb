package vtech.xmb.grabber.db.mybb.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "mybb_users")
public class MybbUser {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "uid")
  public Long uid;

  @Column(name = "username", nullable = false, unique = false)
  public String username;

  @Column(name = "password", nullable = false, unique = false)
  public String password;

  @Column(name = "email", nullable = false, unique = false)
  public String email;

  @Column(name = "avatar", nullable = false, unique = false)
  public String avatar;

  @Column(name = "regdate", nullable = false, unique = false)
  public long regdate;

  @Column(name = "lastvisit", nullable = false, unique = false)
  public long lastvisit;

  @Column(name = "lastactive", nullable = false, unique = false)
  public long lastactive;

  @Column(name = "website", nullable = false, unique = false)
  public String website;

  @Column(name = "icq", nullable = false, unique = false)
  public String icq;

  @Column(name = "aim", nullable = false, unique = false)
  public String aim;

  @Column(name = "yahoo", nullable = false, unique = false)
  public String yahoo;

  @Column(name = "birthday", nullable = false, unique = false)
  public String birthday;

  @Column(name = "signature", nullable = false, unique = false)
  public String signature;

  @Column(name = "usertitle", nullable = false, unique = false)
  public String usertitle;

  @Column(name = "xmb_uid")
  public Long xmbUid;

  @Column(name = "buddylist", nullable = false, unique = false)
  public String buddylist;

  @Column(name = "usergroup", nullable = false, unique = false)
  public int usergroup;

  @Column(name = "ignorelist", nullable = false, unique = false)
  public String ignorelist;

  @Column(name = "pmfolders", nullable = false, unique = false)
  public String pmfolders;

  @Column(name = "notepad", nullable = false, unique = false)
  public String notepad;

  @Column(name = "usernotes", nullable = false, unique = false)
  public String usernotes;

  @Column(name = "regip", nullable = true, unique = false)
  public byte[] regip;

  @Column(name = "allownotices", nullable = true, unique = false)
  public int allowNotices;

  @Column(name = "receivepms", nullable = true, unique = false)
  public int receivepms;

  @Column(name = "pmnotice", nullable = true, unique = false)
  public int pmnotice;

  @Column(name = "pmnotify", nullable = true, unique = false)
  public int pmnotify;

  @Column(name = "showimages", nullable = true, unique = false)
  public int showimages;

  @Column(name = "showvideos", nullable = true, unique = false)
  public int showvideos;

  @Column(name = "showsigs", nullable = true, unique = false)
  public int showsigs;

  @Column(name = "showavatars", nullable = true, unique = false)
  public int showavatars;

  @Column(name = "showquickreply", nullable = true, unique = false)
  public int showquickreply;

  @Column(name = "showredirect", nullable = true, unique = false)
  public int showredirect;

  @Column(name = "hideemail", nullable = true, unique = false)
  public int hideemail;
  
  @Column(name = "invisible", nullable = true, unique = false)
  public int invisible;
  
  @Column(name = "classicpostbit", nullable = true, unique = false)
  public int classicpostbit;

  @Column(name = "tpp", nullable = true, unique = false)
  public int tpp;

  @Column(name = "ppp", nullable = true, unique = false)
  public int ppp;
}
