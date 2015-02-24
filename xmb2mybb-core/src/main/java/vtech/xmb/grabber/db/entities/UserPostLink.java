package vtech.xmb.grabber.db.entities;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

@Entity
@Table(name = "user_posts_links")
public class UserPostLink extends GenericEntity {

  @OneToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
  @JoinColumn(name = "user_id")
  private XmbUser xmbUser;

  @Column(name = "post_time", nullable = false, unique = false)
  @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
  private DateTime time;

  @Column(name = "thread_title", nullable = false, unique = false)
  private String threadTitle;

  @Column(name = "post_link", nullable = false, unique = false)
  private String postLink;

  public UserPostLink() {
    super();
  }

  public DateTime getTime() {
    return time;
  }

  public XmbUser getXmbUser() {
    return xmbUser;
  }

  public void setXmbUser(XmbUser xmbUser) {
    this.xmbUser = xmbUser;
  }

  public String getThreadTitle() {
    return threadTitle;
  }

  public void setThreadTitle(String threadTitle) {
    this.threadTitle = threadTitle;
  }

  public String getPostLink() {
    return postLink;
  }

  public void setPostLink(String postLink) {
    this.postLink = postLink;
  }

  public void setTime(DateTime time) {
    this.time = time;
  }
}
