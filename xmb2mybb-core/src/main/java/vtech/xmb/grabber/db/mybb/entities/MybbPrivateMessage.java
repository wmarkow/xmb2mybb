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
  private String subject;

  @Column(name = "message")
  private String message;

  @Column(name = "dateline")
  private Long dateline;

  @Column(name = "folder")
  private int folder;
  
  @Column(name = "recipients")
  private String recipients;
  
  @Column(name = "status")
  private int status;

  public void setFolderToOutgoing() {
    this.folder = 2 ;
    this.status = 1;
  }

  public void setSenderId(long id) {
    this.uid = id;
    this.fromid = id;
  }

  public void setRecipientId(long id) {
    this.toid = id; 
    this.recipients = "a:1:{s:2:\"to\";a:1:{i:0;s:4:\"" + id + "\";}}";
  }

  public void setSubject(String subject) {
    this.subject = subject;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public void setDateline(Long dateline) {
    this.dateline = dateline;
  }
}
