package vtech.xmb.grabber.db.mybb.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "mybb_smilies")
public class MybbSmiley implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "sid")
  public Long sid;

  @Column(name = "name")
  public String name;

  @Column(name = "find")
  public String find;

  @Column(name = "image")
  public String image;

  @Column(name = "disporder")
  public int disporder;

  @Column(name = "showclickable")
  public int showclickable;
}
