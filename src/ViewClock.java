import java.util.Observer;
import java.util.Observable;
import java.util.Random;

class ViewClock implements ViewInterface {
  public ViewClock(){
    this.rng = new Random(System.currentTimeMillis());

    this.maxChangeMilliSec = defaultMaxChangeMilliSec; 
    this.minChangeMilliSec = defaultMinChangeMilliSec; 
    this.lastChangedMillSec = 0;
  }

  private boolean debugMode;
  public void setDebugMode(boolean debug){
    this.debugMode = debug;
  }

  @Override
  public void running(long currentTime){
    this.clockwise(currentTime);
  }

  private static final int defaultMaxChangeMilliSec = 15000;
  private int maxChangeMilliSec;
  private static final int defaultMinChangeMilliSec = 3000;
  private int minChangeMilliSec;
  private long lastChangedMillSec;
  private Random rng;

/**
 * deside whether change speed or not
 *
 * [+] algorithm example
 *  random < max
 *  <--------------->
 * |------|------|--------------|
 * last   min    currentTime    last + max
 * -> in this case, not change clockwise speed
 * -> binomial distribution
 *
 * @param currentTime start proc time
 */
  public void clockwise(long currentTime) {
    if (this.rng.nextInt(this.maxChangeMilliSec - this.minChangeMilliSec) + this.lastChangedMillSec + this.minChangeMilliSec < currentTime) {
      if (this.debugMode) {
        System.out.println("[+] changed speed of clockwise in " + (currentTime - this.lastChangedMillSec));
      }
      this.lastChangedMillSec = currentTime;
      changeClockwise();
      recalcParams();
    }
  }

  private final static double maxParam = 0.93;
  private final static double minParam = 0.95;

/**
 * recalc maxChangeMilliSec and minChangeMilliSec
 * a_n = a_n-1 * params
 */
  private void recalcParams(){
    this.maxChangeMilliSec = (int)(this.maxChangeMilliSec * (this.maxParam + this.rng.nextDouble() % 0.05));
    this.minChangeMilliSec = (int)(this.minChangeMilliSec * (this.minParam + this.rng.nextDouble() % 0.05));
    if (this.maxChangeMilliSec < this.minChangeMilliSec + 1000){
      this.maxChangeMilliSec = this.minChangeMilliSec + 1000;
    }

    if (this.debugMode) {
      System.out.println("[+] recalced maxChangeMilliSec = " + this.maxChangeMilliSec + ", minChangeMilliSec = " + this.minChangeMilliSec);
    }
  }

  private static final float speedRange = 1;
  private float speed;

  private void changeClockwise(){}

/**
 * if finished game, call this
 */
  public void finish(){

  }
}