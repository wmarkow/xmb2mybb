package vtech.xmb.grabber.db.xmb.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "xmb_favorites")
public class XmbFavorites implements Serializable {

  //a hack to provide some bogus Id (as is not specified in XMB)
  @Id
  @Column(name = "tid", nullable = false, unique = false, insertable = false, updatable = false)
  private long id;
  
  @Column(name = "tid", nullable = false, unique = false, insertable = false, updatable = false)
  public Long tid;

  @Column(name = "username", nullable = false, unique = false)
  public String username;

  @Column(name = "type", nullable = false, unique = false)
  public String type;
}
