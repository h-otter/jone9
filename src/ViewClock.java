import java.util.Random;

import javax.media.j3d.Group;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Shape3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.Transform3D;
import javax.vecmath.*;

class ViewClock extends ViewInterface {
  private ViewClock(){}

  private TransformGroup tg;
  private Transform3D tf;
  private Transform3D tfScale; // local TG for scaling, local position
  private Shape3D collidingShape;
  private String collisonName = "";

  public ViewClock(Group parentGroup, double defaultPoint, double defaultSpeed, double defaultRot){
    super("assets/arrow2_fix.obj", "needle");

    this.rng = new Random(System.currentTimeMillis());
    this.rndRange = rng.nextInt(30) + 40;
    this.changeTimes = 0;
    this.minChangeMilliSec = defaultMinChangeMilliSec;
    this.lastChangedMillSec = 0;
    this.finished = false;

    tg = new TransformGroup();
    tg.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
    tg.addChild(po.getTransformGroup());
    tfScale = new Transform3D();
    tfScale.setTranslation(new Vector3d(0.0, defaultPoint, -0.6));
    po.getTransformGroup().setTransform(tfScale);
    parentGroup.addChild(tg);

    tf = new Transform3D();
    this.speed = defaultSpeed;
    this.rotValue = defaultRot;
    tf.rotY(rotValue);
    tg.setTransform(tf);
    tf.setTranslation(new Vector3d(0.0, 0.0, 0.0));
    tg.setTransform(tf);
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
    if (this.finished){
      return;
    }
    
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

  private static final double maxSpeed = 2 * Math.PI / 30;
  private int changeTimes;
  private double speed;

/**
 * change speed randomly
 *
 * [*] algorithm
 * 0 <= random speed <= this.maxSpeed / 1.5
 * 0 <= random speed <= this.maxSpeed / (Math.pow(2, -changeTimes) + 1)
 * 0 <= random speed <= this.maxSpeed
 * speed converge to maxSpeed in about 15 times changed
 */
  private void changeClockwise(){
    speed = rng.nextDouble() % (this.maxSpeed / (Math.pow(2, -changeTimes) + 1));
    if (rng.nextInt(2) == 1){
      this.speed *= -1;
    }
    changeTimes++;

    if (this.debugMode) {
      System.out.println("[+] speed = " + this.speed);
    }
  }

  private boolean finished;
/**
 * if finished game, call this
 */
  public void finish(){
    if (this.debugMode){
      System.out.println("[+] Game is finished.");
    }
    this.speed = 0;
    this.finished = true;
  }
}
