package vtech.xmb.grabber.db.domain;

import org.apache.log4j.Logger;

public class ProgressCalculator {

  private long maxHits;
  private long currentHits;

  private int lastRestrictedPercentProgress = 0;

  public ProgressCalculator(long maxHits) {
    this.maxHits = maxHits;
    this.currentHits = 0;
  }

  public void hit() {
    currentHits++;
  }

  public void hit(long hits) {
    currentHits += hits;
  }

  public int getPercentProgress() {
    return (int) (100 * currentHits / maxHits);
  }

  public void logProgress(Logger... loggers) {
    logProgress(10, loggers);
  }

  public void logProgress(int resolution, Logger... loggers) {
    int currentRestrictedPercentProgress = getPercentProgress();

    if (lastRestrictedPercentProgress / resolution == currentRestrictedPercentProgress / resolution) {
      return;
    }

    for (Logger logger : loggers) {
      logger.info(String.format("Progress is %s", currentRestrictedPercentProgress));
    }
    lastRestrictedPercentProgress = currentRestrictedPercentProgress;
  }
}
