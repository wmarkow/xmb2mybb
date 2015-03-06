package vtech.xmb.grabber.db.services.fixers;

import static org.junit.Assert.assertEquals;

import java.text.ParseException;

import org.junit.Test;

import vtech.xmb.grabber.db.domain.fixers.XmbComplexQuote;
import vtech.xmb.grabber.db.domain.fixers.XmbQuoteParser;

public class RquoteTest {

  @Test
  public void testFirst() throws ParseException {
    XmbComplexQuote rquote = (XmbComplexQuote) XmbQuoteParser.parse("[rquote=351038&amp;tid=24803&amp;author=greg_x]");

    assertEquals("greg_x", rquote.getAuthor());
    assertEquals(351038, rquote.getPostId());
    assertEquals(24803, rquote.getThreadId());
  }
  
  @Test
  public void testSecond() throws ParseException {
    XmbComplexQuote rquote = (XmbComplexQuote) XmbQuoteParser.parse("[rquote=350996&amp;tid=24803&amp;author=Megana]");

    assertEquals("Megana", rquote.getAuthor());
    assertEquals(350996, rquote.getPostId());
    assertEquals(24803, rquote.getThreadId());
  }
}
