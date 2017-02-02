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
  private Transform3D tf_; // local TG for scaling, local position
  public ViewClock(BranchGroup parentGroup, double defaultPoint, double defaultSpeed){
    this.rng = new Random(System.currentTimeMillis());
    this.rndRange = rng.nextInt(30) + 40;

    this.minChangeMilliSec = defaultMinChangeMilliSec;
    this.lastChangedMillSec = 0;

    tg = new TransformGroup();
    ObjLoader po = new ObjLoader("assets/arrow2_fix.obj", ObjectFile.RESIZE);
	  tg.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
    tg.addChild(po.getTransformGroup());
    tf_ = new Transform3D();
    tf_.setTranslation(new Vector3d(0.0, defaultPoint, -0.6));
    po.getTransformGroup().setTransform(tf_);
    parentGroup.addChild(tg);

    rotValue = 0;
    speed = defaultSpeed;
    tf = new Transform3D();
    tf.setTranslation(new Vector3d(0.0, 0.0, 0.0));
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

  private static final int defaultMinChangeMilliSec = 3000;
  private int minChangeMilliSec;
  private long lastChangedMillSec;
  private Random rng;
  private int rndRange;

  private final static int minRnd = 40;
  private final static int maxRnd = 30;

  private double rotValue;
/**
 * deside whether change speed or not
 *
 * [+] algorithm example
 * currentTime is depend on systemclock to effect for bias
 * when currentTime % rndRange(minRnd ~ maxRnd) == 0, changeClockwise
 *
 * @param currentTime start proc time
 */
  public void clockwise(long currentTime) {
    if (this.lastChangedMillSec + this.minChangeMilliSec < currentTime && currentTime % rndRange == 0) {
      if (this.debugMode) {
        System.out.println("[+] changed speed of clockwise in " + (currentTime - this.lastChangedMillSec));
      }
      this.lastChangedMillSec = currentTime;
      this.rndRange = rng.nextInt(maxRnd) + minRnd;
      
      changeClockwise();
      recalcParams();
    }

    rotValue += speed;
    // rotValue %= Math.PI * 2;
    tf.rotY(rotValue);
    tg.setTransform(tf);
  }

  private final static double minParam = 0.95;

/**
 * recalc minChangeMilliSec
 * a_n = a_n-1 * params
 */
  private void recalcParams(){
    this.minChangeMilliSec = (int)(this.minChangeMilliSec * (this.minParam + this.rng.nextDouble() % 0.05));

    if (this.debugMode) {
      System.out.println("[+] recalced minChangeMilliSec = " + this.minChangeMilliSec);
    }
  }

  private static final double speedRange = 1;
  private double speed;

  private void changeClockwise(){
    // speed *= 2;    
  }

/**
 * if finished game, call this
 */
  public void finish(){

  }
}
