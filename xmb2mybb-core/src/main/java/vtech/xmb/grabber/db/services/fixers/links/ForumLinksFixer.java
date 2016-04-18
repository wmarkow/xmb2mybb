package vtech.xmb.grabber.db.services.fixers.links;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import vtech.xmb.grabber.db.domain.fixers.LinkFixResult;
import vtech.xmb.grabber.db.mybb.entities.MybbPost;
import vtech.xmb.grabber.db.mybb.repositories.MybbAttachmentsRepository;

@Component
public class ForumLinksFixer {
  private final static Logger LINKS_TO_FIX_LOGGER = Logger.getLogger("vtech.xmb.grabber.db.services.fixers.links.LinksToFixLogger");

  @Autowired
  private MybbAttachmentsRepository mybbAttachmentsRepository;
  @Autowired
  private ViewthreadTidPagePidLinkFixer viewthreadTidPagePidLinkFixer;
  @Autowired
  private ViewthreadTidLinkFixer viewthreadTidLinkFixer;
  @Autowired
  private ForumdisplayLinkFixer forumdisplayLinkFixer;
  @Autowired
  private ForumRulesLinkFixer forumRulesLinkFixer;
  
  @Value("${xmb.forum.links.prefix}")
  private String xmbForumLinksPrefix;

  public LinkFixResult fix(final String textToFix, MybbPost mybbPost) {
    LinkFixResult fixResult1 = viewthreadTidPagePidLinkFixer.fix(textToFix, mybbPost);
    LinkFixResult fixResult2 = viewthreadTidLinkFixer.fix(fixResult1.getFixedText(), mybbPost);
    LinkFixResult fixResult3 = forumdisplayLinkFixer.fix(fixResult2.getFixedText(), mybbPost);
    LinkFixResult fixResult4 = forumRulesLinkFixer.fix(fixResult3.getFixedText(), mybbPost);

    LinkFixResult fixResult = fixResult4;
    
    LinkFixResult result = new LinkFixResult();
    result.setFixedText(fixResult.getFixedText());
    result.setFixRequired(fixResult1.isFixRequired() | fixResult2.isFixRequired() | fixResult3.isFixRequired() | fixResult4.isFixRequired());
    result.setHasInvalidLinks(fixResult1.isHasInvalidLinks() | fixResult2.isHasInvalidLinks() | fixResult3.isHasInvalidLinks() | fixResult4.isHasInvalidLinks());

    Pattern pattern = Pattern.compile(xmbForumLinksPrefix);
    Matcher matcher = pattern.matcher(fixResult.getFixedText());

    while (matcher.find()) {
      final String biggerLinkAsString = fixResult.getFixedText().substring(matcher.start(), Math.min(fixResult.getFixedText().length(), matcher.end() + 100));

      LINKS_TO_FIX_LOGGER.warn(String.format("Do not know how to fix link like %s", biggerLinkAsString));
    }

    return result;
  }
}
