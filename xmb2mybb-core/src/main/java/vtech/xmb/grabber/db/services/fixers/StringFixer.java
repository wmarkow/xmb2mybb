package vtech.xmb.grabber.db.services.fixers;

import java.text.ParseException;

import vtech.xmb.grabber.db.domain.fixers.FixResult;

public abstract class StringFixer {

  public abstract FixResult fix(String textToFix) throws ParseException;
}
