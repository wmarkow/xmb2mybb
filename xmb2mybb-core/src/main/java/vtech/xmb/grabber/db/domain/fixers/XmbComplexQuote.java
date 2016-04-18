package vtech.xmb.grabber.db.domain.fixers;

public class XmbComplexQuote extends XmbQuote {

  private String author;
  private long postId;
  private long threadId;

  XmbComplexQuote(String originalString) {
    super(originalString);
  }

  public String getAuthor() {
    return author;
  }

  public long getPostId() {
    return postId;
  }

  public long getThreadId() {
    return threadId;
  }

  void setAuthor(String author) {
    this.author = author;
  }

  void setPostId(long postId) {
    this.postId = postId;
  }

  void setThreadId(long threadId) {
    this.threadId = threadId;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((author == null) ? 0 : author.hashCode());
    result = prime * result + (int) (postId ^ (postId >>> 32));
    result = prime * result + (int) (threadId ^ (threadId >>> 32));
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    XmbComplexQuote other = (XmbComplexQuote) obj;
    if (author == null) {
      if (other.author != null)
        return false;
    } else if (!author.equals(other.author))
      return false;
    if (postId != other.postId)
      return false;
    if (threadId != other.threadId)
      return false;
    return true;
  }
}
