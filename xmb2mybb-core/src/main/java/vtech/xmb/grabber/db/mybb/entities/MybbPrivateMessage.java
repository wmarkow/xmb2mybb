package vtech.xmb.grabber.db.mybb.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "mybb_privatemessages")
public class MybbPrivateMessage {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "pmid")
  private Long pmid;

  @Column(name = "uid")
  private Long uid;

  @Column(name = "toid")
  private Long toid;

  @Column(name = "fromid")
  private Long fromid;

  @Column(name = "subject")
  public String subject;

  @Column(name = "message")
  public String message;

  @Column(name = "dateline")
  public Long dateline;

  @Column(name = "folder")
  private int folder;

  @Column(name = "recipients")
  private String recipients;

  @Column(name = "status")
  private int status;

  @Column(name = "receipt")
  private int receipt;

  public void setAsOutgoing(MybbUser mybbSender, MybbUser mybbRecipient) {
    long mybbSenderId = 0;
    long mybbRecipientId = 0;
    if (mybbSender != null) {
      mybbSenderId = mybbSender.uid;
    }
    if (mybbRecipient != null) {
      mybbRecipientId = mybbRecipient.uid;
    }

    this.folder = 2;
    this.status = 1;

    this.uid = mybbSenderId;
    this.fromid = mybbSenderId;

    this.toid = mybbRecipientId;
    this.recipients = "a:1:{s:2:\"to\";a:1:{i:0;s:4:\"" + mybbRecipientId + "\";}}";

    this.receipt = 0;
  }

  public void setAsIncoming(MybbUser mybbSender, MybbUser mybbRecipient) {
    long mybbSenderId = 0;
    long mybbRecipientId = 0;
    if (mybbSender != null) {
      mybbSenderId = mybbSender.uid;
    }
    if (mybbRecipient != null) {
      mybbRecipientId = mybbRecipient.uid;
    }

    this.folder = 1;
    this.status = 1;

    this.uid = mybbRecipientId;
    this.toid = mybbRecipientId;

    this.fromid = mybbSenderId;

    this.recipients = "a:1:{s:2:\"to\";a:1:{i:0;s:1:\"" + mybbRecipientId + "\";}}";

    this.receipt = 0;
  }

  public void setAsDraft(MybbUser mybbOwner) {
    long mybbOwnerId = mybbOwner.uid;

    this.folder = 3;
    this.status = 0;

    this.uid = mybbOwnerId;
    this.fromid = mybbOwnerId;

    this.toid = 0L;
    this.recipients = "a:0:{}";
    this.receipt = 1;
  }
}
