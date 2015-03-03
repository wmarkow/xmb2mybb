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
  private String buddylist;
  
  @Column(name = "usergroup", nullable = false, unique = false)
  public int usergroup;
  
  @Column(name = "ignorelist", nullable = false, unique = false)
  private String ignorelist;
  
  @Column(name = "pmfolders", nullable = false, unique = false)
  private String pmfolders;
  
  @Column(name = "notepad", nullable = false, unique = false)
  private String notepad;
  
  @Column(name = "usernotes", nullable = false, unique = false)
  private String usernotes;
  
  public void applyDefaults(){
    buddylist = "";
    ignorelist = "";
    pmfolders = "";
    notepad = "";
    usernotes = "";
    usergroup = 2;
  }
}
