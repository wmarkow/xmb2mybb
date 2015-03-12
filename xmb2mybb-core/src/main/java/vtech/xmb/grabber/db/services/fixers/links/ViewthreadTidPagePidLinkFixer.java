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

import vtech.xmb.grabber.db.domain.fixers.FixResult;
import vtech.xmb.grabber.db.mybb.entities.MybbPost;
import vtech.xmb.grabber.db.mybb.repositories.MybbPostsRepository;
import vtech.xmb.grabber.db.services.fixers.StringFixer;

@Component
public class ViewthreadTidPagePidLinkFixer extends StringFixer {
  private final static Logger LOGGER = Logger.getLogger(ViewthreadTidPagePidLinkFixer.class);

  @Autowired
  private MybbPostsRepository mybbPostsRepository;
  @Value("${xmb.forum.links.prefix}")
  private String xmbForumLinksPrefix;
  @Value("${mybb.forum.links.prefix}")
  private String mybbForumLinksPrefix;

  @Override
  public FixResult fix(final String textToFix) throws ParseException {
    String result = textToFix;
    boolean fixed = false;

    Pattern pattern = Pattern.compile(xmbForumLinksPrefix + "viewthread.php\\?tid=\\d+(&page=\\d+#pid\\d+|#pid\\d+)");
    Matcher matcher = pattern.matcher(result);

    Map<String, String> toReplace = new HashMap<String, String>();
    while (matcher.find()) {
      final String xmbLinkAsString = matcher.group();
      final String mybbLinkAsString = convertXmbToMybb(xmbLinkAsString);

      if (mybbLinkAsString == null) {
        LOGGER.warn(String.format("Can not convert XMB link %s to its MyBB link.", xmbLinkAsString));
        continue;
      }

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
    String[] splits = xmbLink.split("#pid");
    if (splits.length != 2) {
      final String converted = createMybbLink(0);
      LOGGER.warn(String.format("XMB post link converted to null: %s -> %s", xmbLink, converted));

      return converted;
    }

    try {
      final long pid = Long.valueOf(splits[1]);

      MybbPost mybbPost = mybbPostsRepository.findByXmbpid(pid);
      if (mybbPost == null) {
        final String converted = createMybbLink(0);
        LOGGER.warn(String.format("XMB post link converted to null: %s -> %s", xmbLink, converted));

        return converted;
      }

      return createMybbLink(mybbPost.pid);
    } catch (NumberFormatException ex) {
      final String converted = createMybbLink(0);
      LOGGER.warn(String.format("XMB post link converted to null: %s -> %s", xmbLink, converted));

      return converted;
    }
  }

  private String createMybbLink(long postId) {
    return String.format("%sshowthread.php?pid=%s#pid%s", mybbForumLinksPrefix, postId, postId);
  }
}
