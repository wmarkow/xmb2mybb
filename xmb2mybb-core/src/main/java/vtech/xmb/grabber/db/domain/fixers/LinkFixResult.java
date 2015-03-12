package vtech.xmb.grabber.db.domain.fixers;

public class LinkFixResult extends FixResult {

  private boolean hasInvalidLinks;

  public boolean isHasInvalidLinks() {
    return hasInvalidLinks;
  }

  public void setHasInvalidLinks(boolean hasInvalidLinks) {
    this.hasInvalidLinks = hasInvalidLinks;
  }
}
