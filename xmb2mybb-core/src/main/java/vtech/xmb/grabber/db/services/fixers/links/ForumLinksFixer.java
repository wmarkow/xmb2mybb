package vtech.xmb.grabber.db.services.fixers.links;

import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import vtech.xmb.grabber.db.domain.fixers.FixResult;
import vtech.xmb.grabber.db.mybb.repositories.MybbAttachmentsRepository;
import vtech.xmb.grabber.db.services.fixers.FixersChain;
import vtech.xmb.grabber.db.services.fixers.StringFixer;

@Component
public class ForumLinksFixer extends StringFixer {
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

  private FixersChain fixersChain;

  @Override
  public FixResult fix(final String textToFix) throws ParseException {
    FixResult fixResult = fixersChain.fix(textToFix);

    Pattern pattern = Pattern.compile(xmbForumLinksPrefix);
    Matcher matcher = pattern.matcher(fixResult.getFixedText());

    while (matcher.find()) {
      final String biggerLinkAsString = fixResult.getFixedText().substring(Math.max(0, matcher.start() - 100),
          Math.min(fixResult.getFixedText().length(), matcher.end() + 100));

      LOGGER.warn(String.format("Link to fix is something like %s", biggerLinkAsString));
    }
    return fixResult;
  }

  @PostConstruct
  private void init() {
    fixersChain = new FixersChain();
    fixersChain.addFixerToChain(viewthreadTidPagePidLinkFixer);
    fixersChain.addFixerToChain(viewthreadTidLinkFixer);
    fixersChain.addFixerToChain(forumdisplayLinkFixer);
  }
}
