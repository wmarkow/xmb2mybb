package vtech.xmb.grabber.db.xmb.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "xmb_u2u")
public class XmbU2U {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "u2uid")
  private Long uid;

  @Column(name = "type")
  private String type;

  @OneToOne
  @JoinColumn(name = "msgfrom", referencedColumnName = "username")
  private XmbMember sender;

  @OneToOne
  @JoinColumn(name = "msgto", referencedColumnName = "username")
  private XmbMember recipient;

  @Column(name = "subject")
  private String subject;

  @Column(name = "message")
  private String message;

  @Column(name = "dateline")
  private Long dateline;

  public Long getUid() {
    return uid;
  }

  public String getType() {
    return type;
  }

  public XmbMember getSender() {
    return sender;
  }

  public XmbMember getRecipient() {
    return recipient;
  }

  public String getSubject() {
    return subject;
  }

  public String getMessage() {
    return message;
  }

  public Long getDateline() {
    return dateline;
  }
}
