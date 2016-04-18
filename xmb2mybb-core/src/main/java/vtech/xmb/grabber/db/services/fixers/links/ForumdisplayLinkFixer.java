package vtech.xmb.grabber.db.services.fixers.links;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import vtech.xmb.grabber.db.cache.MybbForumsCache;
import vtech.xmb.grabber.db.domain.fixers.LinkFixResult;
import vtech.xmb.grabber.db.mybb.entities.MybbForum;
import vtech.xmb.grabber.db.mybb.entities.MybbPost;

@Component
public class ForumdisplayLinkFixer extends LinkFixer {
  private final static Logger LOGGER = Logger.getLogger("vtech.xmb.grabber.db.services.fixers.links.BrokenLinksLogger");

  @Autowired
  private MybbForumsCache mybbForumsCache;
//  @Value("${xmb.forum.links.prefix}")
//  private String xmbForumLinksPrefix;
//  @Value("${mybb.forum.links.prefix}")
//  private String mybbForumLinksPrefix;

  public LinkFixResult fix(String textToFix, MybbPost mybbPost) {
    LinkFixResult fixResult = new LinkFixResult();
    String result = textToFix;

    // wino.org.pl/forum/forumdisplay.php?fid=38
    Pattern pattern = Pattern.compile(xmbForumLinksPrefix + "forumdisplay.php\\?fid=\\d+");
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
    String[] splits = xmbLink.split("fid=");
    if (splits.length != 2) {
      return null;
    }

    try {
      final long fid = Long.valueOf(splits[1]);

      MybbForum mybbForum = mybbForumsCache.findByXmbForumId(fid);
      if (mybbForum == null) {
        return null;
      }

      return createMybbLink(mybbForum.fid);
    } catch (NumberFormatException ex) {
      return null;
    }
  }

  private String createMybbLink(long forumId) {
    return String.format("%sforumdisplay.php?fid=%s", mybbForumLinksPrefix, forumId);
  }
}
