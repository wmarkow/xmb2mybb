package vtech.xmb.grabber.db.xmb.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "xmb_u2u")
public class XmbU2U {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "u2uid")
  public Long uid;

  @Column(name = "type")
  public String type;

  @Column(name = "msgfrom")
  public String sender;

  @Column(name = "msgto")
  public String recipient;
  
  @Column(name = "owner")
  public String owner;

  @Column(name = "subject")
  public String subject;

  @Column(name = "message")
  public String message;

  @Column(name = "dateline")
  public Long dateline;
}
