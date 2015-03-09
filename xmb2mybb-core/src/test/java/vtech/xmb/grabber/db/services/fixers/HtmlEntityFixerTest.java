package vtech.xmb.grabber.db.services.fixers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.text.ParseException;

import org.junit.Test;

import vtech.xmb.grabber.db.domain.fixers.FixResult;

public class HtmlEntityFixerTest {

  private HtmlEntityFixer fixer = new HtmlEntityFixer();

  @Test
  public void testForNoEntities() throws ParseException {
    FixResult fixResult = fixer.fix("a simple text with no html entities");

    assertFalse(fixResult.isFixRequired());
    assertEquals("a simple text with no html entities", fixResult.getFixedText());
  }

  @Test
  public void testForSimpleEntity() throws ParseException {
    FixResult fixResult = fixer.fix("a simple text with &amp; entity");

    assertTrue(fixResult.isFixRequired());
    assertEquals("a simple text with & entity", fixResult.getFixedText());
  }

  @Test
  public void testForMultipleEntities() throws ParseException {
    FixResult fixResult = fixer.fix("a simple text with &amp; entity and &gt; and &quot;;");

    assertTrue(fixResult.isFixRequired());
    assertEquals("a simple text with & entity and > and \";", fixResult.getFixedText());
  }

  @Test
  public void testForHexEntity() throws ParseException {
    FixResult fixResult = fixer.fix("a simple text with &#xe5;");

    assertTrue(fixResult.isFixRequired());
    assertEquals("a simple text with å", fixResult.getFixedText());
  }
  
  @Test
  public void testForDoubleEncodedHexEntity() throws ParseException {
    FixResult fixResult = fixer.fix("a simple text with &amp;#xe5;");

    assertTrue(fixResult.isFixRequired());
    assertEquals("a simple text with å", fixResult.getFixedText());
  }
  
  @Test
  public void testForDoubleEncodedDecimalEntity() throws ParseException {
    FixResult fixResult = fixer.fix("a simple text with &amp;#61514;");

    assertTrue(fixResult.isFixRequired());
    assertEquals("a simple text with ", fixResult.getFixedText());
  }
}
