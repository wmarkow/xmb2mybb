package vtech.xmb.grabber.db.services.fixers;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import vtech.xmb.grabber.db.domain.fixers.FixResult;

public class FixersChain extends StringFixer {

  private List<StringFixer> chain = new ArrayList<StringFixer>();

  public FixersChain addFixerToChain(StringFixer fixer) {
    chain.add(fixer);

    return this;
  }

  @Override
  public FixResult fix(String textToFix) throws ParseException {
    String fixedString = textToFix;
    boolean fixRequired = false;

    for (StringFixer fixer : chain) {
      FixResult fixResult = fixer.fix(fixedString);

      fixRequired |= fixResult.isFixRequired();
      fixedString = fixResult.getFixedText();
    }

    FixResult fixResult = new FixResult();
    fixResult.setFixedText(fixedString);
    fixResult.setFixRequired(fixRequired);

    return fixResult;
  }
}
