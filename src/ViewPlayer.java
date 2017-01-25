import com.sun.j3d.utils.geometry.ColorCube;

import javax.media.j3d.BranchGroup;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.Transform3D;
import javax.vecmath.*;

class ViewPlayer implements ViewInterface {
/**
 * disabled default constructor
 */
  private ViewPlayer(){}

/**
 * how far from center of clock
 */
  private static double defaultLength = 1.0;

  TransformGroup tg;
  Transform3D tf;
  public ViewPlayer(TransformGroup parentGroup){
    this.jumpStatus = 0;
    this.distance = 0;

    ColorCube testCube = new ColorCube(0.1f);
    tg = new TransformGroup();
    tg.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
    tg.addChild(testCube);
    parentGroup.addChild(tg);

    tf = new Transform3D();
    tf.set(new Vector3d(0, 0.0, defaultLength));
    tg.setTransform(tf);
  }

  private boolean debugMode;
  public void setDebugMode(boolean debug){
    this.debugMode = debug;
  }

/**
 * fall with this formula when jumped
 * y = graphA * (jumpStatus - jumpStatusError) ^ 2 + graphB
 */
  private final static double graphA = -0.7 / 1000;
  private double graphB;
  private double jumpStatusError;  

  @Override
  public void running(long currentTime){
    if (this.jumpStatus > 0){
      this.jumpStatus--;
      // this.distance -= this.defaultDistance;
      this.distance = this.graphA * Math.pow(this.jumpStatus - this.jumpStatusError, 2) + this.graphB;
      if (this.jumpStatus == 0){
        this.distance = 0;
      }
      this.tf.set(new Vector3d(0.0, distance, defaultLength));
      this.tg.setTransform(tf);

      if (debugMode) {
        System.out.println("[+] jumped status = " + this.jumpStatus);
      }
    }
  }

/**
 * jumpStatus: not jumping = 0
 *             jumping > 0
 *             jumped < 0
 */
  private int jumpStatus;

/**
 * player jump distance per frame
 */
  private double distance;
  private final static double defaultDistance = 0.03;
  private final static int jumpMaxFrames = 20;  

/**
 * if key pressed, this method is called
 * @return jumpStatus
 */
  public int jump(){
    if (this.jumpStatus > 0){
      return this.jumpStatus;
    }
    if (this.jumpStatus < -jumpMaxFrames){
      this.changeJumpStatus();
      return this.jumpStatus;
    }
    
    this.distance += this.defaultDistance;
    this.tf.set(new Vector3d(0.0, distance, defaultLength));
    this.tg.setTransform(tf);
    this.jumpStatus--;

    if (this.debugMode) {
      System.out.println("[+] jumping status = " + this.jumpStatus);
    }

    return jumpStatus;
  }

/**
 * if key released, this method is called
 */
  public int jumped(){
    if (this.jumpStatus < 0){
      this.changeJumpStatus();
      System.out.println("[*] graphA = " + this.graphA + ", graphB = " + this.graphB);
    }

    return jumpStatus;
  }

/**
 * recalc formula of falling
 */
  private final static double jumpSpan = 0.05;
  private void changeJumpStatus(){
    this.graphB = this.distance + this.jumpSpan;
    this.jumpStatusError = Math.sqrt(-(this.jumpSpan + this.distance) / this.graphA);
    this.jumpStatus = (int)(Math.sqrt(-this.jumpSpan / this.graphA) + Math.sqrt(-(this.jumpSpan + this.distance) / this.graphA));
  }

  public void finish(){

  }
}
