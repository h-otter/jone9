import java.util.Observer;
import java.util.Observable;
import java.util.Random;

class ViewClock implements Observer {
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
  public void update(Observable o, Object arg){
    
  }

  private static final int defaultMaxChangeMilliSec = 20000;
  private int maxChangeMilliSec;
  private static final int defaultMinChangeMilliSec = 5000;
  private int minChangeMilliSec;
  private long lastChangedMillSec;
  private Random rng;

/**
 * deside whether change speed or not
 * @param currentTime start proc time
 *
 * [+] algorithm example
 *  random < maxChangeMilliSec
 *  <--------------->
 * |-------------|--------------|
 * last          currentTime    last + maxChangeMilliSec
 * -> in this case, not change clockwise speed
 * -> binomial distribution
 */
  public void clockwise(long currentTime) {
    if (rng.nextInt(maxChangeMilliSec) + lastChangedMillSec < currentTime && currentTime - lastChangedMillSec > minChangeMilliSec) {
      lastChangedMillSec = currentTime;
      changeClockwise();
    }
  }

  private static final float speedRange = 1;
  private float speed;

  private void changeClockwise(){
    if (debugMode) {
      System.out.println("[+] changed speed of clockwise");
    }
  }
}