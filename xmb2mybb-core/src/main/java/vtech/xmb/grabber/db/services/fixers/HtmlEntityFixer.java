package vtech.xmb.grabber.db.services.fixers;

import java.text.ParseException;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.web.util.HtmlUtils;

import vtech.xmb.grabber.db.domain.fixers.FixResult;

@Component
public class HtmlEntityFixer extends StringFixer {
  private final static Logger LOGGER = Logger.getLogger(HtmlEntityFixer.class);

  @Override
  public FixResult fix(final String textToFix) throws ParseException {
    String unescaped = HtmlUtils.htmlUnescape(textToFix);
    // sometimes text are double encoded
    unescaped = HtmlUtils.htmlUnescape(unescaped);
    
    FixResult fixResult = new FixResult();
    fixResult.setFixedText(unescaped);

    if (unescaped.equals(textToFix)) {
      fixResult.setFixRequired(false);
    } else {
      fixResult.setFixRequired(true);
    }
    return fixResult;
  }
}
