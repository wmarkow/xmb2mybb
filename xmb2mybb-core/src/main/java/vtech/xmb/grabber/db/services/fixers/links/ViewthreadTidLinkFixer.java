package vtech.xmb.grabber.db.services.fixers.links;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import vtech.xmb.grabber.db.cache.MybbThreadsCache;
import vtech.xmb.grabber.db.domain.fixers.FixResult;
import vtech.xmb.grabber.db.mybb.entities.MybbThread;
import vtech.xmb.grabber.db.services.fixers.StringFixer;

@Component
public class ViewthreadTidLinkFixer extends StringFixer {
  private final static Logger LOGGER = Logger.getLogger(ViewthreadTidLinkFixer.class);

  @Autowired
  private MybbThreadsCache mybbThreadsCache;
  @Value("${xmb.forum.links.prefix}")
  private String xmbForumLinksPrefix;
  @Value("${mybb.forum.links.prefix}")
  private String mybbForumLinksPrefix;

  @Override
  public FixResult fix(final String textToFix) throws ParseException {
    String result = textToFix;
    boolean fixed = false;

    Pattern pattern = Pattern.compile(xmbForumLinksPrefix + "viewthread.php\\?tid=\\d+");
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
    String[] splits = xmbLink.split("tid=");
    if (splits.length != 2) {
      final String converted = createMybbLink(0);
      LOGGER.warn(String.format("XMB thread link converted to null: %s -> %s", xmbLink, converted));

      return converted;
    }

    try {
      final long tid = Long.valueOf(splits[1]);

      MybbThread mybbThread = mybbThreadsCache.findByXmbThreadId(tid);
      if (mybbThread == null) {
        final String converted = createMybbLink(0);
        LOGGER.warn(String.format("XMB thread link converted to null: %s -> %s", xmbLink, converted));

        return converted;
      }

      return createMybbLink(mybbThread.tid);
    } catch (NumberFormatException ex) {
      final String converted = createMybbLink(0);
      LOGGER.warn(String.format("XMB thread link converted to null: %s -> %s", xmbLink, converted));

      return converted;
    }
  }

  private String createMybbLink(long threadId) {
    return String.format("%sshowthread.php?tid=%s", mybbForumLinksPrefix, threadId);
  }
}
