package vtech.xmb.grabber.db.services.fixers.links;

import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import vtech.xmb.grabber.db.domain.fixers.LinkFixResult;
import vtech.xmb.grabber.db.mybb.repositories.MybbAttachmentsRepository;
import vtech.xmb.grabber.db.services.fixers.StringFixer;

@Component
public class ForumLinksFixer extends StringFixer<LinkFixResult> {
  private final static Logger LOGGER = Logger.getLogger(ForumLinksFixer.class);

  @Autowired
  private MybbAttachmentsRepository mybbAttachmentsRepository;
  @Autowired
  private ViewthreadTidPagePidLinkFixer viewthreadTidPagePidLinkFixer;
  @Autowired
  private ViewthreadTidLinkFixer viewthreadTidLinkFixer;
  @Autowired
  private ForumdisplayLinkFixer forumdisplayLinkFixer;
  @Value("${xmb.forum.links.prefix}")
  private String xmbForumLinksPrefix;

  @Override
  public LinkFixResult fix(final String textToFix) throws ParseException {
    LinkFixResult fixResult1 = viewthreadTidPagePidLinkFixer.fix(textToFix);
    LinkFixResult fixResult2 = viewthreadTidLinkFixer.fix(fixResult1.getFixedText());
    LinkFixResult fixResult3 = forumdisplayLinkFixer.fix(fixResult2.getFixedText());

    Pattern pattern = Pattern.compile(xmbForumLinksPrefix);
    Matcher matcher = pattern.matcher(fixResult3.getFixedText());

    while (matcher.find()) {
      final String biggerLinkAsString = fixResult3.getFixedText().substring(Math.max(0, matcher.start() - 100),
          Math.min(fixResult3.getFixedText().length(), matcher.end() + 100));

      LOGGER.warn(String.format("Link to fix is something like %s", biggerLinkAsString));
    }

    LinkFixResult result = new LinkFixResult();
    result.setFixedText(fixResult3.getFixedText());
    result.setFixRequired(fixResult1.isFixRequired() | fixResult2.isFixRequired() | fixResult3.isFixRequired());
    result.setHasInvalidLinks(fixResult1.isHasInvalidLinks() | fixResult2.isHasInvalidLinks() | fixResult3.isHasInvalidLinks());

    return result;
  }
}
