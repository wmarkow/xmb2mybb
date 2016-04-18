package vtech.xmb.grabber.db.domain.fixers;

public class FixResult {
  private String fixedText;
  private boolean fixRequired;

  public String getFixedText() {
    return fixedText;
  }

  public void setFixedText(String fixedText) {
    this.fixedText = fixedText;
  }

  public boolean isFixRequired() {
    return fixRequired;
  }

  public void setFixRequired(boolean fixRequired) {
    this.fixRequired = fixRequired;
  }
}
