package vtech.xmb.grabber.db.mybb.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "mybb_userfields")
public class MybbUserFields {
  @Id
  @Column(name = "ufid")
  public Long ufid;

  @Column(name = "fid1")
  public String location;

  @Column(name = "fid2")
  public String bio;
  
  @Column(name = "fid3")
  public String sex;
}
