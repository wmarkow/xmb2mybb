package vtech.xmb.grabber.db.services.fixers.links;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import vtech.xmb.grabber.db.cache.MybbForumsCache;
import vtech.xmb.grabber.db.domain.fixers.FixResult;
import vtech.xmb.grabber.db.mybb.entities.MybbForum;
import vtech.xmb.grabber.db.services.fixers.StringFixer;

@Component
public class ForumdisplayLinkFixer extends StringFixer {
  private final static Logger LOGGER = Logger.getLogger(ForumdisplayLinkFixer.class);

  @Autowired
  private MybbForumsCache mybbForumsCache;
  @Value("${xmb.forum.links.prefix}")
  private String xmbForumLinksPrefix;
  @Value("${mybb.forum.links.prefix}")
  private String mybbForumLinksPrefix;

  @Override
  public FixResult fix(String textToFix) throws ParseException {
    String result = textToFix;
    boolean fixed = false;

    // wino.org.pl/forum/forumdisplay.php?fid=38
    Pattern pattern = Pattern.compile(xmbForumLinksPrefix + "forumdisplay.php\\?fid=\\d+");
    Matcher matcher = pattern.matcher(result);

    Map<String, String> toReplace = new HashMap<String, String>();
    while (matcher.find()) {
      final String xmbLinkAsString = matcher.group();
      final String mybbLinkAsString = convertXmbToMybb(xmbLinkAsString);

      toReplace.put(xmbLinkAsString, mybbLinkAsString);
    }

    for (String file : toReplace.keySet()) {
      result = result.replace(file, toReplace.get(file));
      fixed = true;
    }

    FixResult fixResult = new FixResult();
    fixResult.setFixedText(result);
    fixResult.setFixRequired(fixed);
    return fixResult;
  }

  private String convertXmbToMybb(String xmbLink) {
    String[] splits = xmbLink.split("fid=");
    if (splits.length != 2) {
      final String converted = createMybbLink(0);
      LOGGER.warn(String.format("XMB forum link converted to null: %s -> %s", xmbLink, converted));

      return converted;
    }

    try {
      final long fid = Long.valueOf(splits[1]);

      MybbForum mybbForum = mybbForumsCache.findByXmbForumId(fid);
      if (mybbForum == null) {
        final String converted = createMybbLink(0);
        LOGGER.warn(String.format("XMB forum link converted to null: %s -> %s", xmbLink, converted));

        return converted;
      }

      return createMybbLink(mybbForum.fid);
    } catch (NumberFormatException ex) {
      final String converted = createMybbLink(0);
      LOGGER.warn(String.format("XMB forum link converted to null: %s -> %s", xmbLink, converted));

      return converted;
    }
  }

  private String createMybbLink(long forumId) {
    return String.format("%sforumdisplay.php?fid=%s", mybbForumLinksPrefix, forumId);
  }
}
