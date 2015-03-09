package vtech.xmb.grabber.db.xmb.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "xmb_attachments")
public class XmbAttachment {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "aid")
  public Long aid;

  @Column(name = "pid")
  public Long pid;
  
  @Column(name = "parentid")
  public Long parentid;

  @Column(name = "filename")
  public String filename;

  @Column(name = "filetype")
  public String filetype;

  @Column(name = "filesize")
  public Integer filesize;

  @Column(name = "attachment")
  public byte[] attachment;

  @Column(name = "downloads")
  public int downloads;

  @Column(name = "subdir")
  public String subdir;
}
