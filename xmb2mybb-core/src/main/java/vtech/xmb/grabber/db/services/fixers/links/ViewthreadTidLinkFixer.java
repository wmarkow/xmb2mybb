package vtech.xmb.grabber.db.services.fixers.links;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import vtech.xmb.grabber.db.cache.MybbThreadsCache;
import vtech.xmb.grabber.db.domain.fixers.LinkFixResult;
import vtech.xmb.grabber.db.mybb.entities.MybbPost;
import vtech.xmb.grabber.db.mybb.entities.MybbThread;

@Component
public class ViewthreadTidLinkFixer extends LinkFixer {
  private final static Logger LOGGER = Logger.getLogger("vtech.xmb.grabber.db.services.fixers.links.BrokenLinksLogger");

  @Autowired
  private MybbThreadsCache mybbThreadsCache;

  // @Value("${xmb.forum.links.prefix}")
  // private String xmbForumLinksPrefix;
  // @Value("${mybb.forum.links.prefix}")
  // private String mybbForumLinksPrefix;

  public LinkFixResult fix(final String textToFix, MybbPost mybbPost) {
    LinkFixResult fixResult = new LinkFixResult();
    String result = textToFix;

    Pattern pattern = Pattern.compile(xmbForumLinksPrefix + "viewthread.php\\?tid=\\d+");
    Matcher matcher = pattern.matcher(result);

    Map<String, String> toReplace = new HashMap<String, String>();
    while (matcher.find()) {
      final String xmbLinkAsString = matcher.group();
      String mybbLinkAsString = convertXmbToMybb(xmbLinkAsString);

      if (mybbLinkAsString == null) {
        mybbLinkAsString = createMybbLink(0);
        LOGGER.warn(getBrokenLinkeMessage(mybbPost, xmbLinkAsString, mybbLinkAsString));
        fixResult.setHasInvalidLinks(true);
      }

      toReplace.put(xmbLinkAsString, mybbLinkAsString);
    }

    for (String file : toReplace.keySet()) {
      result = result.replace(file, toReplace.get(file));
      fixResult.setFixRequired(true);
    }

    fixResult.setFixedText(result);
    return fixResult;
  }

  private String convertXmbToMybb(String xmbLink) {
    String[] splits = xmbLink.split("tid=");
    if (splits.length != 2) {
      return null;
    }

    try {
      final long tid = Long.valueOf(splits[1]);

      MybbThread mybbThread = mybbThreadsCache.findByXmbThreadId(tid);
      if (mybbThread == null) {
        return null;
      }

      return createMybbLink(mybbThread.tid);
    } catch (NumberFormatException ex) {
      return null;
    }
  }

  private String createMybbLink(long threadId) {
    return String.format("%sshowthread.php?tid=%s", mybbForumLinksPrefix, threadId);
  }
}
