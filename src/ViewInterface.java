interface ViewInterface {
/**
 * set class as debug mode
 * @param boolean debug
 */
  public void setDebugMode(boolean debug);

/**
 * called in finished game
 */
  public void finish();

/**
 * called per frame
 */
  public void running(long currentTime);
}