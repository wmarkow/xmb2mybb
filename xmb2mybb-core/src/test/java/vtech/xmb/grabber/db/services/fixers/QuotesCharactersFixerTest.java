package vtech.xmb.grabber.db.services.fixers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import vtech.xmb.grabber.db.domain.fixers.FixResult;

public class QuotesCharactersFixerTest {

  private QuotesCharactersFixer fixer = new QuotesCharactersFixer();

  @Test
  public void testFixWhenFixNotRequired() {
    FixResult result = fixer.fix("this is a simple text");

    assertEquals("this is a simple text", result.getFixedText());
    assertFalse(result.isFixRequired());
  }

  @Test
  public void testFixForDoubleQuotes() {
    FixResult result = fixer.fix("this is a simple so called \\\"text\\\" and also a second \\\"quote\\\"");

    assertEquals("this is a simple so called \"text\" and also a second \"quote\"", result.getFixedText());
    assertTrue(result.isFixRequired());
  }
  
  @Test
  public void testFixForSingleQuotes() {
    FixResult result = fixer.fix("this is a simple so called \\'text\\' and also a second \\'quote\\'");

    assertEquals("this is a simple so called 'text' and also a second 'quote'", result.getFixedText());
    assertTrue(result.isFixRequired());
  }
  
  @Test
  public void testFixForSingleQuoteAndDoubleQuote() {
    FixResult result = fixer.fix("this is a simple so called \\\"text\\\" and also a second \\'quote\\'");

    assertEquals("this is a simple so called \"text\" and also a second 'quote'", result.getFixedText());
    assertTrue(result.isFixRequired());
  }
}
