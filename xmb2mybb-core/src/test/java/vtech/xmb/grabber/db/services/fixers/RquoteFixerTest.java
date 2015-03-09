package vtech.xmb.grabber.db.services.fixers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.Whitebox;
import org.mockito.runners.MockitoJUnitRunner;

import vtech.xmb.grabber.db.domain.fixers.FixResult;
import vtech.xmb.grabber.db.domain.fixers.MybbComplexQuote;
import vtech.xmb.grabber.db.domain.fixers.XmbComplexQuote;
import vtech.xmb.grabber.db.domain.fixers.XmbQuote2MybbQuote;
import vtech.xmb.grabber.db.domain.fixers.XmbQuoteParser;

@RunWith(MockitoJUnitRunner.class)
public class RquoteFixerTest {

  @Mock
  private XmbQuote2MybbQuote xmbQuote2MybbQuote;

  private RquoteFixer fixer;

  @Before
  public void init() {
    fixer = new RquoteFixer();
    Whitebox.setInternalState(fixer, "xmbQuote2MybbQuote", xmbQuote2MybbQuote);
  }

  @Test
  public void testFixNestedXmbQuotes() throws IOException, ParseException {
    XmbComplexQuote complexQuote1 = (XmbComplexQuote) XmbQuoteParser.parse("[rquote=351038&tid=24803&author=greg_x]");
    XmbComplexQuote complexQuote2 = (XmbComplexQuote) XmbQuoteParser.parse("[rquote=350996&tid=24803&author=Megana]");

    MybbComplexQuote mybbComplexQuote1 = new MybbComplexQuote();
    mybbComplexQuote1.setAuthor(complexQuote1.getAuthor());
    mybbComplexQuote1.setDateline(15001900);
    mybbComplexQuote1.setPostId(123);

    MybbComplexQuote mybbComplexQuote2 = new MybbComplexQuote();
    mybbComplexQuote2.setAuthor(complexQuote2.getAuthor());
    mybbComplexQuote2.setDateline(15002900);
    mybbComplexQuote2.setPostId(124);

    Mockito.when(xmbQuote2MybbQuote.xmbRquote2MybbQuote(Mockito.eq(complexQuote1))).thenReturn(mybbComplexQuote1);
    Mockito.when(xmbQuote2MybbQuote.xmbRquote2MybbQuote(Mockito.eq(complexQuote2))).thenReturn(mybbComplexQuote2);

    String nestedRquotes = FileUtils.readFileToString(new File("src/test/resources/fixers/nested_rquotes.txt"));
    String fixedNestedRquotes = FileUtils.readFileToString(new File("src/test/resources/fixers/nested_rquotes_fixed.txt"));

    FixResult fixResult = fixer.fix(nestedRquotes);

    assertTrue(fixResult.isFixRequired());
    assertEquals(fixedNestedRquotes, fixResult.getFixedText());
  }
}
