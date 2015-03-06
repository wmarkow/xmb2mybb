package vtech.xmb.grabber.db.domain.fixers;

public class MybbSimpleQuote extends MybbQuote {

  private String author;

  public MybbSimpleQuote(String author) {
    this.author = author;
  }

  @Override
  public String toString() {
    return String.format("[quote='%s']", author);
  }
}
