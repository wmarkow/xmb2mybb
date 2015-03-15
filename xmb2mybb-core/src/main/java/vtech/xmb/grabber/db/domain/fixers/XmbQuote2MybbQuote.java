package vtech.xmb.grabber.db.domain.fixers;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import vtech.xmb.grabber.db.mybb.entities.MybbPost;
import vtech.xmb.grabber.db.mybb.repositories.MybbPostsRepository;
import vtech.xmb.grabber.db.services.fixers.RquoteFixer;

@Component
public class XmbQuote2MybbQuote {
  private final static Logger LOGGER = Logger.getLogger(RquoteFixer.class);

  @Autowired
  private MybbPostsRepository mybbPostsRepository;

  public MybbQuote xmbRquote2MybbQuote(XmbQuote xmbQuote) {
    if (xmbQuote instanceof XmbComplexQuote) {
      XmbComplexQuote xmbComplexQuote = (XmbComplexQuote) xmbQuote;

      MybbPost mybbPost = mybbPostsRepository.findByXmbpid(xmbComplexQuote.getPostId());
      if (mybbPost == null) {
        return new MybbSimpleQuote(xmbComplexQuote.getAuthor());
      }

      if (!mybbPost.username.equals(xmbComplexQuote.getAuthor())) {
        LOGGER.warn(String.format("Username mismatch in MyBB Post pid=%s quotes %s. The real post author is %s", mybbPost.pid,
            xmbComplexQuote.getOriginalString(), mybbPost.username));
      }
      MybbComplexQuote mybbComplexQuote = new MybbComplexQuote();
      mybbComplexQuote.setAuthor(xmbComplexQuote.getAuthor());
      mybbComplexQuote.setPostId(mybbPost.pid);
      mybbComplexQuote.setDateline(mybbPost.dateline);

      return mybbComplexQuote;
    }

    if (xmbQuote instanceof XmbSimpleQuote) {
      return new MybbVerySimpleQuote();
    }

    return null;
  }
}
