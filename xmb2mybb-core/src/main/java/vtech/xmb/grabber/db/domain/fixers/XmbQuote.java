package vtech.xmb.grabber.db.domain.fixers;

public abstract class XmbQuote extends Quote {

  private String originalString;

  XmbQuote(String originalString) {
    super();
    this.originalString = originalString;
  }

  public String getOriginalString() {
    return originalString;
  }
}
