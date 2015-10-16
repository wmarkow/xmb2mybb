package vtech.xmb.grabber.db.services.fixers.links;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import vtech.xmb.grabber.db.domain.fixers.LinkFixResult;
import vtech.xmb.grabber.db.mybb.entities.MybbPost;

@Component
public class ForumRulesLinkFixer extends LinkFixer {
  private final static Logger LOGGER = Logger.getLogger("vtech.xmb.grabber.db.services.fixers.links.ForumRulesLinkFixer");

  public LinkFixResult fix(final String textToFix, MybbPost mybbPost) {
    LinkFixResult fixResult = new LinkFixResult();
    String result = textToFix;

    Pattern pattern = Pattern.compile(xmbForumLinksPrefix + "faq.php\\?page=forumrules");
    Matcher matcher = pattern.matcher(result);

    Map<String, String> toReplace = new HashMap<String, String>();
    while (matcher.find()) {
      final String xmbLinkAsString = matcher.group();
      String mybbLinkAsString = String.format("%sannouncements.php?aid=1", mybbForumLinksPrefix);

      toReplace.put(xmbLinkAsString, mybbLinkAsString);
      
      LOGGER.info(String.format("In post pid=%s, tid=%s fixed link %s -> %s", mybbPost.pid, mybbPost.tid, xmbLinkAsString, mybbLinkAsString));
    }

    for (String file : toReplace.keySet()) {
      result = result.replace(file, toReplace.get(file));
      fixResult.setFixRequired(true);
    }

    fixResult.setFixedText(result);
    return fixResult;
  }
}
