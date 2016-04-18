package vtech.xmb.grabber.db.services.fixers.links;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import vtech.xmb.grabber.db.mybb.entities.MybbPost;

@Component
public abstract class LinkFixer {

  @Value("${xmb.forum.links.prefix}")
  protected String xmbForumLinksPrefix;
  @Value("${mybb.forum.links.prefix}")
  protected String mybbForumLinksPrefix;

  protected String getBrokenLinkeMessage(MybbPost mybbPost, String xmbLink, String mybbLink) {
    StringBuilder sb = new StringBuilder();
    sb.append("\n");
    sb.append(String.format("Broken link in XMB post : http://%sviewthread.php?tid=%s&goto=search&pid=%s \n", xmbForumLinksPrefix, mybbPost.xmbtid,
        mybbPost.xmbpid));
    sb.append(String.format("Broken link in MyBB post: http://%sshowthread.php?tid=%s&pid=%s#pid%s \n", mybbForumLinksPrefix, mybbPost.tid, mybbPost.pid,
        mybbPost.pid));
    sb.append(String.format("Converting link: %s -> %s \n", xmbLink, mybbLink));

    return sb.toString();
  }
}
