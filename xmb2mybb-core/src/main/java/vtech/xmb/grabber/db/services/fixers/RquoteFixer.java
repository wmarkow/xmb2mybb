package vtech.xmb.grabber.db.services.fixers;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import vtech.xmb.grabber.db.domain.fixers.FixResult;
import vtech.xmb.grabber.db.domain.fixers.MybbQuote;
import vtech.xmb.grabber.db.domain.fixers.XmbQuote;
import vtech.xmb.grabber.db.domain.fixers.XmbQuote2MybbQuote;
import vtech.xmb.grabber.db.domain.fixers.XmbQuoteParser;

@Component
public class RquoteFixer extends StringFixer {
  private final static Logger LOGGER = Logger.getLogger(RquoteFixer.class);

  @Autowired
  private XmbQuote2MybbQuote xmbQuote2MybbQuote;

  @Override
  public FixResult fix(final String textToFix) throws ParseException {
    String result = textToFix;
    boolean fixed = false;

    Pattern pattern = Pattern.compile("\\[rquote.*?\\]");
    Matcher matcher = pattern.matcher(result);

    List<Rquote2Quote> matches = new ArrayList<Rquote2Quote>();

    while (matcher.find()) {
      final String rquoteAsString = matcher.group();
      final XmbQuote xmbQuote = XmbQuoteParser.parse(rquoteAsString);
      final MybbQuote mybbQuote = xmbQuote2MybbQuote.xmbRquote2MybbQuote(xmbQuote);

      matches.add(new Rquote2Quote(xmbQuote, mybbQuote));
    }

    for (Rquote2Quote match : matches) {
      result = result.replace(match.getXmbQuote().getOriginalString(), match.getMybbQuote().toString());
      fixed = true;
    }

    result = result.replaceAll("\\[/rquote\\]", "\\[/quote\\]");

    FixResult fixResult = new FixResult();
    fixResult.setFixedText(result);
    fixResult.setFixRequired(fixed);
    return fixResult;
  }

  private class Rquote2Quote {
    private XmbQuote xmbQuote;
    private MybbQuote mybbQuote;

    public Rquote2Quote(XmbQuote xmbQuote, MybbQuote mybbQuote) {
      this.xmbQuote = xmbQuote;
      this.mybbQuote = mybbQuote;
    }

    public XmbQuote getXmbQuote() {
      return xmbQuote;
    }

    public MybbQuote getMybbQuote() {
      return mybbQuote;
    }
  }
}
