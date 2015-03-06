package vtech.xmb.grabber.db.domain.fixers;

import java.text.ParseException;

public class XmbQuoteParser {

  public final static XmbQuote parse(String quoteAsString) throws ParseException {
    try {
      return parseAsComplex(quoteAsString);
    } catch (ParseException e) {
      // silently fail
    }

    try {
      return parseAsSimple(quoteAsString);
    } catch (ParseException e) {
      // silently fail
    }

    throw new ParseException(String.format("%s can not be parsed to XMB quote", quoteAsString), 0);
  }

  private final static XmbSimpleQuote parseAsSimple(String quoteAsString) throws ParseException {
    if ("[rquote]".equals(quoteAsString.trim())) {
      return new XmbSimpleQuote(quoteAsString);
    }

    throw new ParseException(String.format("%s can not be parsed as simple XMB quote", quoteAsString), 0);
  }

  private final static XmbComplexQuote parseAsComplex(String quoteAsString) throws ParseException {
    try {
      String[] splits = quoteAsString.split("&amp;");

      XmbComplexQuote rquote = new XmbComplexQuote(quoteAsString);

      rquote.setPostId(Long.valueOf(splits[0].replaceAll("\\[rquote=", "")));
      rquote.setThreadId(Long.valueOf(splits[1].replaceAll("tid=", "")));
      rquote.setAuthor(splits[2].replaceAll("author=", "").replaceAll("\\]", ""));

      return rquote;
    } catch (Exception ex) {
      throw new ParseException(ex.getMessage(), 0);
    }
  }
}
