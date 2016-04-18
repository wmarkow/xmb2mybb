package vtech.xmb.grabber.db.mybb.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "mybb_forums")
public class MybbForum implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "fid")
  public Long fid;

  @Column(name = "name")
  public String name;

  @Column(name = "description")
  public String description;

  @Column(name = "type")
  public String type; // c lub f

  @Column(name = "pid")
  public long pid;

  @Column(name = "parentlist")
  public String parentlist;

  @Column(name = "disporder")
  public int disporder;

  @Column(name = "active")
  public int active;

  @Column(name = "open")
  public int open;

  @Column(name = "password")
  public String password;
  
  @Column(name = "xmb_fid")
  public Long xmbfid;
  
  @Column(name = "xmb_pid")
  public Long xmbpid;

  @Column(name = "rules")
  private String rules;

  @Column(name = "allowmycode")
  private int allowmycode;

  @Column(name = "allowsmilies")
  private int allowsmilies;

  @Column(name = "allowimgcode")
  private int allowimgcode;

  @Column(name = "allowvideocode")
  private int allowvideocode;

  @Column(name = "allowpicons")
  private int allowpicons;

  @Column(name = "allowtratings")
  private int allowtratings;

  @Column(name = "usepostcounts")
  private int usepostcounts;

  @Column(name = "usethreadcounts")
  private int usethreadcounts;
  
  @Column(name = "showinjump")
  private int showinjump;

  public void setDefaults() {
    rules = "";
    allowmycode = 1;
    allowsmilies = 1;
    allowimgcode = 1;
    allowvideocode = 1;
    allowpicons = 1;
    allowtratings = 1;
    usepostcounts = 1;
    usethreadcounts = 1;
    showinjump = 1;
  }
}
