package vtech.xmb.grabber.db.services.fixers;

import java.text.ParseException;

import vtech.xmb.grabber.db.domain.fixers.FixResult;

public abstract class StringFixer<T extends FixResult> {

  public abstract T fix(String textToFix) throws ParseException;
}
