package vtech.xmb.grabber.db.services.fixers;

public class FixResult {
  private String fixedText;
  private boolean fixRequired;

  public String getFixedText() {
    return fixedText;
  }

  void setFixedText(String fixedText) {
    this.fixedText = fixedText;
  }

  public boolean isFixRequired() {
    return fixRequired;
  }

  void setFixRequired(boolean fixRequired) {
    this.fixRequired = fixRequired;
  }
}
