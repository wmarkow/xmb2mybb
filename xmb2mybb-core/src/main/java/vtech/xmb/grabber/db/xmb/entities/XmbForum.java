package vtech.xmb.grabber.db.xmb.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "xmb_forums")
public class XmbForum implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "fid")
  public Long fid;

  @Column(name = "type")
  public String type;

  @Column(name = "name")
  public String name;

  @Column(name = "description")
  public String description;

  @Column(name = "status")
  public String status;

  @Column(name = "displayorder")
  public int displayorder;

  @Column(name = "moderator")
  private String moderator;

  @Column(name = "password")
  public String password;

  @Column(name = "fup")
  public long fup;

  public String[] getModerators() {
    if (moderator == null) {
      return new String[] {};
    }
    String[] splits = moderator.split(",");

    List<String> result = new ArrayList<String>();
    for (String split : splits) {
      if (!split.trim().isEmpty()) {
        result.add(split.trim());
      }
    }

    return result.toArray(new String[] {});
  }
}
