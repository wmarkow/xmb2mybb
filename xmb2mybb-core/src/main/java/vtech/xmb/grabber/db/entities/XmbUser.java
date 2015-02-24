package vtech.xmb.grabber.db.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.springframework.util.Assert;

@Entity
@Table(name = "users")
public class XmbUser extends GenericEntity {

  @Column(name = "login", nullable = false, unique = true)
  private String login;

  XmbUser() {
    super();
  }

  public XmbUser(String login) {
    super();

    Assert.notNull(login);

    this.login = login;
  }

  public String getLogin() {
    return login;
  }

  void setLogin(String login) {
    this.login = login;
  }
}
