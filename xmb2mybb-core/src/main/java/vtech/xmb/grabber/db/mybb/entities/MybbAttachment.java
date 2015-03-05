package vtech.xmb.grabber.db.mybb.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "mybb_attachments")
public class MybbAttachment {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "aid")
  public Long aid;

  @Column(name = "pid")
  public Long pid;

  @Column(name = "uid")
  public Long uid;

  @Column(name = "filename")
  public String filename;

  @Column(name = "filetype")
  public String filetype;

  @Column(name = "filesize")
  public Integer filesize;

  @Column(name = "attachname")
  public String attachname;

  @Column(name = "downloads")
  public int downloads;

  @Column(name = "dateuploaded")
  public Long dateuploaded;

  @Column(name = "visible")
  public int visible;

  @Column(name = "xmb_aid")
  public Long xmb_aid;
}
