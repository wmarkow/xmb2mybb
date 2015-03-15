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
public class RquoteFixer {
  private final static Logger LOGGER = Logger.getLogger(RquoteFixer.class);

  @Autowired
  private XmbQuote2MybbQuote xmbQuote2MybbQuote;

  public FixResult fix(final String textToFix, long mybbPid, long xmbPid) {
    FixResult fixResult = new FixResult();
    String result = textToFix;

    Pattern pattern = Pattern.compile("\\[rquote.*?\\]");
    Matcher matcher = pattern.matcher(result);

    List<Rquote2Quote> matches = new ArrayList<Rquote2Quote>();

    while (matcher.find()) {
      final String rquoteAsString = matcher.group();
      try {
        final XmbQuote xmbQuote = XmbQuoteParser.parse(rquoteAsString);
        final MybbQuote mybbQuote = xmbQuote2MybbQuote.xmbRquote2MybbQuote(xmbQuote);

        matches.add(new Rquote2Quote(xmbQuote, mybbQuote));
      } catch (ParseException e) {
        LOGGER.warn(String.format("MyBB post with pid=%s and xmbpid=%s contains invalid XMB rquote (%s).", mybbPid, xmbPid, rquoteAsString));
      }
    }

    for (Rquote2Quote match : matches) {
      result = result.replace(match.getXmbQuote().getOriginalString(), match.getMybbQuote().toString());
      fixResult.setFixRequired(true);
    }

    result = result.replaceAll("\\[/rquote\\]", "\\[/quote\\]");
    fixResult.setFixedText(result);

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
