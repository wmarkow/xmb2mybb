package vtech.xmb.grabber.db.xmb.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "xmb_smilies")
public class XmbSmiley implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "id")
  public Long id;

  @Column(name = "type")
  public String type;

  @Column(name = "code")
  public String code;

  @Column(name = "url")
  public String url;
}
