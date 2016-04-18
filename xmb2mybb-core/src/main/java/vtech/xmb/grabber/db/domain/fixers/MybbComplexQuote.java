package vtech.xmb.grabber.db.domain.fixers;

public class MybbComplexQuote extends MybbQuote {

  private String author;
  private long postId;
  private long dateline;

  public MybbComplexQuote() {
    super();
  }

  public void setAuthor(String author) {
    this.author = author;
  }

  public void setPostId(long postId) {
    this.postId = postId;
  }

  public void setDateline(long dateline) {
    this.dateline = dateline;
  }

  @Override
  public String toString() {
    return String.format("[quote='%s' pid='%s' dateline='%s']", author, postId, dateline);
  }
}
