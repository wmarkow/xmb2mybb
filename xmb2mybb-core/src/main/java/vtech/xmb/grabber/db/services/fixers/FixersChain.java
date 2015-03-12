package vtech.xmb.grabber.db.services.fixers;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import vtech.xmb.grabber.db.domain.fixers.FixResult;

public class FixersChain extends StringFixer<FixResult> {

  private List<StringFixer<FixResult>> chain = new ArrayList<StringFixer<FixResult>>();

  public FixersChain addFixerToChain(StringFixer<FixResult> fixer) {
    chain.add(fixer);

    return this;
  }

  @Override
  public FixResult fix(String textToFix) throws ParseException {
    String fixedString = textToFix;
    boolean fixRequired = false;

    for (StringFixer<FixResult> fixer : chain) {
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
