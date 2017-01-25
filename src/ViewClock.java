import java.util.Random;

import com.sun.j3d.loaders.objectfile.ObjectFile;
import com.sun.j3d.utils.geometry.ColorCube;

import javax.media.j3d.BranchGroup;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.Transform3D;
import javax.vecmath.*;

class ViewClock implements ViewInterface {
  private ViewClock(){}

  private TransformGroup tg;
  private Transform3D tf;
  public ViewClock(BranchGroup parentGroup, double defaultPoint){
    this.rng = new Random(System.currentTimeMillis());

    this.maxChangeMilliSec = defaultMaxChangeMilliSec;
    this.minChangeMilliSec = defaultMinChangeMilliSec;
    this.lastChangedMillSec = 0;

    //ColorCube testCube = new ColorCube(0.1f);
    //tg = new TransformGroup();
    //tg.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
    //tg.addChild(testCube);
    tg = new TransformGroup();
    playerObj po = new playerObj("assets/arrow2_fix.obj", ObjectFile.RESIZE);
	tg.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
	tg.addChild(po.getSceneGroup());
    parentGroup.addChild(tg);

    rotValue = 0;
    speed = Math.PI * 2 / 30;
    tf = new Transform3D();
    tf.setTranslation(new Vector3d(defaultPoint, 0.0, 0.0));
    tg.setTransform(tf);
  }

  private boolean debugMode;
  public void setDebugMode(boolean debug){
    this.debugMode = debug;
  }

  public TransformGroup getTg(){
    return tg;
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

  private double rotValue;
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

    rotValue += speed;
    tf.rotY(rotValue);
    tg.setTransform(tf);
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

  private static final double speedRange = 1;
  private double speed;

  private void changeClockwise(){
    speed *= 2;    
  }

/**
 * if finished game, call this
 */
  public void finish(){

  }
}
