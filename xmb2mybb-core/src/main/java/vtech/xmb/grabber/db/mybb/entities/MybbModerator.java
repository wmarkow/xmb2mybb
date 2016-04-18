package vtech.xmb.grabber.db.mybb.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "mybb_moderators")
public class MybbModerator {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "mid")
  public Long mid;

  @Column(name = "fid")
  public Long fid;

  @Column(name = "id")
  public Long uid;

  @Column(name = "caneditposts")
  public int caneditposts;
  
  @Column(name = "cansoftdeleteposts")
  public int cansoftdeleteposts;
  
  @Column(name = "canrestoreposts")
  public int canrestoreposts;
  
  @Column(name = "candeleteposts")
  public int candeleteposts;
  
  @Column(name = "cansoftdeletethreads")
  public int cansoftdeletethreads;
  
  @Column(name = "canrestorethreads")
  public int canrestorethreads;
  
  @Column(name = "candeletethreads")
  public int candeletethreads;
  
  @Column(name = "canviewips")
  public int canviewips;
  
  @Column(name = "canviewunapprove")
  public int canviewunapprove;
  
  @Column(name = "canviewdeleted")
  public int canviewdeleted;
  
  @Column(name = "canopenclosethreads")
  public int canopenclosethreads;
  
  @Column(name = "canstickunstickthreads")
  public int canstickunstickthreads;
  
  @Column(name = "canapproveunapprovethreads")
  public int canapproveunapprovethreads;
  
  @Column(name = "canapproveunapproveposts")
  public int canapproveunapproveposts;
  
  @Column(name = "canapproveunapproveattachs")
  public int canapproveunapproveattachs;
  
  @Column(name = "canmanagethreads")
  public int canmanagethreads;
  
  @Column(name = "canmanagepolls")
  public int canmanagepolls;
  
  @Column(name = "canpostclosedthreads")
  public int canpostclosedthreads;
  
  @Column(name = "canmovetononmodforum")
  public int canmovetononmodforum;
  
  @Column(name = "canusecustomtools")
  public int canusecustomtools;
  
  @Column(name = "")
  public int canmanageannouncements;
  
  @Column(name = "canmanagereportedposts")
  public int canmanagereportedposts;
  
  @Column(name = "canviewmodlog")
  public int canviewmodlog;
}
