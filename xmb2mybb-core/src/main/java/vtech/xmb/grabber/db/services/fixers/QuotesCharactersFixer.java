package vtech.xmb.grabber.db.services.fixers;

import org.springframework.stereotype.Component;

@Component
public class QuotesCharactersFixer extends StringFixer {

  @Override
  public FixResult fix(final String textToFix) {
    String result = textToFix;
    boolean fixed = false;
    
    if (result.contains("\\\"")) {
      result = result.replaceAll("\\\\\"", "\"");
      fixed = true;
    }

    if (result.contains("\\'")) {
      result = result.replaceAll("\\\\'", "'");
      fixed = true;
    }

    FixResult fixResult = new FixResult();
    fixResult.setFixedText(result);
    fixResult.setFixRequired(fixed);

    return fixResult;
  }
}
